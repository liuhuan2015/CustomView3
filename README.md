# CustomView3
自定义控件练习3<br>
虽然写了CutsomView1,CustomView2,但是写这个项目时,还是有些懵的,代码写出来后发现效果完全不对,检查了半天才发现是有个地方的-+号写错了<br>
是这里<br>

     RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius);//定义圆弧形状和大小
     
项目中用到了之前我很少甚至基本没有用过的Canvas的两个方法(具体解释可见代码或百度):<br>
    
    canvas.drawCircle(...):绘制圆环
    canvas.drawArc(...):根据进度绘制圆弧


这个项目的思路是:<br>
一,在构造方法中获取自定义的各种属性(圆环第一层颜色,第二层颜色,第二层颜色增长速度,圆环宽度),然后开启了一个子线程,用来不断触发onDraw(...)方法<br>
只贴一点那个开启子线程的代码吧<br>

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
        
二,在onDraw(...)方法中来绘制图形

    @Override
    protected void onDraw(Canvas canvas) {
    //super.onDraw(canvas);

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

