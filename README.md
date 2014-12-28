DragSortAdapter
=====

Drag and Drop adapter implementation for RecyclerView. Targeted to support any LayoutManager
and ItemAnimator.

Still work in progress, API may change.

Usage
-----

Add Snapshot repository and add to dependencies:
```
repositories {
  maven {
       url 'https://oss.sonatype.org/content/repositories/snapshots/'
  }
}

dependencies {
  compile 'com.makeramen:dragsortadapter:0.9.0-SNAPSHOT'
}
```


Set in code:
```java
recyclerView.setAdapter(new ExampleAdapter(recyclerView, data));
```