#include <jni.h>
#include <android/log.h>
#include "lwf.h"

#define LOG_TAG "pure2d::LWF"
#define LOG(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

using namespace LWF;

typedef map<int, shared_ptr<Data> > DataMap;
typedef map<int, shared_ptr<class LWF> > LWFMap;

static DataMap s_dataMap;
static int s_dataId;

static LWFMap s_lwfMap;
static int s_lwfId;

extern "C" JNIEXPORT jint JNICALL Java_com_funzio_pure2D_lwf_LWFData_create(JNIEnv *env, jobject obj, jbyteArray jdata)
{
    jsize len = env->GetArrayLength(jdata);
    jbyte *b = (jbyte *)env->GetByteArrayElements(jdata, NULL);

    shared_ptr<Data> data = make_shared<Data>(b, len);
    int id;
    if (data->Check()) {
        id = ++s_dataId;
        s_dataMap[id] = data;
    } else {
        id = -1;
    }

    env->ReleaseByteArrayElements(jdata, b, 0);
    return id;
}

extern "C" JNIEXPORT void JNICALL Java_com_funzio_pure2D_lwf_LWFData_destroy(JNIEnv *env, jobject obj, jint jLWFDataId)
{
    s_dataMap.erase((int)jLWFDataId);
}

extern "C" JNIEXPORT jint JNICALL Java_com_funzio_pure2D_lwf_LWF_create(JNIEnv *env, jobject obj, jint jLWFDataId)
{
    DataMap::iterator it = s_dataMap.find((int)jLWFDataId);
    if (it == s_dataMap.end())
        return -1;

    shared_ptr<NullRendererFactory> factory =
        make_shared<NullRendererFactory>();
    shared_ptr<class LWF> lwf = make_shared<class LWF>(it->second, factory);

    int id = ++s_lwfId;
    s_lwfMap[id] = lwf;

    return id;
}

extern "C" JNIEXPORT jlong JNICALL Java_com_funzio_pure2D_lwf_LWF_getPointer(JNIEnv *env, jobject obj, jint jLWFId)
{
    LWFMap::iterator it = s_lwfMap.find((int)jLWFId);
    if (it == s_lwfMap.end())
        return 0;

    return (jlong)it->second.get();
}

extern "C" JNIEXPORT void JNICALL Java_com_funzio_pure2D_lwf_LWF_exec(JNIEnv *env, jobject obj, jlong jLWF, jfloat jTick)
{
    if (!jLWF)
        return;

    class LWF *lwf = (class LWF *)jLWF;
    lwf->Exec(jTick);
}

extern "C" JNIEXPORT void JNICALL Java_com_funzio_pure2D_lwf_LWF_destroy(JNIEnv *env, jobject obj, jint jLWFId)
{
    s_lwfMap.erase((int)jLWFId);
}

