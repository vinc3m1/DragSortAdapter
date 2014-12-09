/*
 * Copyright (C) 2014 Vincent Mi
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
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

public abstract class DragSortAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> implements
    View.OnDragListener {

  private final int SCROLL_AMOUNT = (int) (15 / Resources.getSystem().getDisplayMetrics().density);

  private long draggingId = RecyclerView.NO_ID;
  private final Handler debounceHandler = new Handler(Looper.getMainLooper());
  private PointF debouncePoint = null;
  private final PointF targetPoint = new PointF();
  private final PointF lastPoint = new PointF(); // used to continue edge scrolling

  public abstract int getPositionForId(long id);

  public abstract void move(int fromPosition, int toPosition);

  public void onDrop() { }

  public long getDraggingId() {
    return draggingId;
  }

  @Override public boolean onDrag(View v, DragEvent event) {
    if (v instanceof RecyclerView) {
      final RecyclerView recyclerView = (RecyclerView) v;
      final long itemId;
      try {
        itemId = (long) event.getLocalState();
      } catch (NullPointerException e) {
        throw new IllegalArgumentException(
            "startDrag must be called with myLocalState that is a long of value getItemId()");
      }

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

          if (toPosition > 0 && fromPosition != toPosition) {
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
                    move(fromPosition, toPosition);
                    notifyItemMoved(fromPosition, toPosition);
                  }

                  // reset so we know to attach listener again next time
                  debouncePoint = null;
                }
              });
            }
          }

          recyclerView.setOnScrollListener(mScrollListener);
          lastPoint.set(x, y);
          handleScroll(recyclerView, x, y);
          break;

        case DragEvent.ACTION_DRAG_ENDED:
          draggingId = RecyclerView.NO_ID;
          targetPoint.set(0, 0);
          lastPoint.set(0, 0);

          // queue up the show animation until after all move animations are finished

          recyclerView.getItemAnimator()
              .isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
                @Override public void onAnimationsFinished() {
                  int position = getPositionForId(itemId);

                  RecyclerView.ViewHolder vh = recyclerView.findViewHolderForItemId(itemId);
                  if (vh != null && vh.getPosition() != position) {
                    // if positions don't match, there's still an outstanding move animation
                    // so we reschedule the notifyItemChanged until after that
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
    }
    return true;
  }

  private void handleScroll(RecyclerView recyclerView) {
    if (!lastPoint.equals(0, 0)) {
      handleScroll(recyclerView, lastPoint.x, lastPoint.y);
    }
  }

  private void handleScroll(RecyclerView recyclerView, float x, float y) {
    if (y < 200) {
      // scroll up
      debounceHandler.removeCallbacksAndMessages(null);
      debouncePoint = null;
      recyclerView.scrollBy(0, -SCROLL_AMOUNT);
    } else if (y > recyclerView.getHeight() - 200) {
      // scroll down
      debounceHandler.removeCallbacksAndMessages(null);
      debouncePoint = null;
      recyclerView.scrollBy(0, SCROLL_AMOUNT);
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
      switch (newState) {
        case RecyclerView.SCROLL_STATE_IDLE:
          break;
        case RecyclerView.SCROLL_STATE_DRAGGING:
        case RecyclerView.SCROLL_STATE_SETTLING:
          break;
      }
    }
  };
}
