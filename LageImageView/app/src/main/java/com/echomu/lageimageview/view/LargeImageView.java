package com.echomu.lageimageview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 *     author : echoMu
 *     e-mail :
 *     time   : 2018/04/10
 *     desc   :
 *     version:
 * </pre>
 */
public class LargeImageView extends View {
    /**
     * 图片原来的宽和高
     */
    private int mOriginWith, mOriginHeight;
    private BitmapRegionDecoder mDecoder;
    private MoveGestureDetector mDetector;
    /**
     * 绘制的区域
     */
    private volatile Rect mRect = new Rect();

    private static final BitmapFactory.Options options = new BitmapFactory.Options();

    static
    {
        options.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    public void setInputStream(InputStream is){
        try {
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds=true;
            //获得图片的宽、高
            BitmapFactory.decodeStream(is,null,options);
            mOriginWith=options.outWidth;
            mOriginHeight=options.outHeight;

            mDecoder=BitmapRegionDecoder.newInstance(is,false);

            requestLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LargeImageView(Context context) {
        this(context,null);
    }

    public LargeImageView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public LargeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Log.d("echoMu","init...");
        mDetector=new MoveGestureDetector(getContext(),new MoveGestureDetector.SimpleMoveGestureDetector(){
            @Override
            public boolean onMove(MoveGestureDetector detector) {
                Log.d("echoMu","onMove...");

                int moveX= (int) detector.getMoveX();
                int moveY= (int) detector.getMoveY();

                if(mOriginWith>getWidth()){
                    mRect.offset(-moveX,0);
                    checkWidth();
                    invalidate();
                }
                if (mOriginHeight > getHeight())
                {
                    mRect.offset(0, -moveY);
                    checkHeight();
                    invalidate();
                }

                return true;
            }
        });
    }

    private void checkWidth()
    {


        Rect rect = mRect;
        int imageWidth = mOriginWith;
        int imageHeight = mOriginHeight;

        if (rect.right > imageWidth)
        {
            rect.right = imageWidth;
            rect.left = imageWidth - getWidth();
        }

        if (rect.left < 0)
        {
            rect.left = 0;
            rect.right = getWidth();
        }
    }


    private void checkHeight()
    {

        Rect rect = mRect;
        int imageWidth = mOriginWith;
        int imageHeight = mOriginHeight;

        if (rect.bottom > imageHeight)
        {
            rect.bottom = imageHeight;
            rect.top = imageHeight - getHeight();
        }

        if (rect.top < 0)
        {
            rect.top = 0;
            rect.bottom = getHeight();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onToucEvent(event);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int imageWidth = mOriginWith;
        int imageHeight = mOriginHeight;

        //默认直接显示图片的中心区域，可以自己去调节
        mRect.left = imageWidth / 2 - width / 2;
        mRect.top = imageHeight / 2 - height / 2;
        mRect.right = mRect.left + width;
        mRect.bottom = mRect.top + height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("echoMu","onDraw...");

        Bitmap bm=mDecoder.decodeRegion(mRect,options);
        canvas.drawBitmap(bm,0,0,null);
    }
}
