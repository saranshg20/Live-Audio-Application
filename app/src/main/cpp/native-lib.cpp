#include <jni.h>
#include <string>
#include "../../../../../oboe/include/oboe/Definitions.h"
#include "../../../../../oboe/samples/LiveEffect/src/main/cpp/LiveEffectEngine.h"
#include "../../../../../oboe/apps/fxlab/app/src/main/cpp/logging_macros.h"
#include "LiveEffectEngine.h"

static LiveEffectEngine *engine = nullptr;

extern "C" {

JNIEXPORT void JNICALL
Java_com_example_android_livestream_MainActivity_native_1setDefaultStreamValues(JNIEnv *env,
                                                                                jobject thiz,
                                                                                jint default_sample_rate,
                                                                                jint default_frames_per_burst) {
    // TODO: implement native_setDefaultStreamValues()
    oboe::DefaultStreamValues::SampleRate = (int32_t) default_sample_rate;
    oboe::DefaultStreamValues::FramesPerBurst = (int32_t) default_frames_per_burst;
}


}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_android_livestream_MainActivity_native_1setRecordingDeviceId(JNIEnv *env,
                                                                              jobject thiz,
                                                                              jint type_ble_headset) {
    // TODO: implement native_setRecordingDeviceId()
    if(engine== nullptr){
        LOGE("Engine is null, call create Engine before..");
        return;
    }
    engine->setRecordingDeviceId(type_ble_headset);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_android_livestream_MainActivity_native_1setPlaybackDeviceId(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jint type_builtin_speaker) {
    // TODO: implement native_setPlaybackDeviceId()
    if(engine== nullptr){
        LOGE("Engine is null, call create Engine before..");
        return;
    }
    engine->setPlaybackDeviceId(type_builtin_speaker);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_android_livestream_MainActivity_native_1create(JNIEnv *env, jobject thiz) {
    // TODO: implement native_create()
    if(engine== nullptr){
        engine = new LiveEffectEngine();
    }
    return;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_android_livestream_MainActivity_native_1delete(JNIEnv *env, jobject thiz) {
    // TODO: implement native_delete()
    if(engine){
        engine->setEffectOn(false);
        delete engine;
        engine = nullptr;
    }
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_android_livestream_MainActivity_native_1setEffectOn(JNIEnv *env, jobject thiz,
                                                                     jboolean b) {
    // TODO: implement native_setEffectOn()
    if(engine== nullptr){
        LOGE("Engine is null, call create Engine before..");
        return JNI_FALSE;
    }

    return engine->setEffectOn(b)?JNI_TRUE:JNI_FALSE;
}