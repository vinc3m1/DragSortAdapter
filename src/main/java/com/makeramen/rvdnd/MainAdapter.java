package com.makeramen.rvdnd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
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

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

  private final Toast mToast;

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
    mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
  }

  @Override public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_main, parent, false);
    MainViewHolder holder = new MainViewHolder(view);
    view.setOnClickListener(holder);
    view.setOnLongClickListener(holder);
    view.setOnDragListener(holder);
    return holder;
  }

  @Override public void onBindViewHolder(MainViewHolder holder, int position) {
    holder.text.setText(NUMBERS[data.get(position)]);
  }

  @Override public long getItemId(int position) {
    return data.get(position);
  }

  @Override public int getItemCount() {
    return data.size();
  }

  class MainViewHolder extends RecyclerView.ViewHolder implements
      View.OnClickListener, View.OnLongClickListener, View.OnDragListener {

    @InjectView(R.id.text) TextView text;

    public MainViewHolder(View itemView) {
      super(itemView);
      ButterKnife.inject(this, itemView);
    }

    @Override public void onClick(View v) {
      mToast.setText(text.getText() + " clicked!");
      mToast.show();
    }

    @Override public boolean onLongClick(View v) {
      mToast.setText(text.getText() + " long clicked!");
      mToast.show();
      v.startDrag(null, new View.DragShadowBuilder(), null, 0);
      return true;
    }

    @Override public boolean onDrag(View v, DragEvent event) {
      Log.d("vmi", "view: " + v + "  event: " + event);

      switch (event.getAction()) {
        case DragEvent.ACTION_DRAG_STARTED:
        case DragEvent.ACTION_DRAG_ENTERED:
        case DragEvent.ACTION_DRAG_LOCATION:
        case DragEvent.ACTION_DRAG_EXITED:
        case DragEvent.ACTION_DROP:
      }
      return true;
    }
  }

  /**
   * Creates the hover cell with the appropriate bitmap and of appropriate
   * size. The hover cell's BitmapDrawable is drawn on top of the bitmap every
   * single time an invalidate call is made.
   */
  private BitmapDrawable getAndAddHoverView(View v) {

    int w = v.getWidth();
    int h = v.getHeight();
    int top = v.getTop();
    int left = v.getLeft();


    Bitmap b = getBitmapFromView(v);
    BitmapDrawable drawable = new BitmapDrawable(v.getResources(), b);

    mHoverCellOriginalBounds = new Rect(left, top, left + w, top + h);
    mHoverCellCurrentBounds = new Rect(mHoverCellOriginalBounds);
    mHoverCellCurrentBounds.top -= mShadowSize;
    mHoverCellCurrentBounds.bottom += mShadowSize;
    drawable.setBounds(mHoverCellCurrentBounds);

    return drawable;
  }

  /** Returns a bitmap showing a screenshot of the view passed in. */
  private Bitmap getBitmapFromView(View v) {
    Bitmap bitmap =
        Bitmap.createBitmap(v.getWidth(), v.getHeight() /* + 2 * mShadowSize */, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);

    //Rect r1 = new Rect(0, 0, bitmap.getWidth(), mShadowSize);
    //Rect r2 = new Rect(0, v.getHeight() /*+ mShadowSize*/, bitmap.getWidth(), bitmap.getHeight());
    //
    //Paint paint1 = new Paint();
    //paint1.setShader(
    //    new LinearGradient(r1.left, r1.top, 0, r1.bottom, Color.TRANSPARENT, mShadowColor,
    //        Shader.TileMode.CLAMP)
    //);
    //Paint paint2 = new Paint();
    //paint2.setShader(
    //    new LinearGradient(r2.left, r2.top, 0, r2.bottom, mShadowColor, Color.TRANSPARENT,
    //        Shader.TileMode.CLAMP)
    //);

    //canvas.drawRect(r1, paint1);
    //canvas.drawRect(r2, paint2);
    //
    //canvas.translate(0, mShadowSize);
    v.draw(canvas);
    return bitmap;
  }
}
