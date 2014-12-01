package com.makeramen.rvdnd;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.makeramen.rvdnd.util.EnglishNumberToWords;
import java.util.List;

public class ExampleAdapter extends DraggableAdapter<ExampleAdapter.MainViewHolder> {

  private final List<Integer> data;

  public ExampleAdapter(List<Integer> data) {
    super();
    this.data = data;
    setHasStableIds(true); // required for drag and drop
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
    holder.text.setText(EnglishNumberToWords.convert(itemId));
    holder.cardView.setVisibility(getDraggingId() == itemId ? View.INVISIBLE : View.VISIBLE);
  }

  @Override public long getItemId(int position) {
    return data.get(position);
  }

  @Override public int getItemCount() {
    return data.size();
  }

  @Override public void move(int fromPosition, int toPosition) {
    data.add(toPosition, data.remove(fromPosition));
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
      Log.d("ExampleAdapter", text.getText() + " clicked!");
    }

    @Override public boolean onLongClick(View v) {
      // IMPORTANT: must use getItemId() for myLocalState
      v.startDrag(null, new View.DragShadowBuilder(v), getItemId(), 0);
      return true;
    }
  }
}
