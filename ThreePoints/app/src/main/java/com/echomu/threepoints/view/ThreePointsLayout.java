package com.echomu.threepoints.view;

import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.echomu.threepoints.R;
import com.echomu.threepoints.utils.Utils;

public class ThreePointsLayout extends RelativeLayout {
    private PointLeftView pointViewLeft;
    private PointRightView pointViewRight;
    private PointMidView pointViewMid;
    private Button animateBt;

//    private float x1, x2, y1, y2;

    private int status = BEGIN;
    private static int BEGIN = 0;
    private static int TOBIG = 1;
    private static int TOSMALL = 2;
    private static int TOSMALL_AND_TOLEFTANDRIGHT = 3;
    private static int TOLEFTANDRIGHT = 4;
    private static int TOBIG_AND_TOMID = 5;
    private static int TOMID = 6;

    private static int CHANGE_MIDPOINT_TOSMALL = 0;
    private static int CHANGE_MIDPOINT_TOBIG = 1;

    public ThreePointsLayout(Context context) {
        this(context, null);
    }

    public ThreePointsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThreePointsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        pointViewLeft = (PointLeftView) findViewById(R.id.objectAnimatorViewLeft);
        pointViewMid = (PointMidView) findViewById(R.id.objectAnimatorViewMid);
        pointViewRight = (PointRightView) findViewById(R.id.objectAnimatorViewRight);
        animateBt = (Button) findViewById(R.id.animateBt);

    }

    private class PointFEvaluator implements TypeEvaluator<PointF> {
        PointF newPoint = new PointF();

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            float x = startValue.x + (fraction * (endValue.x - startValue.x));
            float y = startValue.y + (fraction * (endValue.y - startValue.y));

            newPoint.set(x, y);

            return newPoint;
        }
    }

    private float mLastY = 0;
    private float mLastx = 0;

    private int scrollByX=0;
    private final static float DISTANCE=Utils.dpToPixel(30);

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);

        float currentY = event.getY();
        float currentX = event.getX();

        //获得触摸事件
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("echoMu", "手指按下");
//                //当手指按下的时候
//                x1 = event.getX();
//                y1 = event.getY();

                mLastY = currentY;
                mLastx = currentX;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = mLastY - currentY;
                mLastY = currentY;
                mLastx = currentX;
//                Log.d("echoMu", "moveY:" + moveY);
                Log.d("echoMu", "status:" + status);

                if (moveY > 0&&status==TOLEFTANDRIGHT) {
                    Log.d("echoMu", "向上滑动:");

                    scrollByX+=(int) (moveY);

                    if(Math.abs(scrollByX)<=DISTANCE) {
                        pointViewLeft.scrollBy(-(int) (moveY), 0);
                        pointViewRight.scrollBy((int) (moveY), 0);
                    }else {
                        //结束，复位
                        scrollByX=0;
                        status=BEGIN;
                    }
                } else if (moveY <= 0&&status==BEGIN) {
                    Log.d("echoMu", "向下滑动 ");

                    scrollByX+=(int) (moveY);

                    if(Math.abs(scrollByX)<DISTANCE/5) {
                        int scale=Math.abs(scrollByX)/10>1?Math.abs(scrollByX)/10:1;
                        Log.d("echoMu","scale:"+scale);
                        pointViewMid.animate().scaleX(scale).scaleY(scale);
                    }

                    if(Math.abs(scrollByX)>=0&&Math.abs(scrollByX)<=DISTANCE) {
                        if(scrollByX!=0) {
                            float scale = Math.abs(scrollByX) / 10;
                            Log.d("echoMu", "scale2:" + scale);
                            pointViewMid.animate().scaleX(scale).scaleY(scale);
                        }

                        pointViewLeft.scrollBy(-(int) (moveY), 0);
                        pointViewRight.scrollBy((int) (moveY),0);
                    }else {
                        //状态：向两边分散
                        scrollByX=0;
                        status=TOLEFTANDRIGHT;
                    }
                    Log.d("echoMu", "scrollByX:"+scrollByX);
                }

                break;
            case MotionEvent.ACTION_UP:
                //当手指离开的时候
                Log.d("echoMu", "手指离开 ");

                break;
        }
        return true;
    }

}
