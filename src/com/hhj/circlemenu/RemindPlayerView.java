
package com.hhj.circlemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.PrivateCredentialPermission;

public class RemindPlayerView extends View {

    /**
     * Paint for drawing left and passed time.
     */
    private Paint mPaintTime;

    /**
     * RectF for draw circle progress.
     */

    /**
     * Modified OnClickListener. We do not want all view click. notify onClick()
     * only button area touched.
     */
    private OnClickListener onClickListener;

    /**
     * Play/Pause button region for handle onTouch
     */
    private Region mButtonRegion;

    /**
     * Play icon will be converted to Bitmap
     */
    private Bitmap mBitmapPlay;

    /**
     * Pause icon will be converted to Bitmap
     */
    private Bitmap mBitmapPause;

    /**
     * Paint for drawing play/pause icons to canvas.
     */
    private Paint mPaintPlayPause;

    /**
     * Image Height and Width values.
     */
    private int mHeight;
    private int mWidth;

    /**
     * Center values for cover image.
     */
    private float mCenterX;
    private float mCenterY;

    /**
     * Handler for posting runnable object
     */
    private Handler mHandlerProgress;

    /**
     * Runnable for turning image (default velocity is 10)
     */
    private Runnable mRunnableProgress;

    /**
     * isPlaying
     */
    private boolean isPlaying;

    /**
     * 1 sn = 1000 ms
     */
    private static int PROGRESS_SECOND_MS = 500;

    /**
     * Default color code for cover
     */
    private int mCoverColor = Color.parseColor("#E1E1E1");
    /* private int mCoverColor = Color.GRAY; */
    /**
     * Play/Pause button radius.(default = 120)
     */
    private float mButtonRadius = 120f;

    /**
     * Play/Pause button color(Default = dark gray)
     */
    private int mButtonColor = Color.DKGRAY;

    /**
     * Time text size
     */
    private int mTextSize = 40;

    /**
     * Default text color
     */
    private int mTextColor = 0xFFFFFFFF;

    /**
     * Current progress value
     */
    private int currentProgress = 0;

    /**
     * Max progress value
     */
    private int maxProgress = 100;

    /**
     * Progressview and time will be visible/invisible depends on this
     */
    private boolean mProgressVisibility = true;

    // 音频封面画笔
    private Paint mAudioCoverPaint;
    private int mCoverStrokeWidth;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat();

    /**
     * Constructor
     * 
     * @param context
     */
    public RemindPlayerView(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * Constructor
     * 
     * @param context
     * @param attrs
     */
    public RemindPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Constructor
     * 
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public RemindPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Constructor
     * 
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    public RemindPlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * Initializes resource values, create objects which we need them later.
     * Object creation must not called onDraw() method, otherwise it won't be
     * smooth.
     * 
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {

        // Get Image resource from xml
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.playerview);
        mButtonColor = a.getColor(R.styleable.playerview_buttonColor, mButtonColor);
        mTextColor = a.getColor(R.styleable.playerview_textColor, mTextColor);
        mTextSize = a.getDimensionPixelSize(R.styleable.playerview_textSize, mTextSize);
        a.recycle();

        // Handler and Runnable object for progressing.
        mHandlerProgress = new Handler();

        mRunnableProgress = new Runnable() {
            @Override
            public void run() {
                if (isPlaying) {
                    postInvalidate();
                    mHandlerProgress.postDelayed(mRunnableProgress, PROGRESS_SECOND_MS);
                }
            }
        };

        // Play/Pause button icons paint and bitmaps
        mPaintPlayPause = new Paint();
        mPaintPlayPause.setAntiAlias(true);
        mBitmapPlay = BitmapFactory.decodeResource(getResources(), R.drawable.icon_play);
        mBitmapPause = BitmapFactory.decodeResource(getResources(), R.drawable.icon_pause);

        mPaintTime = new Paint();
        mPaintTime.setColor(mTextColor);
        mPaintTime.setDither(true);
        mPaintTime.setAntiAlias(true);
        mPaintTime.setTextSize(mTextSize);
        mPaintTime.setTextAlign(Paint.Align.CENTER);// 从字符中心

        // 初始化音频封面画笔
        mCoverStrokeWidth = dip2px(getContext(), 7);
        mAudioCoverPaint = new Paint();
        mAudioCoverPaint.setDither(true);
        mAudioCoverPaint.setAntiAlias(true);
        mAudioCoverPaint.setColor(mCoverColor);
        mAudioCoverPaint.setStrokeWidth(mCoverStrokeWidth);
    }

