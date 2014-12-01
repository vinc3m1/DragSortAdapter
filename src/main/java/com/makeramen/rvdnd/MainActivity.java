package com.makeramen.rvdnd;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

  @InjectView(android.R.id.list) RecyclerView recyclerView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    ButterKnife.inject(this);

    int dataSize = 20;
    List<Integer> data = new ArrayList<>(dataSize);
    for (int i = 1; i < dataSize + 1; i++) {
      data.add(i);
    }

    MainAdapter adapter = new MainAdapter(this, data);
    recyclerView.setAdapter(adapter);
    recyclerView.setOnDragListener(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
  }
}
