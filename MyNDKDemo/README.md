# MyNDKDemo
NDK开发学习入门

## 前言
通过NDK开发能够解决Java做不到或者Java做的话效率、安全性会很低的问题。
比如说视频处理直播，支付安全等。

我们需要先认识这两个概念：

1 NDK，Native Development Kit，NDK提供了一系列的工具，帮助开发者快速开发C（或C++）的动态库，并能自动将so和java应用一起打包成apk。

2 JNI，Java Native Interface，Java调用C/C++，C/C++调用Java的一套API。NDK开发需要用到JNI。

Android Studio从2.2开始就默认使用CMake的方式来构建NDK项目，当然我们也可以使用ndk-build的方式，本工程采用CMake的方式。
我们在创建项目的时候，在向导的 Configure your new project 部分，选中 Include C++ Support 复选框，就可以创建带C/C++支持的项目。


### JNI数据类型与方法属性访问
#### JNI数据类型
##### 基本数据

Java基本数据类型与JNI数据类型的映射关系

    Java类型->JNI类型->C类型
    
JNI的基本数据类型

| java        | JNI           | 
| ------------- |:-------------:|
| boolean      | jboolean | 
| byte      | jbyte      |  
| char | jchar      | 
| short     | jshort          |
| int     | jint          |
| long     | jlong          |
| float    | jfloat          |
| double     | jdouble          |
| void     | void          |      
                 
引用类型(对象)

| java        | JNI           | 
| ------------- |:-------------:|
| String      | jstring | 
| Object      | jobject |                  
| byte[]       | jByteArray | 
| object[](String[])      | jobjectArray | 
                 
                              
                 
#### native函数参数说明

每个native函数，都至少有两个参数（JNIEnv*,jclass或者jobject)。

1 当native方法为静态方法时：
jclass 代表native方法所属类的class对象(JniTest.class)。

2 当native方法为非静态方法时：
jobject 代表native方法所属的对象。

#### 关于属性与方法的签名
##### 属性的签名
属性的签名其实就是属性的类型的简称，对应关系例如

  char->C
  
##### 方法的签名
获取方法的签名比较麻烦，通过cd命令，来到Java工程的bin目录，用以下命令就可以拿到指定类的所有属性、方法的签名。

    javap -s -p 完整类名

#### C/C++访问Java的属性、方法

有以下几种情况：

- 访问Java类的非静态属性。
- 访问Java类的静态属性。
- 访问Java类的非静态方法。
- 访问Java类的静态方法。
- 间接访问Java类的父类的方法。
- 访问Java类的构造方法。

以上情况在工程中都有对应的例子。
  
                 
