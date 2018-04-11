# CustomsProgressBar
Android 自定义View实践之圆形进度ProgressBar
为了实现一个带有进度文本的圆形ProgressBar，我们自定义开发一个RoundProgressBar。

效果是这样的
![device-2017-02-24-165125.png](http://upload-images.jianshu.io/upload_images/817079-7b5fa86d50c91237.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

1.新建一个ProgressBar类继承View类，复写其构造函数和onDraw函数；
2.在values中建立一个attrs.xml文件来存放自定义view的属性；
3.从xml文件中获取自定义的view类中的各个属性值；
4.同时对使用者提供set和get方法进行各个属性的设置；

首先要整理出我们需要的view的属性有哪些，我们定义了这些属性：

        max                    最大进度
        startAngle             进度从圆环的哪个角度开始走
        backColor              圆环内部的填充色
        roundColor             圆环的颜色
        roundProgressColor     圆环进度的颜色
        roundWidth             圆环宽度
        textColor              中心文字的颜色
        textSize               中心文字的大小
        textIsDisplayable      中心文字是否显示
        style = 0/1            进度的风格，实心或者空心

通过键值对的形式来存放键（name）：属性名，和值（format）：属性的类型（限制输入的类型），attrs.xml文件如下;

    <?xml version="1.0" encoding="utf-8"?>
    <resources>
    <declare-styleable name="RoundProgressBar">
        <attr name="max" format="integer"></attr>
        <attr name="startAngle" format="integer"></attr>
        <attr name="backColor" format="color"></attr>
        <attr name="roundColor" format="color"></attr>
        <attr name="roundProgressColor" format="color"></attr>
        <attr name="roundWidth" format="dimension"></attr>
        <attr name="textColor" format="color"></attr>
        <attr name="textSize" format="dimension"></attr>
        <attr name="textIsDisplayable" format="boolean"></attr>
        <attr name="style">
            <enum name="STROKE" value="0"></enum>
            <enum name="FILL" value="1"></enum>
        </attr>
    </declare-styleable>
    </resources>

有了这些属性，我们就可以获取它们的值了，在ProgressBar类中的构造函数进行初始化工作，使用TypedArray，得到每一项属性的值：

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
        //获取自定义属性和默认值，第一个参数是从用户属性中得到的设置，如果用户没有设置，那么就用默认的属性，即：第二个参数
        //圆环的颜色
        roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.BLACK);
        //圆环进度的颜色
        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.RED);
        //中间进度百分比的字符串的颜色
        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.BLUE);
        //文字的大小
        textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 24);
        //圆环的宽度
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 4);
        //最大进度
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
        //是否显示中间的进度
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        //进度的风格，实心或者空心
        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);
        //进度开始的角度数
        startAngle = mTypedArray.getInt(R.styleable.RoundProgressBar_startAngle, -90);
        // 圆形颜色
        backColor = mTypedArray.getColor(R.styleable.RoundProgressBar_backColor, 0);

        //回收
        mTypedArray.recycle();

一切准备工作做完，我们就可以在onDraw函数中开始绘制了：

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //进行绘制
        //1.画最外层的大圆环
        //获取圆心的x坐标
        int centre = getWidth() / 2;
        //圆环的半径
        int radius = (int) (centre - roundWidth / 2);
        //设置圆环的颜色
        paint.setColor(roundColor);
        //设置空心
        paint.setStyle(Paint.Style.STROKE);
        //设置圆环的宽度
        paint.setStrokeWidth(roundWidth);
        //消除锯齿
        paint.setAntiAlias(true);
        //画出圆环
        canvas.drawCircle(centre, centre, radius, paint);
        if (backColor != 0) {
            paint.setColor(backColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centre, centre, radius, paint);
        }

        //画进度百分比字体
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        int percent = (int) (((float) progress / (float) max) * 100);
        //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        float textWidth = paint.measureText(percent + "%");
        if (textIsDisplayable && percent != 0 && style == STROKE) {
            //绘制文字
            //宽 centre - textWidth / 2
            //高 centre + textSize / 2
            canvas.drawText(percent + "%", centre - textWidth / 2, centre + textSize / 2, paint);
        }

        //画圆弧 ，圆环的进度
        //设置圆环的宽度
        paint.setStrokeWidth(roundWidth);
        //设置进度的颜色
        paint.setColor(roundProgressColor);
        //用于定义的圆弧的形状和大小的界限
        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius);

        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                /*第二个参数是进度开始的角度，-90表示从12点方向开始走进度，如果是0表示从三点钟方向走进度，依次类推
                 *public void drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint)
                    oval :指定圆弧的外轮廓矩形区域。
                    startAngle: 圆弧起始角度，单位为度。
                    sweepAngle: 圆弧扫过的角度，顺时针方向，单位为度。
                    useCenter: 如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形。
                    paint: 绘制圆弧的画板属性，如颜色，是否填充等
                 *
                */
                canvas.drawArc(oval, startAngle, 360 * progress / max, false, paint);  //根据进度画圆弧
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0)
                    canvas.drawArc(oval, startAngle, 360 * progress / max, true, paint);  //根据进度画圆弧
                break;
            }
        }

    }

完成之后就可以使用它了，例如要这样的ProgressBar;

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="无数字，环形" />

    <com.echomu.customsprogressbar.view.RoundProgressBar
        android:id="@+id/rpb_04"
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:layout_margin="8dp"
        app:startAngle="30"
        app:roundColor="#C6E2FF"
        app:roundWidth="10dip"
        app:roundProgressColor="#CD3333"
        app:textIsDisplayable="false"/>
