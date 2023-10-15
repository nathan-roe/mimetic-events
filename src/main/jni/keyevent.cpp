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
#include <fstream>
#include <iostream>
#include <cstdio>
#include "KeyEventHandler.h"

using namespace std;

typedef struct keyevent {
    long event_time;
    int key_code;
    int key_state;
} KeyEvent;

static const char* LOG_FILE = "mimetic-log.txt";
static long start_time = 0;
static bool running = true;
static int keyboard_fd;
static const char* user_input;
static vector<KeyEvent> keyevents;

void log_event(const char* log) {
    std::ofstream fs(LOG_FILE);
    if(!fs) { exit(1); }
    fs<<log<<"\n";
    fs.close();
}

void capture_key_events(JNIEnv *env) {
    struct input_event event;
    struct timeval stop;

    if ((keyboard_fd = open("/dev/input/event1", O_RDONLY)) < 0) {
        fprintf(stderr, "\nUnable to read from the device\n");
        exit(2);
    }

    jclass handle_cls = env->FindClass("KeyEventHandler");
    jfieldID capturing_key_id = env->GetFieldID(handle_cls, "capturingKeys", "Z");

    while (running) {
        read(keyboard_fd, &event, sizeof(event));

        if (event.type == EV_KEY && event.value < 2) {
            gettimeofday(&stop, NULL);
            long event_time = stop.tv_sec * 1000 + stop.tv_usec / 1000;
            KeyEvent kev = { .event_time = event_time - start_time, .key_code = event.code, .key_state = event.value };
            keyevents.push_back(kev);
        }

        log_event("Value of capture key field: ");
        log_event(((bool) env->GetBooleanField(handle_cls, capturing_key_id)) ? "true" : "false");
        if(!((bool) env->GetBooleanField(handle_cls, capturing_key_id))) {
            break;
        }
    }

    log_event("Closing keyboard");
    close(keyboard_fd);
}

void construct_jni_keyevents(JNIEnv *env, jobject obj) {
    jclass handle_cls = env->FindClass("KeyEventHandler");
    jfieldID event_fld = env->GetFieldID(handle_cls, "keyEvents", "[LKeyEvent;");
    if(handle_cls == NULL) {
        log_event("handle_cls not found, exiting with code 3");
        exit(3);
    }
    if(event_fld == NULL) {
        log_event("event_fld not found, exiting with code 4");
        exit(4);
    }
    log_event("handle_cls and event_fld set");

    jclass event_cls = env->FindClass("KeyEvent");
    jmethodID event_ctr = env->GetMethodID(event_cls, "<init>", "(IIJ)V");
    if(event_cls == NULL || event_ctr == NULL) {
        log_event("event_cls/event_ctr not found, exiting with code 5");
        exit(5);
    }
    log_event("event_cls and event_ctr set");

    jobjectArray jni_keyevents = env->NewObjectArray(keyevents.size(), event_cls, nullptr);
    log_event("Setting jni_keyevents...");

    for (int i = 0; i < keyevents.size(); i++) {
        KeyEvent kev = keyevents[i];
        // Ordered jclass constructor args are keyCode, keyState, and eventTime
        jobject event = env->NewObject(event_cls, event_ctr, kev.key_code, kev.key_state, kev.event_time);
        env->SetObjectArrayElement(jni_keyevents, i, event);
        env->DeleteLocalRef(event);
    }

    log_event("jni_keyevents successfully set");

    env->SetObjectField(obj, event_fld, jni_keyevents);
    log_event("event field updated");
}

JNIEXPORT void JNICALL Java_KeyEventHandler_captureEvents(JNIEnv *env, jobject obj, jlong jstart) {
    start_time = (long)jstart;
    capture_key_events(env);
}

JNIEXPORT void JNICALL Java_KeyEventHandler_retrieveKeyEvents(JNIEnv *env, jobject obj) {
    running = false;
    log_event("Running state set to false");
    construct_jni_keyevents(env, obj);
    log_event("Key retrieval was successful");
}