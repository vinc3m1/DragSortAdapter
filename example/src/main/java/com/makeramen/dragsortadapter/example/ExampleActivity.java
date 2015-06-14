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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.ArrayList;
import java.util.List;

public class ExampleActivity extends AppCompatActivity {

  @InjectView(android.R.id.list) RecyclerView recyclerView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_example);
    ButterKnife.inject(this);

    int dataSize = 100;
    List<Integer> data = new ArrayList<>(dataSize);
    for (int i = 1; i < dataSize + 1; i++) {
      data.add(i);
    }

    recyclerView.setAdapter(new ExampleAdapter(recyclerView, data));
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.example, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_layout_grid:
        item.setChecked(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        break;
      case R.id.action_layout_linear:
        item.setChecked(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        break;
      case R.id.action_layout_staggered:
        item.setChecked(true);
        recyclerView.setLayoutManager(
            new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
