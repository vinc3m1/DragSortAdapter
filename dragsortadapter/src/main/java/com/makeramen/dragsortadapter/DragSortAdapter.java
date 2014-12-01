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

import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewConfiguration;

public abstract class DragSortAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> implements
    View.OnDragListener {

  private long draggingId = RecyclerView.NO_ID;
  private final Handler debounceHandler = new Handler(Looper.getMainLooper());
  private final PointF targetPoint = new PointF();

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
          Log.d("vmi", "drag started: " + itemId);
          draggingId = itemId;
          notifyItemChanged(recyclerView.findViewHolderForItemId(itemId).getPosition());
          break;

        case DragEvent.ACTION_DRAG_LOCATION:
          int touchSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();

          float x = event.getX();
          float y = event.getY();

          if (targetPoint.equals(0, 0)
              || PointF.length(targetPoint.x - x, targetPoint.y - y) > touchSlop) {
            targetPoint.set(x, y);
            debounceHandler.removeCallbacksAndMessages(null);
            debounceHandler.postDelayed(new Runnable() {
              @Override public void run() {
                if (targetPoint.equals(0, 0)) { return; }

                int fromPosition = recyclerView.findViewHolderForItemId(itemId).getPosition();

                View child = recyclerView.findChildViewUnder(targetPoint.x, targetPoint.y);
                if (child != null) {
                  int toPosition = recyclerView.getChildViewHolder(child).getPosition();
                  move(fromPosition, toPosition);
                  notifyItemMoved(fromPosition, toPosition);
                }

                targetPoint.set(0, 0);
              }
            }, recyclerView.getItemAnimator().getMoveDuration());
          }
          // TODO edge scrolling

          break;

        case DragEvent.ACTION_DRAG_ENDED:
          draggingId = RecyclerView.NO_ID;
          targetPoint.set(0, 0);

          // queue up the show animation until after all move animations are finished
          recyclerView.getItemAnimator().isRunning(
              new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
                @Override public void onAnimationsFinished() {
                  RecyclerView.ViewHolder vh = recyclerView.findViewHolderForItemId(itemId);
                  if (vh != null) { notifyItemChanged(vh.getPosition()); }
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
}
