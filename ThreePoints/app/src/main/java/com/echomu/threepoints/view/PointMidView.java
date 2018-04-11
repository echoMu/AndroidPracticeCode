package com.echomu.threepoints.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.echomu.threepoints.utils.Utils;

/**
 * <pre>
 *     author : echoMu
 *     e-mail :
 *     time   : 2018/01/23
 *     desc   :
 *     version:
 * </pre>
 */
public class PointMidView extends View {
    /**
     * 自定义属性 圆的半径
     */
    private float radius = Utils.dpToPixel(5);

    private Paint mPanint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    {
        mPanint.setColor(Color.parseColor("#009688"));
    }

    public PointMidView(Context context) {
        super(context);
    }

    public PointMidView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PointMidView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mPanint);
    }

}
