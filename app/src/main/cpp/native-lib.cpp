#include <jni.h>
#include <string>
#include "../../../../../oboe/include/oboe/AudioStreamCallback.h"
#include "../../../../../oboe/include/oboe/AudioStreamBase.h"
#include "../../../../../oboe/apps/fxlab/app/src/main/cpp/logging_macros.h"
#include "../../../../../oboe/samples/LiveEffect/src/main/cpp/LiveEffectEngine.h"
#include "../../../../../oboe/include/oboe/AudioStreamBuilder.h"
#include "../../../../../oboe/include/oboe/AudioStream.h"
#include "../../../../../oboe/include/oboe/Utilities.h"

oboe::AudioStreamBuilder inputStreamBuilder, outputStreamBuilder ;
oboe::AudioStream *mOutputStream, *mInputStream;

oboe::Result result;

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

JNIEXPORT void JNICALL
Java_com_example_android_livestream_MainActivity_native_1setRecordingDeviceId(JNIEnv *env,
                                                                              jobject thiz,
                                                                              jint type_ble_headset) {
    // TODO: implement native_setRecordingDeviceId()
    inputStreamBuilder.setDeviceId(type_ble_headset);
}

JNIEXPORT void JNICALL
Java_com_example_android_livestream_MainActivity_native_1setPlaybackDeviceId(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jint type_builtin_speaker) {
    // TODO: implement native_setPlaybackDeviceId()
    outputStreamBuilder.setDeviceId(type_builtin_speaker);

}

JNIEXPORT void JNICALL
Java_com_example_android_livestream_MainActivity_native_1create(JNIEnv *env, jobject thiz) {
    // TODO: implement native_create()
    inputStreamBuilder.setDirection(oboe::Direction::Input);
    inputStreamBuilder.setAudioApi(oboe::AudioApi::OpenSLES);
    inputStreamBuilder.setSharingMode(oboe::SharingMode::Exclusive);
    inputStreamBuilder.setChannelCount(1);
    inputStreamBuilder.setPerformanceMode(oboe::PerformanceMode::LowLatency);

    outputStreamBuilder.setDirection(oboe::Direction::Output);
    outputStreamBuilder.setAudioApi(oboe::AudioApi::OpenSLES);
    outputStreamBuilder.setSharingMode(oboe::SharingMode::Exclusive);
    outputStreamBuilder.setChannelCount(1);
    outputStreamBuilder.setPerformanceMode(oboe::PerformanceMode::LowLatency);
}
//
//JNIEXPORT void JNICALL
//Java_com_example_android_livestream_MainActivity_native_1setAudioApi(JNIEnv *env, jobject thiz) {
//    // TODO: implement native_setAudioApi()
//
//}



JNIEXPORT void JNICALL
Java_com_example_android_livestream_MainActivity_startStream(JNIEnv *env, jobject thiz) {
    // TODO: implement startStream()
    oboe::Result result = inputStreamBuilder.openStream(&mInputStream);
    if (result != oboe::Result::OK){
        LOGE("Error in opening the input stream");
    }
    result = mInputStream->requestStart();
    if (result != oboe::Result::OK){
        LOGE("Error in opening the input stream");
    }

    outputStreamBuilder.openStream(&mOutputStream);
    if (result != oboe::Result::OK){
        LOGE("Error in opening the output stream");
    }
    result = mOutputStream->requestStart();
    if (result != oboe::Result::OK){
        LOGE("Error in opening the output stream");
    }

//    constexpr int kMillisecondsToRecord = 2;
//    const int32_t kMillisPerSecond = 1000;
//    const int32_t requestedFrames = (int32_t)(kMillisecondsToRecord * (mInputStream->getSampleRate() / kMillisPerSecond));
//    int16_t myBuffer[requestedFrames];
//
//    constexpr int64_t kTimeOutValue = 3*(1000000);
//
//    int frameRead = 0;
//    do {
//        auto result = mInputStream->read(myBuffer, mInputStream->getBufferSizeInFrames() , 0);
//        if(result!=oboe::Result::OK){
//            break;
//        }
//        auto result1 = mOutputStream->write(myBuffer, requestedFrames, kTimeOutValue);
//        if(result1!=oboe::Result::OK){
//            LOGD("Write  frames error");
//        }
//        else{
//            LOGE("Error Writing Stream");
//        }
//        frameRead = result.value();
//    } while (frameRead!=0);

//    while(true){


//    }



//    mOutputStream = mInputStream;

}

JNIEXPORT void JNICALL
Java_com_example_android_livestream_MainActivity_flushStream(JNIEnv *env, jobject thiz) {
    // TODO: implement closeStream()
    mOutputStream->close();
    if (result != oboe::Result::OK){
        LOGE("Error in closing the output stream");
    }

    mInputStream->close();
    if (result != oboe::Result::OK){
        LOGE("Error in closing the output stream");
    }
}
}