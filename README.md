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

Override DragSortAdapter:
```
public class ExampleAdapter extends DragSortAdapter<ExampleAdapter.MainViewHolder> {


```


Set in code:
```java
recyclerView.setAdapter(new ExampleAdapter(recyclerView));
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