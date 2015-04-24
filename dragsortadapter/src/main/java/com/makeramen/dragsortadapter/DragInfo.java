package com.makeramen.dragsortadapter;

import android.graphics.Point;
import android.graphics.PointF;

final class DragInfo {
  private final long itemId;
  private final Point shadowSize;
  private final Point shadowTouchPoint;
  private final PointF dragPoint;

  public DragInfo(long itemId, Point shadowSize, Point shadowTouchPoint, PointF dragPoint) {
    this.itemId = itemId;
    this.shadowSize = new Point(shadowSize);
    this.shadowTouchPoint = new Point(shadowTouchPoint);
    this.dragPoint = dragPoint;
  }

  long itemId() {
    return itemId;
  }

  boolean shouldScrollLeft() {
    return dragPoint.x < shadowTouchPoint.x;
  }

  boolean shouldScrollRight(int parentWidth) {
    return dragPoint.x > (parentWidth - (shadowSize.x - shadowTouchPoint.x));
  }

  boolean shouldScrollUp() {
    return dragPoint.y < shadowTouchPoint.y;
  }

  boolean shouldScrollDown(int parentHeight) {
    return dragPoint.y > (parentHeight - (shadowSize.y - shadowTouchPoint.y));
  }

  void setDragPoint(float x, float y) {
    dragPoint.set(x, y);
  }
}
