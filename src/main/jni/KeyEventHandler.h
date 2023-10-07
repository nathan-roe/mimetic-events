#include <jni.h>
#ifndef _Included_KeyEventHandler
#define _Included_KeyEventHandler
#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT void JNICALL Java_KeyEventHandler_captureEvents(JNIEnv *, jobject, jlong);
JNIEXPORT jobjectArray JNICALL Java_KeyEventHandler_retrieveKeyEvents(JNIEnv *, jobject, jlong);
#ifdef __cplusplus
}
#endif
#endif