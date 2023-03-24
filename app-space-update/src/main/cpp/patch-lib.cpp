#include <jni.h>
#include <string>
#include <android/log.h>
#include <exception>

#include "patchUtils.h"
extern "C"
JNIEXPORT jint JNICALL
Java_com_appspa_update_utils_PatchUtils_patch(JNIEnv *env, jclass type, jstring oldApkPath_,
                                           jstring newApkPath_, jstring patchPath_) {

    int argc = 4;
    char *ch[argc];
    ch[0] = (char *) "bspatch";
    ch[1] = const_cast<char *>(env->GetStringUTFChars(oldApkPath_, 0));
    ch[2] = const_cast<char *>(env->GetStringUTFChars(newApkPath_, 0));
    ch[3] = const_cast<char *>(env->GetStringUTFChars(patchPath_, 0));

    __android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "old = %s ", ch[1]);
    __android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "new = %s ", ch[2]);
    __android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "patch = %s ", ch[3]);
    int ret = applypatch(argc, ch);
    __android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "applypatch result = %d ", ret);

//    FILE* f = fopen(ch[3], "r");
//    if (f == NULL){
//        __android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "------->>>>>> ");
//    }else{
//        __android_log_print(ANDROID_LOG_INFO, "ApkPatchLibrary", "<<<<<<<<<< ");
//    }
    env->ReleaseStringUTFChars(oldApkPath_, ch[1]);
    env->ReleaseStringUTFChars(newApkPath_, ch[2]);
    env->ReleaseStringUTFChars(patchPath_, ch[3]);


    return ret;
}