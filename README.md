DragSortAdapter
=====

Drag and Drop adapter implementation for RecyclerView. Targeted to support any LayoutManager
and ItemAnimator.

Still work in progress, API may change.

Usage
-----

Add Snapshot repository and add to dependencies:
```
dependencies {
  compile 'com.makeramen:dragsortadapter:1.0.0'
}
```

Override `DragSortAdapter<T extends DragSortAdapter.ViewHolder>`, see [ExampleAdapter.java](https://github.com/vinc3m1/DragSortAdapter/blob/master/example/src/main/java/com/makeramen/dragsortadapter/example/ExampleAdapter.java):
required functions:
```java
  public abstract int getPositionForId(long id);

  public abstract void move(int fromPosition, int toPosition);

  public void onDrop() { }
```

Set adapter in code:
```java
recyclerView.setAdapter(new ExampleAdapter(recyclerView));
```

Call `startDrag()` when you want to start dragging (e.g. [onLongClick](https://github.com/vinc3m1/DragSortAdapter/blob/master/example/src/main/java/com/makeramen/dragsortadapter/example/ExampleAdapter.java#L93)):
```java
    @Override public boolean onLongClick(@NonNull View v) {
      startDrag();
      return true;
    }
```

Works with any LayoutManager
```java
recyclerView.setLayoutManager(new LinearLayoutManager(this));
recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
```


And any ItemAnimator:
```java
recyclerView.setItemAnimator(new DefaultItemAnimator());
```
