package com.echomu.customsprogressbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.echomu.customsprogressbar.view.RoundProgressBar;

public class MainActivity extends AppCompatActivity {
    private RoundProgressBar rpb01,rpb02,rpb03,rpb04;

    private int mTotalProgress;
    private int mCurrentProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rpb01= (RoundProgressBar) findViewById(R.id.rpb_01);
        rpb02= (RoundProgressBar) findViewById(R.id.rpb_02);
        rpb03= (RoundProgressBar) findViewById(R.id.rpb_03);
        rpb04= (RoundProgressBar) findViewById(R.id.rpb_04);

        mTotalProgress = 100;
        mCurrentProgress = 0;

        new Thread(new ProgressRunable()).start();
    }
    class ProgressRunable implements Runnable {

        @Override
        public void run() {

            while (mCurrentProgress < mTotalProgress) {
                mCurrentProgress += 1;

                rpb01.setProgress(mCurrentProgress);
                rpb02.setProgress(mCurrentProgress);
                rpb03.setProgress(mCurrentProgress);
                rpb04.setProgress(mCurrentProgress);

                try {
                    Thread.sleep(100);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
