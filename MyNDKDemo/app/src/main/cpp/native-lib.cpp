#include <jni.h>
#include <string>
#include <stdlib.h>
#include <android/log.h>

// 项目可以通过编译，但是找不到C++里面的方法，
// 这是因为CMake在编译C++代码的时候把刚才新建的C++函数漏掉了，
// 怎么把他加上呢，注意到C++代码里面有一个extern "C"这句话了吗，
// 这个是CMake的东西，把这句话放到最上面，然后加个大括号，把所有Java需要调用的方法都放里面，
// 调整后的C++代码如下：
extern "C" {
JNIEXPORT jint JNICALL
Java_com_echo_myndkdemo_MainActivity_plusFromJNI(
        JNIEnv *env, jobject /* this */, jint a, jint b) {
    return a + b;
}

JNIEXPORT jstring JNICALL
Java_com_echo_myndkdemo_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

JNIEXPORT void JNICALL
Java_com_echo_myndkdemo_MainActivity_accessFiled(JNIEnv *env, jobject jobj) {
    // TODO
    //通过对象拿到Class
    jclass clz = env->GetObjectClass(jobj);
    //拿到对应属性的ID
    jfieldID fid = env->GetFieldID(clz, "str", "Ljava/lang/String;");
    //通过属性ID拿到属性的值
    jstring jstr = (jstring) env->GetObjectField(jobj, fid);

    //通过Java字符串拿到C字符串，第三个参数是一个出参，用来告诉我们GetStringUTFChars内部是否复制了一份字符串
    //如果没有复制，那么出参为isCopy，这时候就不能修改字符串的值了，因为Java中常量池中的字符串是不允许修改的（但是jstr可以指向另外一个字符串）
    const char *cstr = env->GetStringUTFChars(jstr, NULL);
    //在C层修改这个属性的值
    char res[20] = "I love you : ";
    strcat(res, cstr);

    //重新生成Java的字符串，并且设置给对应的属性
    jstring jstr_new = env->NewStringUTF(res);
    env->SetObjectField(jobj, fid, jstr_new);

    //最后释放资源，通知垃圾回收器来回收
    //良好的习惯就是，每次GetStringUTFChars，结束的时候都有一个ReleaseStringUTFChars与之呼应
    env->ReleaseStringUTFChars(jstr, cstr);
}

JNIEXPORT void JNICALL
Java_com_echo_myndkdemo_MainActivity_accessStaticField(JNIEnv *env, jobject instance) {
    // TODO
    //与上面类似，只不过是某些方法需要加上Static
    jclass clz = env->GetObjectClass(instance);
    jfieldID fid = env->GetStaticFieldID(clz, "NUM", "I");
    jint jInt = env->GetStaticIntField(clz, fid);

    jInt++;
    env->SetStaticIntField(clz, fid, jInt);
}

JNIEXPORT jint JNICALL
Java_com_echo_myndkdemo_MainActivity_accessMethod(JNIEnv *env, jobject instance) {
    // TODO
    jclass clz=env->GetObjectClass(instance);
    jmethodID mid=env->GetMethodID(clz,"genRandomInt","(I)I");

    //最后一个是可变参数，就是调用该方法所传入的参数
    jint jInt=env->CallIntMethod(instance,mid,100);
    __android_log_print(ANDROID_LOG_DEBUG, "echoMu", "output from C---%d", jInt);

    return jInt;
}

JNIEXPORT jstring JNICALL
Java_com_echo_myndkdemo_MainActivity_accessStaticMethod(JNIEnv *env, jobject instance) {
    // TODO
    jclass clz=env->GetObjectClass(instance);
    jmethodID mid=env->GetStaticMethodID(clz,"getUUID","()Ljava/lang/String;");

    //调用java的静态方法，拿到返回值
    jstring jstr=(jstring)env->CallStaticObjectMethod(clz,mid);

    const char *cstr = env->GetStringUTFChars(jstr, NULL);

//    //后续操作，产生以UUID为文件名的文件
//    char fielName[100];
//    sprintf(fielName, "E:\\%s.txt", cstr);
//    FILE* f = fopen(fielName, "w");
//    fputs(cstr, f);
//    fclose(f);
//
//    printf("output from C : File had saved", jstr);
    return jstr;
}

JNIEXPORT jlong JNICALL
Java_com_echo_myndkdemo_MainActivity_accessConstructor(JNIEnv *env, jobject instance) {
    // TODO
    jclass clz_date=env->FindClass("java/util/Date");
    //构造方法的函数名的格式是：<init>
    //不能写类名，因为构造方法函数名都一样区分不了，只能通过参数列表（签名）区分
    jmethodID mid_Date=env->GetMethodID(clz_date,"<init>","()V");

    //调用构造函数
    jobject date=env->NewObject(clz_date,mid_Date);

    //注意签名，返回值long的属性签名是J
    jmethodID mid_getTime=env->GetMethodID(clz_date,"getTime","()J");
    //调用getTime方法
    jlong jtime=env->CallLongMethod(date,mid_getTime);

    return jtime;
}

int compare(const void *a, const void *b)
{
    int *pa = (int*)a;
    int *pb = (int*)b;
    return (*pa )- (*pb);  //从小到大排序
}

JNIEXPORT void JNICALL
Java_com_echo_myndkdemo_MainActivity_sortArray(JNIEnv *env, jobject instance, jintArray array_) {
    // TODO
    //通过Java的数组，拿到C的数组的指针
    jint *c_arr = env->GetIntArrayElements(array_, NULL);
    //获取Java数组的大小
    jsize len=env->GetArrayLength(array_);
    //排序，其中compare是函数指针，用于比较大小，与Java类似
    //头文件<stdlib.h>
    qsort(c_arr,len, sizeof(jint),compare);

    //操作完之后需要同步C的数组到Java数组中
    //    这个方法的最后一个参数是模式：
    //
    //    0：            Java数组进行更新，并且释放C/C++数组。
    //    JNI_ABORT：    Java数组不进行更新，但是释放C/C++数组。
    //    JNI_COMMIT：    Java数组进行更新，不释放C/C++数组（函数执行完，数组还是会释放）。
    env->ReleaseIntArrayElements(array_, c_arr, 0);
}

}



