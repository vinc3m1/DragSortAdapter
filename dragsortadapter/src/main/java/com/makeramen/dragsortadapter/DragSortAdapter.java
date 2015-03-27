/*
 * Copyright (C) 2015 Vincent Mi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.makeramen.dragsortadapter;

import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import java.lang.ref.WeakReference;

public abstract class DragSortAdapter<VH extends DragSortAdapter.ViewHolder>
    extends RecyclerView.Adapter<VH> implements
    View.OnDragListener, RecyclerView.OnItemTouchListener {

  private static final String TAG = DragSortAdapter.class.getSimpleName();

  private final int SCROLL_AMOUNT = (int) (2 * Resources.getSystem().getDisplayMetrics().density);

  private final WeakReference<RecyclerView> recyclerViewRef;
  private long draggingId = RecyclerView.NO_ID;
  private final Handler debounceHandler = new Handler(Looper.getMainLooper());
  private PointF debouncePoint = null;
  private final PointF targetPoint = new PointF();
  private final PointF lastPoint = new PointF(); // used to continue edge scrolling
  private final Point lastTouchPoint = new Point(); // used to continue edge scrolling
  private DragInfo lastDragInfo;
  private int scrollState = RecyclerView.SCROLL_STATE_IDLE;

  public DragSortAdapter(RecyclerView recyclerView) {
    this.recyclerViewRef = new WeakReference<>(recyclerView);
    recyclerView.setOnDragListener(this);
    recyclerView.setOnScrollListener(mScrollListener);
    recyclerView.addOnItemTouchListener(this);
    setHasStableIds(true);
  }

  /**
   * This should be reasonably performant as it gets called a lot on the UI thread.
   *
   * @return position of the item with the given id
   */
  public abstract int getPositionForId(long id);

  /**
   * This is called during the dragging event, the actual positions of the views and data need to
   * change in the adapter for the drag animations to look correct.
   *
   * @return true if the position can be moved from fromPosition to toPosition
   */
  public abstract boolean move(int fromPosition, int toPosition);

  /**
   * Called after a drop event, override to save changes after drop event.
   */
  public void onDrop() { }

  /**
   * You probably want to use this to set the currently dragging item to blank while it's being
   * dragged
   *
   * @return the id of the item currently being dragged or {@link RecyclerView.NO_ID } if not being
   * dragged
   */
  public long getDraggingId() {
    return draggingId;
  }

  @Override public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
    lastTouchPoint.set((int) e.getX(), (int) e.getY());
    return false;
  }

  @Override public void onTouchEvent(RecyclerView rv, MotionEvent e) { }

  public Point getLastTouchPoint() {
    return new Point(lastTouchPoint.x, lastTouchPoint.y);
  }

  @Override public boolean onDrag(View v, DragEvent event) {
    if (v != recyclerViewRef.get() || !(event.getLocalState() instanceof DragInfo)) {
      return false;
    }
    final RecyclerView recyclerView = (RecyclerView) v;
    final DragInfo dragInfo = (DragInfo) event.getLocalState();
    final long itemId = dragInfo.itemId;

    switch (event.getAction()) {
      case DragEvent.ACTION_DRAG_STARTED:
        draggingId = itemId;
        notifyItemChanged(recyclerView.findViewHolderForItemId(itemId).getPosition());
        break;

      case DragEvent.ACTION_DRAG_LOCATION:
        float x = event.getX();
        float y = event.getY();

        int fromPosition = getPositionForId(itemId);
        int toPosition = -1;

        View child = recyclerView.findChildViewUnder(event.getX(), event.getY());
        if (child != null) {
          toPosition = recyclerView.getChildViewHolder(child).getPosition();
        }

        if (toPosition >= 0 && fromPosition != toPosition) {
          RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();

          boolean attach = false;
          if (debouncePoint == null) {
            attach = true;
            debouncePoint = new PointF();
          }
          debouncePoint.x = x;
          debouncePoint.y = y;

          if (attach) {
            animator.isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
              @Override public void onAnimationsFinished() {
                if (debouncePoint == null) {
                  return;
                }

                int fromPosition = getPositionForId(itemId);

                View child = recyclerView.findChildViewUnder(debouncePoint.x, debouncePoint.y);
                if (child != null) {
                  int toPosition = recyclerView.getChildViewHolder(child).getPosition();
                  if (move(fromPosition, toPosition)) {

                    if (fromPosition == 0 || toPosition == 0) {
                      // fix for weird scrolling when animating first item
                      recyclerView.scrollToPosition(0);
                    }

                    notifyItemMoved(fromPosition, toPosition);
                  }
                }

                // reset so we know to attach listener again next time
                debouncePoint = null;
              }
            });
          }
        }

        recyclerView.setOnScrollListener(mScrollListener);
        lastPoint.set(x, y);
        lastDragInfo = dragInfo;
        handleScroll(recyclerView, x, y, dragInfo);
        break;

      case DragEvent.ACTION_DRAG_ENDED:
        draggingId = RecyclerView.NO_ID;
        targetPoint.set(0, 0);
        lastPoint.set(0, 0);
        lastDragInfo = null;

        // queue up the show animation until after all move animations are finished

        recyclerView.getItemAnimator()
            .isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
              @Override public void onAnimationsFinished() {
                int position = getPositionForId(itemId);

                RecyclerView.ViewHolder vh = recyclerView.findViewHolderForItemId(itemId);
                if (vh != null && vh.getPosition() != position) {
                  // if positions don't match, there's still an outstanding move animation
                  // so we try to reschedule the notifyItemChanged until after that
                  recyclerView.post(new Runnable() {
                    @Override public void run() {
                      recyclerView.getItemAnimator().isRunning(
                          new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
                            @Override public void onAnimationsFinished() {
                              notifyItemChanged(getPositionForId(itemId));
                            }
                          });
                    }
                  });
                } else {
                  notifyItemChanged(getPositionForId(itemId));
                }
              }
            });
        break;

      case DragEvent.ACTION_DROP:
        onDrop();
        break;

      case DragEvent.ACTION_DRAG_ENTERED:
        // probably not used?
        break;
      case DragEvent.ACTION_DRAG_EXITED:
        // TODO edge scrolling
        break;
    }
    return true;
  }

  private void handleScroll(RecyclerView recyclerView) {
    if (scrollState != RecyclerView.SCROLL_STATE_IDLE) {
      return;
    }
    if (!lastPoint.equals(0, 0) && lastDragInfo != null) {
      handleScroll(recyclerView, lastPoint.x, lastPoint.y, lastDragInfo);
    }
  }

  private void handleScroll(RecyclerView rv, float x, float y, DragInfo dragInfo) {

    if (rv.canScrollVertically(-1) && y < dragInfo.shadowTouchPoint.y) {
      // scroll up
      debounceHandler.removeCallbacksAndMessages(null);
      debouncePoint = null;
      rv.scrollBy(0, -SCROLL_AMOUNT);
    } else if (rv.canScrollVertically(1)
        && y > (rv.getHeight() - (dragInfo.shadowSize.y - dragInfo.shadowTouchPoint.y))) {
      // scroll down
      debounceHandler.removeCallbacksAndMessages(null);
      debouncePoint = null;
      rv.scrollBy(0, SCROLL_AMOUNT);
    }
  }

  private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
    @Override public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
      recyclerView.post(new Runnable() {
        @Override public void run() {
          handleScroll(recyclerView);
        }
      });
    }

    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      scrollState = newState;
      switch (newState) {
        case RecyclerView.SCROLL_STATE_IDLE:
          handleScroll(recyclerView);
          break;
        case RecyclerView.SCROLL_STATE_DRAGGING:
        case RecyclerView.SCROLL_STATE_SETTLING:
          break;
      }
    }
  };

  private static final class DragInfo {
    final long itemId;
    final Point shadowSize;
    final Point shadowTouchPoint;

    public DragInfo(long itemId, Point shadowSize, Point shadowTouchPoint) {
      this.itemId = itemId;
      this.shadowSize = new Point(shadowSize);
      this.shadowTouchPoint = new Point(shadowTouchPoint);
    }
  }

  public static abstract class ViewHolder extends RecyclerView.ViewHolder {

    final DragSortAdapter<?> adapter;

    public ViewHolder(DragSortAdapter<?> dragSortAdapter, View itemView) {
      super(itemView);
      this.adapter = dragSortAdapter;
    }

    public void startDrag() {
      Point touchPoint = adapter.getLastTouchPoint();
      touchPoint.x = Math.max(touchPoint.x - (int) itemView.getX(), 0);
      touchPoint.y = Math.max(touchPoint.y - (int) itemView.getY(), 0);
      startDrag(getShadowBuilder(itemView, touchPoint));
    }

    public View.DragShadowBuilder getShadowBuilder(View itemView, Point touchPoint) {
      return new DragSortShadowBuilder(itemView, touchPoint);
    }

    public void startDrag(View.DragShadowBuilder dragShadowBuilder) {

      Point shadowSize = new Point();
      Point shadowTouchPoint = new Point();
      dragShadowBuilder.onProvideShadowMetrics(shadowSize, shadowTouchPoint);

      itemView.startDrag(null, dragShadowBuilder,
          new DragInfo(getItemId(), shadowSize, shadowTouchPoint), 0);

      adapter.notifyItemChanged(getPosition());
    }
  }
}
