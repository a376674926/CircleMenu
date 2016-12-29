package com.hhj.circlemenu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PathView extends View {
    protected int mLineColor = Color.parseColor("#76f112");
  //背景的坐标
    private int radiusBg = 19, widthBg = 386, heightBg = 652;
    private Path pathBg, linePath,weavPath;
    private Paint backgroundPaint;
    private Paint weavPaint;
    private int textColor;
    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init();
    }
    
    public PathView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init();
    }

    private void init() {
        pathBg = new Path();
        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        //设置画笔style
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setColor(mLineColor);
        backgroundPaint.setStrokeWidth(5);
        weavPaint = new Paint();
        textColor = Color.BLACK;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        //绘制底部波纹
        /*weavPaint.setColor(textColor);
        weavPath.reset();
        weavPath.moveTo(0, heightBg);
        weavPath.lineTo(0, heightBg * 10 / 12);
        weavPath.cubicTo(weavX, weavY, widthBg * 3 / 10, heightBg * 11 / 12, widthBg, heightBg * 10 / 12);
        weavPath.lineTo(widthBg, heightBg);
        weavPath.lineTo(0, heightBg);
        canvas.drawPath(weavPath, weavPaint);*/
        RectF rectF = new RectF(100, 100, 300, 300);
        pathBg.addArc(rectF, 0, 270);
        canvas.drawPath(pathBg, backgroundPaint);
    }
}
