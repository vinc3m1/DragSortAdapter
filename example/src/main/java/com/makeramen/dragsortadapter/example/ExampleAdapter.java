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

package com.makeramen.dragsortadapter.example;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.makeramen.dragsortadapter.DragSortAdapter;
import com.makeramen.dragsortadapter.NoForegroundShadowBuilder;
import com.makeramen.dragsortadapter.example.util.EnglishNumberToWords;
import java.util.List;

public class ExampleAdapter extends DragSortAdapter<ExampleAdapter.MainViewHolder> {

  public static final String TAG = ExampleAdapter.class.getSimpleName();

  private final List<Integer> data;

  public ExampleAdapter(RecyclerView recyclerView, List<Integer> data) {
    super(recyclerView);
    this.data = data;
  }

  @Override public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_example, parent, false);
    MainViewHolder holder = new MainViewHolder(view);
    view.setOnClickListener(holder);
    view.setOnLongClickListener(holder);
    return holder;
  }

  @Override public void onBindViewHolder(final MainViewHolder holder, final int position) {
    int itemId = data.get(position);
    holder.text.setText(EnglishNumberToWords.convert(itemId));
    // NOTE: check for getDraggingId() match to set an "invisible space" while dragging
    holder.container.setVisibility(getDraggingId() == itemId ? View.INVISIBLE : View.VISIBLE);
    holder.container.postInvalidate();
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

  @Override public boolean move(int fromPosition, int toPosition) {
    data.add(toPosition, data.remove(fromPosition));
    return true;
  }

  class MainViewHolder extends DragSortAdapter.ViewHolder implements
      View.OnClickListener, View.OnLongClickListener {

    @InjectView(R.id.container) ViewGroup container;
    @InjectView(R.id.text) TextView text;

    public MainViewHolder(View itemView) {
      super(itemView);
      ButterKnife.inject(this, itemView);
    }

    @Override public void onClick(@NonNull View v) {
      Log.d(TAG, text.getText() + " clicked!");
    }

    @Override public boolean onLongClick(@NonNull View v) {
      startDrag();
      return true;
    }

    @Override public View.DragShadowBuilder getShadowBuilder(View itemView, Point touchPoint) {
      return new NoForegroundShadowBuilder(itemView, touchPoint);
    }
  }
}
