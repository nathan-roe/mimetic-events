#!/bin/bash

pushd ./src/main/kotlin || exit
  kotlinc-jvm -include-runtime -d MimeticEvents.jar *.kt
popd || exit

pushd ./src/main/jni || exit
  gcc keyevent.cpp -o libkeyevent.so -shared -fPIC \
    -I /usr/lib/jvm/java-11-openjdk-amd64/include \
    -I /usr/lib/jvm/java-11-openjdk-amd64/include/linux
popd || exit

mkdir -p ./build/kotlin/dist
mv ./src/main/kotlin/MimeticEvents.jar ./build/kotlin/dist/
mv ./src/main/jni/libkeyevent.so ./build/kotlin/dist/

pushd ./build/kotlin/dist || exit
  sudo java -jar -Djava.library.path=. MimeticEvents.jar
popd || exit