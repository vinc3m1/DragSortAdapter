package com.makeramen.rvdnd;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

  private final Toast mToast;

  final List<String> data = new ArrayList<String>() {{
    add("one");
    add("two");
    add("three");
    add("four");
    add("five");
    add("six");
    add("eight");
    add("nine");
    add("ten");
    add("eleven");
    add("twelve");
    add("thirteen");
    add("fourteen");
    add("fifteen");
    add("sixteen");
    add("seveteen");
    add("eighteen");
    add("nineteen");
    add("twenty");
  }};

  public MainAdapter(Toast toast) {
    super();
    mToast = toast;
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
    holder.text.setText(data.get(position));
  }

  @Override public int getItemCount() {
    return data.size();
  }

  class MainViewHolder extends RecyclerView.ViewHolder implements
      View.OnClickListener, View.OnLongClickListener {

    @InjectView(R.id.text) TextView text;

    public MainViewHolder(View itemView) {
      super(itemView);
      ButterKnife.inject(this, itemView);
    }

    @Override public void onClick(View v) {
      mToast.setText(data.get(getPosition()) + " clicked!");
      mToast.show();
    }

    @Override public boolean onLongClick(View v) {
      mToast.setText(data.get(getPosition()) + " long clicked!");
      mToast.show();
      return true;
    }
  }
}
