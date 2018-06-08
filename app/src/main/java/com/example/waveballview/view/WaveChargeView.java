package com.example.waveballview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.waveballview.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class WaveChargeView extends View {
    private static final String TAG = "TAG" + "WaveChargeView";
    //波浪路径及内部填充画笔
    private Paint mPaint;
    //文字画笔
    private Paint mTextPaint;

    private NumberFormat mNumberFormat;

//    private Canvas mCanvas;

    /**
     * 中心圆背景
     */
    private Bitmap mCenterCircleBackgroundBitmap;

    private Bitmap bg;

    private Bitmap mRingBitmap;


    private PorterDuffXfermode mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);


    //宽高
    private float mCenterCircleWidth;
    private float mCenterCircleHeight;
    private float mCenterCircleLeft;
    private float mCenterCircleRight;
    private float mCenterCircleTop;
    private float mCenterCircleBottom;

    private float mProgress = 0;


    /**
     * 波峰高度
     */
    private float mQuadHeight;
    /**
     * 波长的四分之一
     */
    private float mQuadWidth;
    /**
     * 波形曲线起点Y值
     */
    private float mWaveStartY;

    /**
     * 文字定位框
     */
    private Rect mTextRect = new Rect();

    private float mContentWidth;
    private float mContentHeight;

    //闭合波浪路径
    private Path path;

    //当前属性动画的进度值
    private float currentPercent;
    /**
     * 剩余距离
     */
    private String mDistanceString = "";

    private int mTextColor = Color.WHITE;
    //属性动画（计值器）
    private ValueAnimator mValueAnimator;


    private Paint mRingPaint;


    /**
     * 水波填充颜色渐变
     */
    private LinearGradient mLinearGradient;

    /**
     * 波浪填充颜色
     */
    private int mWaveFillColorStart = 0xff81affd;

    /**
     * 波浪填充颜色
     */
    private int mWaveFillColorEnd = 0xff395ba3;


    /**
     * 气泡原图
     */
    private Bitmap mBubbleBitmapOrigin;

    /**
     * 缩放气泡矩阵
     */
    private Matrix mMatrix = new Matrix();

    private List<Bubble> mBubbleList = new ArrayList<>();


    public WaveChargeView(Context context) {
        this(context, null);
    }

    public WaveChargeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public WaveChargeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 进行初始化操作
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {

        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mValueAnimator = ValueAnimator.ofFloat(0f, 1f);
        mValueAnimator.setDuration(3000);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        ValueAnimator.setFrameDelay(500);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int times = 0;
            long last;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long current = System.currentTimeMillis();
                if ((times++) % 2 == 0) {
                    currentPercent = (float) animation.getAnimatedValue();
                    invalidate();
                    Log.d(TAG, "onAnimationUpdate: interval " + (current - last) + " " + currentPercent);
                    last = current;
                    times = times - 2;
                }
            }
        });

