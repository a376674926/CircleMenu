
package com.hhj.circlemenu;

import android.R.id;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CircleMenuView extends View {
    // 画布
    private Canvas mCanvas;
    // 绘制中间文本的paint
    private TextPaint mTextPaint;
     // 绘制进度文本的paint
    private TextPaint mProgressTextPaint;
    // 绘制内圆的画笔
    private Paint mCirclePaint;
    // 最外层园的半径
    private int mRadius;

    // 角度增量
    private float mDeltaAngle = 60;
    // 边距
    private int minpadding;
    // 绘制弧形的画笔
    private Paint mArcPaint;
    // 分割矩形的画笔
    private Paint mRectPaint;
    // 中心点X，Y坐标
    private float x;
    private float y;
    // 画布宽度
    private int width;
    private CircleMenuClickListener listener;

    // 盘块颜色
    private String[] mMenuItemBackground = {
            "#60A2D0", "#5F9576", "#C5CD74", "#E3BD65", "#DE8C5F", "#D45F5E"
    };
    // 盘块的范围
    private RectF mRange;
    // 开始角度
    private float mStartAngle;
    // 指针
    private Path mRrianglePath;
    private Paint mRrianglePaint;
    // 旋转开始
    private float mRotateStartAngle;
    //动画每次更新帧的结束角度
    private float mRotateEndAngle;
    //旋转最终角度
    private float mRotateLastAngle;
    // 进度text
    private String mProgressText;
    // 默认盘块数
    private static final int COUNT = 7;
    // 盘块的个数
    private int mcount;
    // 盘块文本大小
    private float mBooldPressureTextSize;
    private CharSequence mBooldPressureText[];
    // 分割空隙颜色
    private int mGapColor;
    // 进度颜色
    private int mProgressTextColor = Color.parseColor("#60A2D0");
    // 内圆半径
    private float mInsideRadius;
    // 分割的偏移量
    private int mDeltaPadding = dip2px(getContext(), 3);
    // 半透明内圆画笔
    private Paint mStrokePaint;
    // 中间文本
    private String mCenterText;
    //血压范围
    private Integer[] mLowRange = {0,89};
    private Integer[] mNormalRange = {90,129};
    private Integer[] mNormalHighRange = {130,139};
    private Integer[] mMildHighRange = {140,159};
    private Integer[] mModerateHighRange = {160,179};
    private Integer[] mSevereHighRange = {180,199};
    private List<Integer[]> mBooldPressureRanges = new ArrayList<Integer[]>();
    //小圆圈画笔
    private Paint mStartInsideCirclePaint;
    private Paint mStartOuterCirclePaint;
    //指向盘块位置
    private int mMenuItemIndex;
    //是否点击开始检测
    private boolean mIsClickStart;
    private AnimatorSet mAnimatorSet;
    //开始检测时小圆圈移动当前位置
    private Point mCurrentPoint;
    //开始小圆圈半径‘
    private int mSmallOuterRadius;
    private int mSmallInsideRadius;
    //开始小圆圈颜色
    private int mSmallCircleColor = Color.parseColor("#98877D");
    //执行盘块文本颜色
    private int mMenuItemTextColor = Color.parseColor("#60A2D0");;
    
    public CircleMenuView(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public CircleMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
        initAttrs(context, attrs);
        init();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.CircleMenuView);
            mBooldPressureTextSize = typedArray.getDimension(
                    R.styleable.CircleMenuView_menu_text_size, sp2px(getContext(), 12));
            mBooldPressureText = typedArray.getTextArray(R.styleable.CircleMenuView_menu_text);
            mGapColor = typedArray.getColor(R.styleable.CircleMenuView_gap_color, getResources()
                    .getColor(R.color.default_backgrouncolor));
            mCenterText = typedArray.getString(R.styleable.CircleMenuView_center_text);
            mInsideRadius = (int) typedArray.getDimension(
                    R.styleable.CircleMenuView_inside_cirle_radius, 0);
            mDeltaPadding = (int) typedArray.getDimension(R.styleable.CircleMenuView_gap_size,
                    dip2px(getContext(), 2));
            TypedArray ar = getResources().obtainTypedArray(
                    typedArray.getResourceId(R.styleable.CircleMenuView_menu_item_background, 0));
            int len = ar.length();
            mMenuItemBackground = new String[len];
            for (int i = 0; i < len; i++)
                mMenuItemBackground[i] = ar.getString(i);
            ar.recycle();
            typedArray.recycle();
        } else {
            mGapColor = getResources().getColor(R.color.default_backgrouncolor);
            mBooldPressureTextSize = sp2px(getContext(), 12);
            mCenterText = "";
            mInsideRadius = 0;
            mDeltaPadding = dip2px(getContext(), 10);
        }
    }

    private void init() {
        mBooldPressureRanges.add(mLowRange);
        mBooldPressureRanges.add(mNormalRange);
        mBooldPressureRanges.add(mNormalHighRange);
        mBooldPressureRanges.add(mMildHighRange);
        mBooldPressureRanges.add(mModerateHighRange);
        mBooldPressureRanges.add(mSevereHighRange);
        int length = mBooldPressureText.length > 0 ? mBooldPressureText.length + 1 : COUNT;
        mcount = length;
        mDeltaAngle = 360.0f / mcount;
        mRotateStartAngle = 90 + mDeltaAngle / 2;
        mRotateEndAngle = 90 + mDeltaAngle / 2;
        mProgressText = "100%";
        mCenterText = "";
        mSmallOuterRadius = dip2px(getContext(), 23);
        mSmallInsideRadius = dip2px(getContext(), 20);
        mAnimatorSet = new AnimatorSet();
        Log.i("debug", "=====initSomeThing==========mDeltaAngle:" + mDeltaAngle + "==mStartAngle:"
                + mStartAngle);
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);// 抗锯齿
        mArcPaint.setDither(true);// 防抖动
        /*
         * mArcPaint.setColor(menuItemBackground);
         * mArcPaint.setStyle(Paint.Style.STROKE);// 设置画笔为填充
         * mArcPaint.setStrokeWidth(70);
         */
        /*
         * mRange = new RectF(minpadding, minpadding, minpadding + radius * 2,
         * minpadding + radius * 2);
         */

        // 绘制内圆(*)
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint
                .setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        // 初始化分割矩形
        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setDither(true);
        mRectPaint.setColor(mGapColor);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(mDeltaPadding);
        mRectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        // 初始化中间文本的paint
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setColor(mMenuItemTextColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(sp2px(getContext(), 23));
        mTextPaint.setTextAlign(Paint.Align.CENTER);// 从字符中心
        // 初始化进度文本的paint
        mProgressTextPaint = new TextPaint();
        mProgressTextPaint.setAntiAlias(true);
        mProgressTextPaint.setDither(true);
        mProgressTextPaint.setColor(mProgressTextColor);
        mProgressTextPaint.setStyle(Paint.Style.FILL);
        mProgressTextPaint.setTextSize(sp2px(getContext(), 23));
        mProgressTextPaint.setTextAlign(Paint.Align.CENTER);// 从字符中心
        // 初始化中心圆扫边画笔
        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setDither(true);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(dip2px(getContext(), 12));
        mStrokePaint.setColor(Color.WHITE);
        // 三角指针画笔
        mRrianglePaint = new Paint();
        mRrianglePaint.setDither(true);
        mRrianglePaint.setAntiAlias(true);
        mRrianglePaint.setStyle(Paint.Style.FILL);
        mRrianglePaint.setColor(Color.WHITE);
        mRrianglePath = new Path();
        //开始小圆圈
        mStartInsideCirclePaint = new Paint();
        mStartInsideCirclePaint.setDither(true);
        mStartInsideCirclePaint.setAntiAlias(true);
        mStartInsideCirclePaint.setStyle(Paint.Style.FILL);
        mStartInsideCirclePaint.setColor(mSmallCircleColor);
        
        mStartOuterCirclePaint = new Paint();
        mStartOuterCirclePaint.setDither(true);
        mStartOuterCirclePaint.setAntiAlias(true);
        mStartOuterCirclePaint.setStyle(Paint.Style.STROKE);
        mStartOuterCirclePaint.setColor(mSmallCircleColor);
    }

    /**
     * 绘制操作
     */
    private void adraw(Canvas canvas) {
        // dosomething
        this.mCanvas = canvas;
        mRange = new RectF(minpadding, minpadding, minpadding + mRadius * 2,
                minpadding + mRadius * 2);
        drawOuterArc();
        drawNeiCicle();
        drawStrokeLine();
        drawGapRect();
        drawPointer();
        drawText();
        drawStartCircle();
    }

    /**
     * 开始时小圆圈
     */
    private void drawStartCircle() {
        if(mIsClickStart && mCurrentPoint != null){
            mCanvas.drawCircle(mCurrentPoint.getX(), mCurrentPoint.getY(), mSmallInsideRadius, mStartInsideCirclePaint);
            mCanvas.drawCircle(mCurrentPoint.getX(), mCurrentPoint.getY(), mSmallOuterRadius, mStartOuterCirclePaint);
        }
    }

    /**
     * 绘制指针
     */
    private void drawPointer() {
//        Log.i("debug", "===========drawPointer=======@@@@========mRotateStartAngle:" + mRotateStartAngle + "===mRotateEndAngle:" + mRotateEndAngle);
        mCanvas.save();
        mCanvas.rotate(mRotateEndAngle - mRotateStartAngle, x, y);
        mStartAngle = 90 + mDeltaAngle / 2;
        // float angleStart = (float) ((mStartAngle+mDeltaAngle/2 -
        // 10)*Math.PI/180);//圆弧上起始点
        float angleStart = (float) ((mRotateStartAngle - 12) * Math.PI / 180);
        float startX = (float) (x + mInsideRadius * Math.cos(angleStart));
        float startY = (float) (y + mInsideRadius * Math.sin(angleStart));

        // float angleEnd = (float) ((mStartAngle+mDeltaAngle/2 +
        // 10)*Math.PI/180);//圆弧上终点
        float angleEnd = (float) ((mRotateStartAngle + 12) * Math.PI / 180);
        float endX = (float) (x + mInsideRadius * Math.cos(angleEnd));
        float endY = (float) (y + mInsideRadius * Math.sin(angleEnd));

        // float angleMiddle = (float)
        // ((mStartAngle+mDeltaAngle/2)*Math.PI/180);//圆弧上终点
        float angleMiddle = (float) ((mRotateStartAngle) * Math.PI / 180);
        int middleX = (int) (x + (mInsideRadius + dip2px(getContext(), 25)) * Math.cos(angleMiddle));// 10顶点到圆弧高度
        int middleY = (int) (y + (mInsideRadius + dip2px(getContext(), 25)) * Math.sin(angleMiddle));
        mRrianglePath.moveTo(startX, startY);
        mRrianglePath.lineTo(middleX, middleY);
        mRrianglePath.lineTo(endX, endY);
        mRrianglePath.close();
        mCanvas.drawPath(mRrianglePath, mRrianglePaint);
        mRrianglePath.reset();//需要重置，否则下次画时坐标不一样会画上次三角指针，此时出现多个指针
        mCanvas.restore();
    }

    /**
     * 绘制外围
     */
    private void drawOuterArc() {
        // 圆弧范围
        mStartAngle = 90 + mDeltaAngle / 2;
        for (int i = 0; i < mcount - 1; i++) {
            if (i == 0) {
                mArcPaint.setColor(Color.parseColor(mMenuItemBackground[0]));
                mCanvas.drawArc(mRange, mStartAngle, mDeltaAngle, true, mArcPaint);
            } else if (i == 1) {
                mArcPaint.setColor(Color.parseColor(mMenuItemBackground[1]));
                mCanvas.drawArc(mRange, mStartAngle, mDeltaAngle, true, mArcPaint);
            } else if (i == 2) {
                mArcPaint.setColor(Color.parseColor(mMenuItemBackground[2]));
                mCanvas.drawArc(mRange, mStartAngle, mDeltaAngle, true, mArcPaint);
            }
            else if (i == 3) {
                mArcPaint.setColor(Color.parseColor(mMenuItemBackground[3]));
                mCanvas.drawArc(mRange, mStartAngle, mDeltaAngle, true, mArcPaint);
            }
            else if (i == 4) {
                mArcPaint.setColor(Color.parseColor(mMenuItemBackground[4]));
                mCanvas.drawArc(mRange, mStartAngle, mDeltaAngle, true, mArcPaint);
            }
            else if (i == 5) {
                mArcPaint.setColor(Color.parseColor(mMenuItemBackground[5]));
                mCanvas.drawArc(mRange, mStartAngle, mDeltaAngle, true, mArcPaint);
            }
            mStartAngle += mDeltaAngle;
        }
    }

    /**
     * 画分割线
     */
    private void drawGapRect() {
        mStartAngle = 90 + mDeltaAngle / 2;
        for (int i = 0; i < mcount - 2; i++) {
            float angle = (float) ((mStartAngle + mDeltaAngle) * Math.PI / 180);
            int neiX = (int) (x + mInsideRadius * Math.cos(angle));
            int neiY = (int) (y + mInsideRadius * Math.sin(angle));
            int waiX = (int) (x + mRadius * Math.cos(angle));
            int waiY = (int) (y + mRadius * Math.sin(angle));
            int deviation = dip2px(getContext(), 2);
            if (neiX > x) {
                neiX -= deviation;
            } else {
                neiX += deviation;
            }
            mCanvas.drawLine(neiX, neiY, waiX, waiY, mRectPaint);
            mStartAngle += mDeltaAngle;
        }
    }

    private void drawText() {
        // 进度
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float textW = fontMetrics.descent - fontMetrics.top;
        float textProgressX = x;
        float textProgressY = y - textW / 2 + mRadius;
        mCanvas.drawText(mProgressText,
                textProgressX, textProgressY,
                mProgressTextPaint);
        
        // 中间文本
        if(mRotateLastAngle == mRotateEndAngle){
            mCenterText = mBooldPressureText[mMenuItemIndex].toString();
            mMenuItemTextColor = Color.parseColor(mMenuItemBackground[mMenuItemIndex]);
        }
        mTextPaint.setColor(mMenuItemTextColor);
        float textBloodX = x;
        float textBloodY = y - textW;
        StaticLayout layout = new StaticLayout(mCenterText, mTextPaint, 300,
                Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        mCanvas.save();
        mCanvas.translate(textBloodX, textBloodY);// 从100，100开始画
        layout.draw(mCanvas);
        mCanvas.restore();
    }

    /**
     * 绘制半透明内圆弧
     */
    private void drawStrokeLine() {
        mStrokePaint.setAlpha(30);
        mCanvas.drawCircle(x, y, mInsideRadius + 3, mStrokePaint);

    }

    /**
     * 绘制内圆
     */
    private void drawNeiCicle() {
        mCirclePaint.setColor(Color.WHITE);
        mCanvas.drawCircle(x, y, mInsideRadius, mCirclePaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO 自动生成的方法存根
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 将画布设为正方形
        int mWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();
        if (mWidth > mHeight) {
            mWidth = mHeight;
        }
        width = mWidth;
        minpadding = getPaddingLeft();
        // 半径
        mRadius = (width - minpadding * 2) / 2;
        x = width / 2;
        y = x;
        if (mInsideRadius == 0) {
            mInsideRadius = mRadius / 3;
        }
        // 设置画布宽高
        setMeasuredDimension(width, width - minpadding + 3);
        startPointerAnim();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 计算坐标范围，判断坐标在那个范围内
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 内圆点击事件
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_UP:
                Region[] regions = new Region[1];
                Region re = new Region();
                Path path = new Path();
                path.addCircle(x, y, mInsideRadius, Path.Direction.CW);
                // 构造一个区域对象，左闭右开的。
                RectF r = new RectF();
                // 计算控制点的边界
                path.computeBounds(r, true);
                // 设置区域路径和剪辑描述的区域
                re.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right,
                        (int) r.bottom));
                regions[0] = re;
                float x = event.getX();
                float y = event.getY();
                for (int i = 0; i < regions.length; i++) {
                    if (regions[i].contains((int) x, (int) y)) {
                        mIsClickStart = true;
                        startSmallCircleAnim();
                    }
                }
                return true;
        }
        return true;
    }

    public void setOnClickListener(CircleMenuClickListener l) {
        listener = l;
    }

    public interface CircleMenuClickListener {
        void onClick(View v, int position);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        adraw(canvas);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public float getInsideCircleRadius() {
        return mInsideRadius;
    }

    public CircleMenuView setInsideCircleRadius(float insideRadius) {
        this.mInsideRadius = insideRadius;
        postInvalidate();
        return this;
    }

    public float getMenuTextSize() {
        return mBooldPressureTextSize;
    }

    public CircleMenuView setMenuTextSize(float textsize) {
        this.mBooldPressureTextSize = textsize;
        postInvalidate();
        return this;
    }

    public CharSequence[] getMenuTexts() {
        return mBooldPressureText;
    }

    public CircleMenuView setMenuTexts(CharSequence[] text) {
        this.mBooldPressureText = text;
        postInvalidate();
        return this;
    }

    public int getGapSize() {
        return mDeltaPadding;
    }

    public CircleMenuView setGapSize(int deltaPadding) {
        this.mDeltaPadding = deltaPadding;
        postInvalidate();
        return this;
    }

    public String[] getMenuItemBackground() {
        return mMenuItemBackground;
    }

    public CircleMenuView setMenuItemBackground(String[] menuItemBackground) {
        this.mMenuItemBackground = menuItemBackground;
        postInvalidate();
        return this;
    }

    public int getGapColor() {
        return mGapColor;
    }

    public CircleMenuView setGapColor(int gapColor) {
        this.mGapColor = gapColor;
        postInvalidate();
        return this;
    }

    public String getCenterText() {
        return mCenterText;
    }

    public CircleMenuView setCenterText(String centerText) {
        this.mCenterText = centerText;
        postInvalidate();
        return this;
    }

    public CircleMenuView setWidthAndHeight(int w, int h) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(w, h);
        setLayoutParams(params);
        postInvalidate();
        return this;
    }

    public String getProgressText() {
        return mProgressText;
    }

    public void setProgressText(String mProgressText) {
        this.mProgressText = mProgressText;
        postInvalidate();
    }
   //指针旋转动画
    private void startPointerAnim() {
        ValueAnimator bloodAnimator = ValueAnimator.ofFloat(mRotateStartAngle,
                mRotateLastAngle);
        bloodAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRotateEndAngle = (Float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        bloodAnimator.setDuration(500);
        bloodAnimator.start();
    }
    
    //开始检测小圆圈移动动画
    private void startSmallCircleAnim() {
        Point point1 = new Point(width-mSmallOuterRadius, width-mSmallOuterRadius);  
        Point point2 = new Point(x, y);
        ValueAnimator animator = ValueAnimator.ofObject(new PointEvaluator(), point1,point2);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentPoint = (Point) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mAnimatorSet.removeAllListeners();
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                super.onAnimationEnd(animation);
                mIsClickStart = false;
                if (listener != null) {
                    listener.onClick(CircleMenuView.this, 0);
                }
            }
        });
        mAnimatorSet.setDuration(500);
        mAnimatorSet.play(animator);
        mAnimatorSet.start();
        
    }

    //检测收缩压
    public void setSystolicBloodPressure(int systolicBloodPressure) {
        if(mRotateLastAngle != 0.0f){
            mRotateStartAngle = mRotateLastAngle;  
        }
        for(int i = 0 ;i < mBooldPressureRanges.size();i++){
            Integer[] booldPressureRange = mBooldPressureRanges.get(i);
            if(booldPressureRange[0] <= systolicBloodPressure && booldPressureRange[1] >= systolicBloodPressure){
                int offsetAngle = systolicBloodPressure - booldPressureRange[0];
                mRotateLastAngle = offsetAngle * (mDeltaAngle / (booldPressureRange[1] - booldPressureRange[0])) + i * mDeltaAngle
                               +  90 + mDeltaAngle/2;
                mMenuItemIndex = i;
                break;
            }
        }
        mProgressText = "10%";
        startPointerAnim();
    }
    
    //开启自动检测
    public void startAutoDetect(){
        mIsClickStart = true;
        startSmallCircleAnim();
    }

}
