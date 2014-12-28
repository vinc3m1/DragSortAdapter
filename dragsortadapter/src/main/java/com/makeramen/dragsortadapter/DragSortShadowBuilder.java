package com.makeramen.dragsortadapter;

import android.graphics.Canvas;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

public class DragSortShadowBuilder extends View.DragShadowBuilder {

  public static final String TAG = DragSortShadowBuilder.class.getSimpleName();

  final Point touchPoint = new Point();

  public DragSortShadowBuilder(View view, Point touchPoint) {
    super(view);
    this.touchPoint.set(touchPoint.x, touchPoint.y);
  }

  @Override
  public void onProvideShadowMetrics(@NonNull Point shadowSize, @NonNull Point shadowTouchPoint) {
    final View view = getView();
    if (view != null) {
      shadowSize.set(view.getWidth(), view.getHeight());
      shadowTouchPoint.set(touchPoint.x, touchPoint.y);
    } else {
      Log.e(TAG, "Asked for drag thumb metrics but no view");
    }
  }

  @Override public void onDrawShadow(@NonNull Canvas canvas) {
    super.onDrawShadow(canvas);
  }
}
