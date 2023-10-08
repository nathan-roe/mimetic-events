#!/bin/bash


LOG_FILE="./mimetic-log.txt"
if [ "$1" == "-c" ] || [ "$1" == "--compile" ]; then
  pushd ./src/main/kotlin >/dev/null 2>&1 || exit;
  kotlinc-jvm -include-runtime -d MimeticEvents.jar *.kt;
  popd >/dev/null 2>&1 || exit;

  pushd ./src/main/jni >/dev/null 2>&1 || exit;
  gcc keyevent.cpp -o libkeyevent.so -shared -fPIC \
  -I /usr/lib/jvm/java-11-openjdk-amd64/include \
  -I /usr/lib/jvm/java-11-openjdk-amd64/include/linux;
  popd >/dev/null 2>&1 || exit;

  mkdir -p ./build/kotlin/dist >/dev/null 2>&1;
  mv ./src/main/kotlin/MimeticEvents.jar ./build/kotlin/dist/ >/dev/null 2>&1;
  mv ./src/main/jni/libkeyevent.so ./build/kotlin/dist/ >/dev/null 2>&1;
fi

pushd ./build/kotlin/dist >/dev/null 2>&1 || exit
sudo java -jar -Djava.library.path=. MimeticEvents.jar
cat $LOG_FILE
popd >/dev/null 2>&1 || exit