DragSortAdapter
=====

Drag and Drop adapter implementation for RecyclerView. Targeted to support any LayoutManager
and ItemAnimator.

**Note: This is an advanced library meant to be flexible and customizable which leads to more complexity in integration. It is not meant to be a simple drop-in.** If you need even more customization I suggest exploring the source and copying relevant code that you need.

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
// this function should be reasonable performant as it gets called a lot on the UI thread
public abstract int getPositionForId(long id);
  
// this needs to re-order the positions **live** during dragging
public abstract boolean move(int fromPosition, int toPosition);

// not required but you probably want to override this to save the re-ordering after drop event
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
