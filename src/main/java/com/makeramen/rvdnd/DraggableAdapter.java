package com.makeramen.rvdnd;

import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

public abstract class DraggableAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> implements
    View.OnDragListener {

  private long draggingId = RecyclerView.NO_ID;
  private PointF debouncePoint = null;

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
          int fromPosition = recyclerView.findViewHolderForItemId(itemId).getPosition();
          int toPosition = -1;

          View child = recyclerView.findChildViewUnder(event.getX(), event.getY());
          if (child != null) {
            toPosition = recyclerView.getChildViewHolder(child).getPosition();
          }

          if (toPosition > 0 && fromPosition != toPosition) {
            RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
            if (animator.isRunning()) {
              // currently animating, debounce move

              // only attach one listener at a time
              if (debouncePoint == null) {
                debouncePoint = new PointF();
                animator.isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
                  @Override public void onAnimationsFinished() {
                    if (debouncePoint == null) { return; }

                    int fromPosition = recyclerView.findViewHolderForItemId(itemId).getPosition();

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

              // we hold a Point because findChildViewUnder could be wrong during animation
              debouncePoint.x = event.getX();
              debouncePoint.y = event.getY();
            } else {
              // not animating, go ahead and move
              Log.d("vmi", "moving to position: " + toPosition);
              move(fromPosition, toPosition);
              notifyItemMoved(fromPosition, toPosition);

              // reset debouncer
              debouncePoint = null;
            }
          }
          // TODO edge scrolling

          break;

        case DragEvent.ACTION_DRAG_ENDED:
          draggingId = RecyclerView.NO_ID;
          debouncePoint = null;

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
          // probably not used
          break;
        case DragEvent.ACTION_DRAG_EXITED:
          // TODO edge scrolling
          break;
      }
    }
    return true;
  }
}
