package com.echo.myndkdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * feature-1
 */
public class MainActivity extends AppCompatActivity {
    private static String TAG="echoMu";

    public String str="echoMu";

    //访问静态属性NUM，修改它的值
    public static int NUM = 0;

    //产生指定范围的随机数
    public int genRandomInt(int max){
        Log.d(TAG,"genRandomInt 执行了...max = "+ max);
        return new Random().nextInt(max);
    }

    //产生UUID字符串
    public static String getUUID(){
        Log.d(TAG,"getUUID 执行了...");
        return UUID.randomUUID().toString();
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI()+"\n算了个数：1+2="+plusFromJNI(1,2));

        //访问Java的非静态属性
        Log.d(TAG,"str1:"+str);
        //修改非静态属性str
        accessFiled();
        Log.d(TAG,"str2:"+str);

        Log.d(TAG,"NUM0:"+NUM);
        for(int i=0;i<10;i++) {
            accessStaticField();
            Log.d(TAG, "NUM"+(i+1)+":" + NUM);
        }

        Log.d(TAG, "genRandomInt:"+accessMethod());

        Log.d(TAG, "getUUID:"+accessStaticMethod());

        //直接在Java中构造Date然后调用getTime
        Date date = new Date();
        Log.d(TAG,"date.getTime():"+String.valueOf(date.getTime()));
        //通过C语音构造Date然后调用getTime
        Log.d(TAG,"date.getTime():"+String.valueOf(accessConstructor()));

        int[] arr={4,1,7,2,9,6};
        Log.d(TAG,"排序前 arr:"+ Arrays.toString(arr));
        sortArray(arr);
        Log.d(TAG,"经过C排序后 arr:"+Arrays.toString(arr));
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native int plusFromJNI(int a,int b);

    //访问非静态属性str，修改它的值
    public native void accessFiled();

    public native void accessStaticField();

    public native int accessMethod();

    public native String accessStaticMethod();

    //调用Date的构造函数
    public native long accessConstructor();

    //数组处理
    public native void sortArray(int array[]);
}
