#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <vector>
#include <unistd.h>
#include <linux/input.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <signal.h>
#include <errno.h>
#include <sys/time.h>
#include <iostream>
#include <cstdio>
#include "KeyEventHandler.h"

using namespace std;
using jstart = int_least64_t;

typedef struct keyevent {
    long event_time;
    int key_code;
    int key_state;
} KeyEvent;

static bool running = true;
static int keyboard_fd;
static const char* user_input;
static vector<KeyEvent> keyevents;

void captureKeyEvents() {
    struct input_event event;
    struct timeval start, stop;

    if ((keyboard_fd = open("/dev/input/event1", O_RDONLY)) < 0) {
        fprintf(stderr, "\nUnable to read from the device\n");
        exit(1);
    }

    gettimeofday(&start, NULL);

    while (running) {
        read(keyboard_fd, &event, sizeof(event));

        if (event.type == EV_KEY && event.value < 2) {
            gettimeofday(&stop, NULL);
            long event_time = (stop.tv_sec - start.tv_sec) * 1000000 + stop.tv_usec - start.tv_usec;
            KeyEvent kev = { .event_time = event_time, .key_code = event.code, .key_state = event.value };
            keyevents.push_back(kev);
        }
    }

    close(keyboard_fd);
}

jobjectArray construct_jni_keyevents(JNIEnv *env, jobject obj) {
    jclass handle_cls = env->FindClass("KeyEventHandler");
    jfieldID event_fld = env->GetFieldID(handle_cls, "keyEvents", "[LKeyEvent;");
    if(handle_cls == NULL || event_fld == NULL) {
        exit(2);
    }

    jclass event_cls = env->FindClass("KeyEvent");
    jmethodID event_ctr = env->GetMethodID(event_cls, "<init>", "(IIJ)V");
    if(event_cls == NULL || event_ctr == NULL) {
        exit(3);
    }

    jobjectArray jni_keyevents = env->NewObjectArray(keyevents.size(), event_cls, nullptr);

    for (int i = 0; i < keyevents.size(); i++) {
        KeyEvent kev = keyevents[i];
        // Ordered jclass constructor args are keyCode, keyState, and eventTime
        jobject event = env->NewObject(event_cls, event_ctr, kev.key_code, kev.key_state, kev.event_time);
        env->SetObjectArrayElement(jni_keyevents, i, event);
        env->DeleteLocalRef(event);
    }

    env->SetObjectField(obj, event_fld, jni_keyevents);
    return jni_keyevents;
}

JNIEXPORT void JNICALL Java_KeyEventHandler_captureEvents(JNIEnv *env, jobject obj, jlong jstart) {

    captureKeyEvents();
}

JNIEXPORT jobjectArray JNICALL Java_KeyEventHandler_retrieveKeyEvents(JNIEnv *env, jobject obj) {
    running = false;
    jobjectArray jni_keyevents = construct_jni_keyevents(env, obj);
    return jni_keyevents;
}