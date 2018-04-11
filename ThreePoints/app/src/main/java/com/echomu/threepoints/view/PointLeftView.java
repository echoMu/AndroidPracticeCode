package com.echomu.threepoints.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
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
public class PointLeftView extends View {
    public static final float RADIUS = Utils.dpToPixel(5);

    Paint mPanint = new Paint(Paint.ANTI_ALIAS_FLAG);

    PointF position = new PointF(0,0);

    public PointLeftView(Context context) {
        super(context);
    }

    public PointLeftView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PointLeftView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        mPanint.setColor(Color.parseColor("#009688"));
    }

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        if (position != null) {
            this.position.set(position);
            invalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float height = getHeight()/2;
        float moveLeft=Utils.dpToPixel(50);
        canvas.drawCircle( getWidth()/2 - moveLeft* position.x, height, RADIUS, mPanint);
    }

}
