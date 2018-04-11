package com.echomu.threepoints;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.echomu.androidlib.utils.CommonLog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CommonLog.d(getClass(),"----------");
    }

}
