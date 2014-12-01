package com.makeramen.rvdnd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder>
    implements View.OnDragListener {

  @NonNull private final Toast toast;
  @Nullable private Long draggingId = null;

  static final String[] NUMBERS = {
      "zero",
      "one",
      "two",
      "three",
      "four",
      "five",
      "six",
      "seven",
      "eight",
      "nine",
      "ten",
      "eleven",
      "twelve",
      "thirteen",
      "fourteen",
      "fifteen",
      "sixteen",
      "seventeen",
      "eighteen",
      "nineteen",
      "twenty"
  };

  final List<Integer> data = new ArrayList<Integer>() {{
    add(1);
    add(2);
    add(3);
    add(4);
    add(5);
    add(6);
    add(7);
    add(8);
    add(9);
    add(10);
    add(11);
    add(12);
    add(13);
    add(14);
    add(15);
    add(16);
    add(17);
    add(18);
    add(19);
    add(20);
  }};

  @SuppressLint("ShowToast")
  public MainAdapter(Context context) {
    super();
    setHasStableIds(true);
    this.toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
  }

  @Override public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_main, parent, false);
    MainViewHolder holder = new MainViewHolder(view);
    view.setOnClickListener(holder);
    view.setOnLongClickListener(holder);
    return holder;
  }

  @Override public void onBindViewHolder(MainViewHolder holder, int position) {
    int itemId = data.get(position);
    holder.text.setText(NUMBERS[itemId]);
    holder.cardView.setVisibility(draggingId != null && draggingId == itemId
        ? View.INVISIBLE
        : View.VISIBLE);
  }

  @Override public long getItemId(int position) {
    return data.get(position);
  }

  @Override public int getItemCount() {
    return data.size();
  }

  class MainViewHolder extends RecyclerView.ViewHolder implements
      View.OnClickListener, View.OnLongClickListener {

    @InjectView(R.id.card) CardView cardView;
    @InjectView(R.id.text) TextView text;

    public MainViewHolder(View itemView) {
      super(itemView);
      ButterKnife.inject(this, itemView);
    }

    @Override public void onClick(View v) {
      toast.setText(text.getText() + " clicked!");
      toast.show();
    }

    @Override public boolean onLongClick(View v) {
      toast.setText(text.getText() + " long clicked!");
      toast.show();
      v.startDrag(null, new MainDragShadowBuilder(v), getItemId(), 0);
      return true;
    }
  }

  // DRAG AND DROP METHODS & CLASSES BELOW

  @Override public boolean onDrag(View v, DragEvent event) {
    if (v instanceof RecyclerView) {
      RecyclerView recyclerView = (RecyclerView) v;
      long itemId = (long) event.getLocalState();

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
            Integer itemData = data.remove(fromPosition);
            data.add(toPosition, itemData);

            notifyItemMoved(fromPosition, toPosition);
            Log.d("vmi", "notifyItemMoved from:" + fromPosition + " to:" + toPosition);
          }
          break;

        case DragEvent.ACTION_DRAG_ENDED:
          Log.d("vmi", "dropped");
          draggingId = null;
          notifyItemChanged(recyclerView.findViewHolderForItemId(itemId).getPosition());
          break;

        case DragEvent.ACTION_DROP:
          // TODO
          break;

        case DragEvent.ACTION_DRAG_ENTERED:
          // probably not used
          break;
        case DragEvent.ACTION_DRAG_EXITED:
          // TODO probably used for edge scrolling
          break;
      }
    }
    return true;
  }

  static class MainDragShadowBuilder extends View.DragShadowBuilder {
    public MainDragShadowBuilder(View view) {
      super(view);
    }

    @Override public void onProvideShadowMetrics(@NonNull Point shadowSize,
        @NonNull Point shadowTouchPoint) {
      Log.d("vmi", "shadowSize: " + shadowSize + "  touchPoint: " + shadowTouchPoint);
      super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
    }
  }
}
