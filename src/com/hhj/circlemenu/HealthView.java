package com.hhj.circlemenu;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class HealthView extends View {

    private int mTextColor;
    //字体颜色,大小,竖线的颜色
    private int textColor, lineColor;
    //背景的画笔
    private Paint backgroundPaint;
    //背景的坐标
    private int radiusBg, widthBg, heightBg;
    private Path pathBg, linePath;
    //圆弧的画笔
    private Paint arcPaint;
    private RectF arcRect;
    //数字的画笔
    private Paint textPaint;
    private PathEffect effects;

    //虚线的画笔
    private Paint linePaint;

    //圆角竖条的距离,高度,平均高度
    private float rectSize, rectAgHeight;
    //圆角竖条的画笔
    private Paint rectPaint;
    private Path rectPath;

    //底部波纹
    private Paint weavPaint;
    private Path weavPath;
    //动画实现
    //动画效果的添加
    private AnimatorSet animSet;
    private int walkNum, rankNum;
    private float arcNum = 30;
    
    //水波纹的动画实现
    private float weavX,weavY;
    private int mySize, rank, averageSize = 5436;
    private String myaverageTxt;
    protected int mLineColor = Color.parseColor("#76f112");
    
    public static List<Integer> sizes = new ArrayList<Integer>();
    
    public HealthView(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }
    public HealthView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public HealthView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
      //获取我们自定义的样式属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HealthView);
        //默认颜色设置为黑色
        textColor = array.getColor(R.styleable.HealthView_titleColor, Color.BLACK);
        lineColor = mLineColor;
        array.recycle();
        sizes.add(1234);
        sizes.add(2234);
        sizes.add(4234);
        sizes.add(6234);
        sizes.add(3834);
        sizes.add(7234);
        sizes.add(5436);
        init();
    }
    
  //初始化操作
    private void init() {
        pathBg = new Path();
        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePath = new Path();
        effects = new DashPathEffect(new float[]{5, 5}, 1);
        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPath = new Path();
        weavPaint = new Paint();
        weavPaint.setAntiAlias(true);
        weavPath = new Path();
        animSet = new AnimatorSet();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        //如果布局里面设置的是固定值,这里取布局里面的固定值;如果设置的是match_parent,则取父布局的大小
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {

            //如果布局里面没有设置固定值,这里取布局的宽度的1/2
            width = widthSize * 1 / 2;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            //如果布局里面没有设置固定值,这里取布局的高度的3/4
            height = heightSize * 3 / 4;
        }
        Log.i("debug", "===================widthMode:" + widthMode + "====heightMode:" + heightMode);
        widthBg = width;
        heightBg = height;
        setMeasuredDimension(width, height);
        startAnim();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制最底层的背景
        radiusBg = widthBg / 20;
        pathBg.moveTo(0, heightBg);
        pathBg.lineTo(0, radiusBg);
        pathBg.quadTo(0, 0, radiusBg, 0);
        pathBg.lineTo(widthBg - radiusBg, 0);
        pathBg.quadTo(widthBg, 0, widthBg, radiusBg);
        pathBg.lineTo(widthBg, heightBg);
        pathBg.lineTo(0, heightBg);
        backgroundPaint.setColor(Color.WHITE);
        canvas.drawPath(pathBg, backgroundPaint);

        //绘制圆弧
        arcPaint.setStrokeWidth(widthBg / 20);
        //设置空心
        arcPaint.setStyle(Paint.Style.STROKE);
        //防抖动
        arcPaint.setDither(true);
        //连接处为圆弧
        arcPaint.setStrokeJoin(Paint.Join.ROUND);
        //画笔的笔触为圆角
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setColor(lineColor);
        //圆弧范围
        arcRect = new RectF(widthBg * 1 / 4, widthBg * 1 / 4, widthBg * 3 / 4, widthBg * 3 / 4);
        //绘制背景大圆弧
        canvas.drawArc(arcRect, 120, 300, false, arcPaint);
        arcPaint.setColor(textColor);
        //绘制分数小圆弧
        canvas.drawArc(arcRect, 120, arcNum, false, arcPaint);
        
        //绘制圆圈内的数字
        textPaint.setColor(textColor);
        textPaint.setTextSize(widthBg / 10);
        canvas.drawText(String.valueOf(walkNum), widthBg * 3 / 8, widthBg * 1 / 2 + 20, textPaint);
        //绘制名次
        textPaint.setTextSize(widthBg / 15);
        canvas.drawText(String.valueOf(rankNum), widthBg * 1 / 2 - 15, widthBg * 3 / 4 + 10, textPaint);
        
        //绘制其他文字
        textPaint.setColor(lineColor);
        textPaint.setTextSize(widthBg / 25);
        canvas.drawText("截止13:45已走", widthBg * 3 / 8 - 10, widthBg * 5 / 12 - 10, textPaint);
        canvas.drawText("好友平均2781步", widthBg * 3 / 8 - 10, widthBg * 2 / 3 - 20, textPaint);
        canvas.drawText("第", widthBg * 1 / 2 - 50, widthBg * 3 / 4 + 10, textPaint);
        canvas.drawText("名", widthBg * 1 / 2 + 30, widthBg * 3 / 4 + 10, textPaint);
        
        //绘制圆圈外的文字
        canvas.drawText("最近7天", widthBg * 1 / 15, widthBg, textPaint);
        myaverageTxt = String.valueOf(averageSize);
        canvas.drawText("平均", widthBg * 10 / 15 - 15, widthBg, textPaint);
        canvas.drawText(myaverageTxt, widthBg * 11 / 15, widthBg, textPaint);
        canvas.drawText("步/天", widthBg * 12 / 15 + 20, widthBg, textPaint);
        
        //绘制虚线
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);
        linePaint.setColor(lineColor);
        linePath.moveTo(widthBg * 1 / 15, widthBg + 80);
        linePath.lineTo(widthBg * 14 / 15, widthBg + 80);
        linePaint.setPathEffect(effects);
        canvas.drawPath(linePath, linePaint);
        
        rectSize = widthBg / 12;
        rectAgHeight = widthBg / 10;
        //绘制虚线上的圆角竖线
        for (int i = 0; i < sizes.size(); i++) {
            rectPaint.setStrokeWidth(widthBg / 25);
            rectPaint.setStyle(Paint.Style.STROKE);
            rectPaint.setStrokeJoin(Paint.Join.ROUND);
            rectPaint.setStrokeCap(Paint.Cap.ROUND);
            float startHeight = widthBg + 90 + rectAgHeight;
            rectPath.moveTo(rectSize, startHeight);
            double percentage = Double.valueOf(sizes.get(i)) / Double.valueOf(averageSize);
            double height = percentage * rectAgHeight;
            rectPath.lineTo(rectSize, (float) (startHeight - height));
            rectPaint.setColor(textColor);
            canvas.drawPath(rectPath, rectPaint);
            //绘制下方的文字
            textPaint.setColor(lineColor);
            canvas.drawText("0" + (i + 1) + "日", rectSize - 25, startHeight + 50, textPaint);
            rectSize += widthBg / 7;
        }
        
        //绘制底部波纹
        weavPaint.setColor(textColor);
        weavPath.reset();
        weavPath.moveTo(0, heightBg);
        weavPath.lineTo(0, heightBg * 10 / 12);
        weavPath.cubicTo(weavX, weavY, widthBg * 3 / 10, heightBg * 11 / 12, widthBg, heightBg * 10 / 12);
        weavPath.lineTo(widthBg, heightBg);
        weavPath.lineTo(0, heightBg);
        canvas.drawPath(weavPath, weavPaint);
         
        //绘制底部文字
        weavPaint.setColor(Color.WHITE);
        weavPaint.setTextSize(widthBg / 20);
        canvas.drawText("成绩不错,继续努力哟!", widthBg * 1 / 10 - 20, heightBg * 11 / 12 + 50, weavPaint);
    }
    
    private void startAnim() {
        //步数动画的实现
        ValueAnimator walkAnimator = ValueAnimator.ofInt(0, mySize);
        walkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                walkNum = (Integer) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        //排名动画的实现
        ValueAnimator rankAnimator = ValueAnimator.ofInt(0, rank);
        rankAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rankNum = (Integer) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        double size = mySize;
        double avgSize = averageSize;
        if (size > avgSize) {
            size = avgSize;
        }
        //圆弧动画的实现
        ValueAnimator arcAnimator = ValueAnimator.ofFloat(0, (float) (size / avgSize * 300));
        arcAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                arcNum = (Float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        //水波纹动画的实现
        ValueAnimator weavXAnimator = ValueAnimator.ofFloat(widthBg * 1 / 10, widthBg * 2/ 10);
        weavXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                weavX = (Float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        ValueAnimator weavYAnimator = ValueAnimator.ofFloat(heightBg*10/12, heightBg*11/12);
        weavYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                weavY = (Float) animation.getAnimatedValue();
                postInvalidate();
            }
        });


        animSet.setDuration(3000);
        animSet.playTogether(walkAnimator, rankAnimator, arcAnimator,weavXAnimator,weavYAnimator);
        animSet.start();

    }

    public void reSet(int mysize, int myrank, int myaverageSize) {
        walkNum = 0;
        arcNum = 0;
        rankNum = 0;
        mySize = mysize;
        rank = myrank;
        averageSize = myaverageSize;
        startAnim();
    }
}
