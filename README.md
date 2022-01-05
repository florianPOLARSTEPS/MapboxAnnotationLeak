# MapboxAnnotationLeak
This repository sample app demonstrates a memory leak when using annotations

# Usage
- add the `MAPBOX_DOWNLOADS_TOKEN` as property to a file called `local.properties` in your root folder
- Run a debug build and add + remove the map fragments (including the annotationview) a couple of times until leakCanary reports a memory leak.

# Proof
https://user-images.githubusercontent.com/21218132/148228899-c99e6b91-25a5-4cf1-a39d-23fead6be94c.mp4

