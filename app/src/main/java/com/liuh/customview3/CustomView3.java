package com.liuh.customview3;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Date: 2017/12/21 10:00
 * Description:这个项目实现的是:一个圆环,其中有两种颜色交替增长的效果
 * 实现思路:1,在构造方法中获取该控件自定义的属性值,比如设置的第一圈的颜色,第二圈的颜色,圆环半径,增长速度
 * 2,在构造方法中启动一个子线程,在子线程中是一个死循环,用来不断触发View的onDraw(...)
 * 3,在onDraw(...)中使用Canvas对象进行使徒的绘制,主要用到Paint,canvas.drawCircle(...), canvas.drawArc(...)
 * Paint:定义边界宽度,颜色,Style(空心(Paint.Style.STROKE)还是填充(Paint.Style.FILL))
 * canvas.drawCircle(...):绘制圆环
 * canvas.drawArc(...):根据进度绘制圆弧
 * <p>
 * <p>
 * public void drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint)
 * <p>
 * oval :指定圆弧的外轮廓矩形区域。
 * startAngle: 圆弧起始角度，单位为度。
 * sweepAngle: 圆弧扫过的角度，顺时针方向，单位为度,从右中间开始为零度。
 * useCenter: 如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形。
 * paint: 绘制圆弧的画板属性，如颜色，是否填充等。
 */

public class CustomView3 extends View {
    /**
     * 第一圈的颜色
     **/
    private int mFirstColor;
    /**
     * 第二圈的颜色
     **/
    private int mSecondColor;
    /**
     * 圆圈的宽度
     **/
    private int mCircleWidth;
    /**
     * 画笔
     **/
    private Paint mPaint;
    /**
     * 当前进度
     **/
    private int mProgress;
    /**
     * 速度
     **/
    private int mSpeed = 20;
    /**
     * 是否应该开始下一个
     **/
    private boolean isNext = false;


    public CustomView3(Context context) {
        this(context, null);
    }

    public CustomView3(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomView3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomView3, defStyleAttr, 0);

        int n = a.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);

            switch (attr) {
                case R.styleable.CustomView3_firstColor:
                    mFirstColor = a.getColor(attr, Color.GREEN);
                    break;
                case R.styleable.CustomView3_secondColor:
                    mSecondColor = a.getColor(attr, Color.RED);
                    break;
                case R.styleable.CustomView3_circleWidth:
                    mCircleWidth = a.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomView3_speed:
                    mSpeed = a.getInt(attr, 20);
                    Log.e("xxxxxxxx", "--------mSpeed:" + mSpeed);
                    break;
            }
        }
        a.recycle();

        mPaint = new Paint();

        //开启绘图线程
        new Thread() {

            @Override
            public void run() {
                while (true) {
                    mProgress++;

                    if (mProgress == 360) {
                        //如果画完一圈,颜色互换,画下一圈
                        mProgress = 0;
                        if (!isNext) {
                            isNext = true;
                        } else {
                            isNext = false;
                        }
                    }
                    postInvalidate();

                    try {
                        Thread.sleep(mSpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        int center = getWidth() / 2;//获取圆心的x坐标

        int radius = center - mCircleWidth / 2;//半径

        mPaint.setStrokeWidth(mCircleWidth);//设置圆环宽度
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setStyle(Paint.Style.STROKE);//设置空心
        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius);//定义圆弧形状和大小
        if (!isNext) {
            //第一颜色的圈跑完,第二颜色跑
            mPaint.setColor(mFirstColor);//设置圆环的颜色
            canvas.drawCircle(center, center, radius, mPaint);//画出圆环
            mPaint.setColor(mSecondColor);//设置圆环的颜色
            canvas.drawArc(oval, -90, mProgress, false, mPaint);//根据进度画圆弧
        } else {
            mPaint.setColor(mSecondColor);
            canvas.drawCircle(center, center, radius, mPaint);
            mPaint.setColor(mFirstColor);
            canvas.drawArc(oval, -90, mProgress, false, mPaint);
        }


    }
}
