# MapboxAnnotationLeak
This repository sample app demonstrates a memory leak when using annotations

# Usage
- add the `MAPBOX_DOWNLOADS_TOKEN` as property to a file called `local.properties` in your root folder
- Run a debug build and add + remove the map fragments (including the annotationview) a couple of times until leakCanary reports a memory leak.
