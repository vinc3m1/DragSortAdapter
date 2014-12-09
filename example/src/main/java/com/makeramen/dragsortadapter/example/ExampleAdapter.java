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

package com.makeramen.dragsortadapter.example;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.makeramen.dragsortadapter.DragSortAdapter;
import com.makeramen.dragsortadapter.example.util.EnglishNumberToWords;
import java.util.List;

public class ExampleAdapter extends DragSortAdapter<ExampleAdapter.MainViewHolder> {

  private final List<Integer> data;

  public ExampleAdapter(List<Integer> data) {
    super();
    this.data = data;
    setHasStableIds(true); // IMPORTANT: required for drag and drop
  }

  @Override public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_example, parent, false);
    MainViewHolder holder = new MainViewHolder(view);
    view.setOnClickListener(holder);
    view.setOnLongClickListener(holder);
    return holder;
  }

  @Override public void onBindViewHolder(MainViewHolder holder, int position) {
    int itemId = data.get(position);
    holder.text.setText(EnglishNumberToWords.convert(itemId));
    // NOTE: check for getDraggingId() match to set an "invisible space" while dragging
    holder.cardView.setVisibility(getDraggingId() == itemId ? View.INVISIBLE : View.VISIBLE);
  }

  @Override public long getItemId(int position) {
    return data.get(position);
  }

  @Override public int getItemCount() {
    return data.size();
  }

  @Override public int getPositionForId(long id) {
    return data.indexOf((int) id);
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
