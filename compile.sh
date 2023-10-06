#!/bin/bash
pushd ./src/main/kotlin
kotlinc-jvm -include-runtime -d KeyEventHandler.jar *.kt
popd

pushd ./src/main/JNI
gcc keyevent.c -o libkeyevent.so -shared -fPIC \
  -I /usr/lib/jvm/java-11-openjdk-amd64/include \
  -I /usr/lib/jvm/java-11-openjdk-amd64/include/linux
popd

mkdir -p ./build/kotlin/dist
mv ./src/main/kotlin/KeyEventHandler.jar ./build/kotlin/dist/
mv ./src/main/JNI/libkeyevent.so ./build/kotlin/dist/

pushd ./build/kotlin/dist
sudo java -jar -Djava.library.path=. KeyEventHandler.jar
popd