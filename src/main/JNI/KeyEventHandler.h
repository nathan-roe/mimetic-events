#include <jni.h>
#ifndef _Included_KeyEventHandler
#define _Included_KeyEventHandler
#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT void JNICALL Java_KeyEventHandler_captureKeyEvents(JNIEnv *, jobject);
#ifdef __cplusplus
}
#endif
#endif