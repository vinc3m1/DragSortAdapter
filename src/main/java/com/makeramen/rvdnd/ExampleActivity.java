package com.makeramen.rvdnd;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.ArrayList;
import java.util.List;

public class ExampleActivity extends ActionBarActivity {

  @InjectView(android.R.id.list) RecyclerView recyclerView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    ButterKnife.inject(this);

    int dataSize = 100;
    List<Integer> data = new ArrayList<>(dataSize);
    for (int i = 1; i < dataSize + 1; i++) {
      data.add(i);
    }

    ExampleAdapter adapter = new ExampleAdapter(data);
    recyclerView.setAdapter(adapter);
    // IMPORTANT: must set OnDragListener as adapter
    recyclerView.setOnDragListener(adapter);
    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
  }
}