//        mCanvas = new Canvas();

        mCenterCircleBackgroundBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.bg_charge);

        mRingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.outside_ring);
        mBubbleBitmapOrigin = BitmapFactory.decodeResource(getResources(), R.drawable.bubble);
        mCenterCircleWidth = mCenterCircleBackgroundBitmap.getWidth();
        mCenterCircleHeight = mCenterCircleBackgroundBitmap.getHeight();
        Log.d(TAG, "init: " + mCenterCircleWidth + " " + mCenterCircleHeight);

        //波浪图形及路径填充画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setDither(true);

        //文字画笔
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);

        mNumberFormat = NumberFormat.getPercentInstance();
        mNumberFormat.setMinimumIntegerDigits(0);

        //闭合波浪路径
        path = new Path();


        mRingPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        for (int i = 0; i < 5; i++) {
            mBubbleList.add(new Bubble());
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mContentWidth = getWidth();
        mContentHeight = getHeight();

        mCenterCircleLeft = mContentWidth / 2 - mCenterCircleWidth / 2;
        mCenterCircleRight = mContentWidth / 2 + mCenterCircleWidth / 2;
        mCenterCircleTop = mContentHeight / 2 - mCenterCircleHeight / 2;
        mCenterCircleBottom = mContentHeight / 2 + mCenterCircleHeight / 2;


        //生成闭合波浪路径
        path.reset();
        float x = mCenterCircleLeft - mCenterCircleWidth;
        //当前x点坐标（根据动画进度水平推移，一个动画周期的距离为一个mWidth）
        x += currentPercent * mCenterCircleWidth;
        //波形的起点
        mWaveStartY = mCenterCircleTop + mCenterCircleHeight * (1 - mProgress);
        Log.d(TAG, "onDraw: " + x);
        path.moveTo(x, mWaveStartY);
        //控制点的相对宽度
        mQuadWidth = mCenterCircleWidth / 4;
//        Log.d(TAG, "getWavePath: " + mQuadWidth);
        //控制点的相对高度
        mQuadHeight = mCenterCircleHeight / 10;
        if (mProgress == 0) {
            mQuadHeight = 0;
        } else if (mProgress == 1) {
            mQuadHeight = 0;
            path.moveTo(x, mWaveStartY - 1);
        }

        //第一个周期
        path.rQuadTo(mQuadWidth, mQuadHeight, mQuadWidth * 2, 0);
        path.rQuadTo(mQuadWidth, -mQuadHeight, mQuadWidth * 2, 0);
        //第二个周期
        path.rQuadTo(mQuadWidth, mQuadHeight, mQuadWidth * 2, 0);
        path.rQuadTo(mQuadWidth, -mQuadHeight, mQuadWidth * 2, 0);

        path.rQuadTo(mQuadWidth, mQuadHeight, mQuadWidth * 2, 0);


        //右侧的封闭直线
        path.lineTo(x + mCenterCircleWidth * 2, mCenterCircleBottom);
        //下边的封闭直线
        path.lineTo(x, mCenterCircleBottom);
        //自动闭合补出左边的直线
        path.close();


        mPaint.setAntiAlias(true);
        int c = canvas.saveLayer(0, 0, mContentWidth, mContentHeight, mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(mCenterCircleBackgroundBitmap, mCenterCircleLeft - 1, mCenterCircleTop - 1, mPaint);

        mPaint.setXfermode(mPorterDuffXfermode);
        //画波浪(dst目标像素)

        mLinearGradient = new LinearGradient(0f, mWaveStartY - mQuadHeight,
                0f, mCenterCircleBottom,
                mWaveFillColorStart, mWaveFillColorEnd,
                Shader.TileMode.CLAMP);


        mPaint.setShader(mLinearGradient);
        canvas.drawPath(path, mPaint);

        mPaint.setXfermode(null);

        canvas.restoreToCount(c);

//        canvas.drawBitmap(bg, 0, 0, null);


        mMatrix.setRotate(360 * currentPercent, mContentWidth / 2, mContentHeight / 2);
        canvas.drawBitmap(mRingBitmap, mMatrix, mRingPaint);


        for (Bubble bubble : mBubbleList) {
            bubble.initBubble();
            if (bubble.progressInit == mProgress) {
                if (bubble.radius > 0) {
                    Bitmap bitmap = resizeImage(mBubbleBitmapOrigin, bubble.radius * 2, bubble.radius * 2);
                    canvas.drawBitmap(bitmap, bubble.x, bubble.y, mRingPaint);
                }
            }
        }


        drawCenterText(canvas, mTextPaint, mNumberFormat.format(mProgress));

    }

    public Bitmap resizeImage(Bitmap bitmap, float w, float h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        float scaleWidth = w / width;
        float scaleHeight = h / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }


    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(float progress) {
        this.mProgress = progress;
        invalidate();
    }


    /**
     * 绘制百分比
     *
     * @param canvas
     * @param textPaint
     * @param text
     */
    private void drawCenterText(Canvas canvas, Paint textPaint, String text) {
        mTextRect.set((int) mCenterCircleLeft, (int) mCenterCircleTop, (int) mCenterCircleRight, (int) mCenterCircleBottom);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(65);
        textPaint.setTypeface(Typeface.DEFAULT);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        int centerY = (int) (mTextRect.centerY() - top / 2 - bottom / 2);
        canvas.drawText(text, mTextRect.centerX(), centerY, textPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(340, 340);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(340, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, 340);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow: ");
        if (!mValueAnimator.isStarted()) {
            mValueAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow: ");
        mValueAnimator.end();
    }


    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.d(TAG, "onWindowVisibilityChanged: " + visibility);
        if (visibility == View.VISIBLE) {
            mValueAnimator.resume();
        } else {
            mValueAnimator.pause();
        }
    }


    private class Bubble {
        private static final String TAG = "TAG" + "Bubble";

        private long time[] = new long[]{1500, 2000, 2500, 3000, 3500};

        private ValueAnimator mValueAnimator;

        private float progressInit;

        /**
         * 气泡生成时的x轴范围
         */
        private float mMinX;
        private float mMaxX;

        private float mUpDistance;

        /**
         * 气泡半径
         */
        private float radius;
        /**
         * 气泡外切正方形左上角x坐标
         */
        private float x;
        /**
         * 气泡外切正方形左上角y坐标
         */
        private float y;

        /**
         * 气泡生成时，其外切正方形左上角y坐标
         */
        private float originY;

        private boolean inited;

        public Bubble() {
            mValueAnimator = ValueAnimator.ofFloat(0, 1);
            mValueAnimator.addUpdateListener(mListener);
            mValueAnimator.setRepeatCount(1);
            mValueAnimator.setInterpolator(new LinearInterpolator());
        }

        private void initBubble() {
            progressInit = mProgress;
            if (!inited) {
                float r = mCenterCircleWidth / 2;
                float centerX = (mCenterCircleLeft + r);
                float centerY = (mCenterCircleTop + r);

                float f = (float) Math.sqrt(r * r - Math.pow(progressInit * mCenterCircleHeight - r, 2));
                mMinX = centerX - f;
                mMaxX = centerX + f;
                radius = generateFloat(1f, 3f);
                if (progressInit < 0.11) {
                    radius = 0;
                } else if (progressInit < 0.15) {
                    radius = generateFloat(1f, 3f);
                    mValueAnimator.setDuration(time[generateInt(0, 2)]);
                } else if (progressInit < 0.2) {
                    radius = generateFloat(3f, 5f);
                    mValueAnimator.setDuration(time[generateInt(0, 2)]);
                } else {
                    radius = generateFloat(5f, 10f);
                    mValueAnimator.setDuration(time[generateInt(0, 5)]);
                }
                x = generateFloat(mMinX + 2 * radius, mMaxX - 4 * radius);

                if (x > centerX) {
                    y = centerY + (float) Math.sqrt((r * r - (centerX - x) * (centerX - x))) - 4 * radius;
                } else {
                    y = centerY + (float) Math.sqrt((r * r - (centerX - x) * (centerX - x))) - 2 * radius;

                }
                originY = y;
                mUpDistance = mCenterCircleHeight * progressInit - (mCenterCircleBottom - y) - mQuadHeight / 2;
                Log.d(TAG, "initBubble: " + mCenterCircleBottom);
                Log.d(TAG, "initBubble: " + x + " " + y + " " + radius + " " + mUpDistance);
                if (mUpDistance < 0) {
                    mUpDistance = 0;
                }
                mValueAnimator.start();
                inited = true;
            }

        }


        private float generateFloat(float min, float max) {
            return min + new Random().nextFloat() * (max - min);
        }

        private int generateInt(int min, int max) {
            return min + ((int) (new Random().nextFloat() * (max - min)));
        }

        private ValueAnimator.AnimatorUpdateListener mListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                Log.d(TAG, "onAnimationUpdate: " + f + " " + mUpDistance + " " + y);
                if ((1 - f) < 0.05) {
                    inited = false;
                    Log.d(TAG, "onAnimationUpdate: " + inited);
                } else {
                    y = originY - mUpDistance * f;
                    Log.d(TAG, "onAnimationUpdate: " + y);
                }
            }
        };
    }

}