    /**
     * Calculate mWidth, mHeight, mCenterX, mCenterY values and scale resource
     * bitmap. Create shader. This is not called multiple times.
     * 
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        this.setMeasuredDimension(mWidth, mHeight);

        mCenterX = mWidth / 2f;
        mCenterY = mHeight / 2f;

        // button size is about to 1/4 of image size then we divide it to 8.
        mButtonRadius = mWidth / 8.0f;

        mButtonRegion = new Region((int) (mCenterX - mButtonRadius),
                (int) (mCenterY - mButtonRadius),
                (int) (mCenterX + mButtonRadius),
                (int) (mCenterY + mButtonRadius));

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * This is where magic happens as you know.
     * 
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw audio cover
        drawAutoCover(canvas);

        canvas.drawBitmap(isPlaying() ? mBitmapPause : mBitmapPlay,
                mCenterX - mBitmapPause.getWidth() / 2f,
                mCenterY - mBitmapPause.getHeight() / 2f,
                mPaintPlayPause);

        drawTextTime(canvas);

    }

    private void drawTextTime(Canvas canvas) {
        // Draw left time text
        String leftTime = millisecondsToTime(calculateLeftSeconds());
        Paint.FontMetrics fontMetrics = mPaintTime.getFontMetrics();

        float textH = fontMetrics.descent - fontMetrics.top;
        float textW = mPaintTime.measureText(leftTime);
        float textLeftTimeX = mWidth - textW / 2 - 5;
        float textLeftTimeY = mHeight - textH / 2;
        canvas.drawText(leftTime,
                textLeftTimeX, textLeftTimeY,
                mPaintTime);

        // Draw passed time text
        String passedTime = "";
        if(calculatePassedSeconds() >= maxProgress){
            passedTime = millisecondsToTime(calculatePassedSeconds());
        }else{
            passedTime = millisecondsToTime(calculatePassedSeconds() + 500);
        }
        float textPassedTimeX = textW / 2 + 5;
        canvas.drawText(passedTime,
                (float) textPassedTimeX,
                (float) textLeftTimeY,
                mPaintTime);
    }

    // 画音频封面
    private void drawAutoCover(Canvas canvas) {
        canvas.save();
        int i = 0;
        while (i < 30) {
            canvas.translate(7, 0);
            float viewHeight = (float) (Math.random() * mHeight);
            canvas.drawLine(0, mHeight, 0, viewHeight, mAudioCoverPaint);
            i++;
        }
        canvas.restore();
    }

    /**
     * Checks is rotating
     * 
     * @return
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Start turning image
     */
    public void start() {
        isPlaying = true;
        mHandlerProgress.removeCallbacksAndMessages(null);
        mHandlerProgress.postDelayed(mRunnableProgress, PROGRESS_SECOND_MS);
        postInvalidate();
    }

    /**
     * Stop turning image
     */
    public void stop() {
        isPlaying = false;
        postInvalidate();
    }

    /**
     * This is detect when mButtonRegion is clicked. Which means play/pause
     * action happened.
     * 
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                return true;
            }
            case MotionEvent.ACTION_UP: {
                if (mButtonRegion.contains((int) x, (int) y)) {
                    if (onClickListener != null)
                        onClickListener.onClick(this);
                }
            }
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * onClickListener.onClick will be called when button clicked. We dont want
     * all view click. We only want button area click. That is why we override
     * it.
     * 
     * @param l
     */
    @Override
    public void setOnClickListener(OnClickListener l) {
        onClickListener = l;
    }

    /**
     * Sets total seconds of music
     * 
     * @param maxProgress
     */
    public void setMax(int maxProgress) {
        this.maxProgress = maxProgress;
        postInvalidate();
    }

    /**
     * Sets current seconds of music
     * 
     * @param currentProgress
     */
    public void setProgress(int currentProgress) {
        /*
         * if(0 <= currentProgress && currentProgress<= maxProgress){
         * this.currentProgress = currentProgress; postInvalidate(); }
         */
        this.currentProgress = currentProgress;
        postInvalidate();
    }

    /**
     * Get current progress seconds
     * 
     * @return
     */
    public int getProgress() {
        return currentProgress;
    }

    /**
     * Calculate left seconds
     * 
     * @return
     */
    private int calculateLeftSeconds() {
        return maxProgress - currentProgress;
    }

    /**
     * Return passed seconds
     * 
     * @return
     */
    private int calculatePassedSeconds() {
        return currentProgress;

    }

    /**
     * Convert millisecond to time
     * 
     * @param seconds
     * @return
     */
    private String millisecondsToTime(long milliseconds) {
        float hour = milliseconds / (24 * 60 * 1000);
        if (hour >= 1) {
            mDateFormat.applyPattern("HH:mm:ss");
            return mDateFormat.format(new Date(milliseconds));
        }
        mDateFormat.applyPattern("mm:ss");
        return mDateFormat.format(new Date(milliseconds));

    }

    /**
     * Sets time text color
     * 
     * @param color
     */
    public void setTimeColor(int color) {
        mTextColor = color;
        mPaintTime.setColor(mTextColor);
        postInvalidate();
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
}
