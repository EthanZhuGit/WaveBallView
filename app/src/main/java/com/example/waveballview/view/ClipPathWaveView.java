package com.example.waveballview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.waveballview.R;


public class ClipPathWaveView extends View {
    private static final String TAG = "TAG" + "WaveBezierView";
    //波浪路径及内部填充画笔
    private Paint mPaint;
    /**
     * 中心圆背景
     */
    private Bitmap mCenterCircleBackgroundBitmap;


//    private PorterDuffXfermode mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);


    private int mInsideRingColor = 0xff1c1e2e;
    /**
     * 波浪填充颜色
     */
    private int mWaveFillColor = 0xff446694;
    /**
     * 波浪表层颜色
     */
    private int mWaveSurfaceColor = 0xff1c344d;


    //宽高
    private float mCenterCircleWidth;
    private float mCenterCircleHeight;

    private float mCenterCircleLeft;
    private float mCenterCircleRight;
    private float mCenterCircleTop;
    private float mCenterCircleBottom;

    private float mProgress = 0.5f;


    private float mContentWidth;
    private float mContentHeight;

    //闭合波浪路径
    private Path path;

    private Path mPathWaveSurface;

    private Path mCenterCircleBoundPath;


    //当前属性动画的进度值
    private float currentPercent;


    //属性动画（计值器）
    private ValueAnimator mValueAnimator;

    private boolean mBoolean;

    public ClipPathWaveView(Context context) {
        this(context, null);
    }

    public ClipPathWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ClipPathWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
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

        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int times;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if ((times++) % 1 == 0) {
                    currentPercent = (float) animation.getAnimatedValue();
                    Log.d(TAG, "onAnimationUpdate: " + currentPercent);
                    invalidate();
                    times = times - 1;
                }
            }
        });


        mCenterCircleBackgroundBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.a_icon_log_transparent);

        mCenterCircleWidth = mCenterCircleBackgroundBitmap.getWidth();
        mCenterCircleHeight = mCenterCircleBackgroundBitmap.getHeight();

        //波浪图形及路径填充画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setDither(true);


        //闭合波浪路径
        path = new Path();

        mPathWaveSurface = new Path();
        mCenterCircleBoundPath = new Path();

//        setBackground(context.getDrawable(R.drawable.a_wave_bg));

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
        mPathWaveSurface.reset();
        float x = mCenterCircleLeft - mCenterCircleWidth;
        //当前x点坐标（根据动画进度水平推移，一个动画周期的距离为一个mWidth）
        x += currentPercent * mCenterCircleWidth;
        //波形的起点
        Log.d(TAG, "onDraw: " + x);
        path.moveTo(x, mCenterCircleTop + mCenterCircleHeight * (1 - mProgress));
        //控制点的相对宽度
        float quadWidth = mCenterCircleWidth / 4;
//        Log.d(TAG, "getWavePath: " + quadWidth);
        //控制点的相对高度
        float quadHeight = mCenterCircleHeight / 10;
        if (mProgress == 0 || mProgress == 1) {
            quadHeight = 0;
        }

        //第一个周期
        path.rQuadTo(quadWidth, quadHeight, quadWidth * 2, 0);
        path.rQuadTo(quadWidth, -quadHeight, quadWidth * 2, 0);
        //第二个周期
        path.rQuadTo(quadWidth, quadHeight, quadWidth * 2, 0);
        path.rQuadTo(quadWidth, -quadHeight, quadWidth * 2, 0);

//        path.rQuadTo(quadWidth, quadHeight, quadWidth * 2, 0);

        mPathWaveSurface = path;

        //右侧的封闭直线
        path.lineTo(x + mCenterCircleWidth * 2, mCenterCircleBottom);
        //下边的封闭直线
        path.lineTo(x, mCenterCircleBottom);
        //自动闭合补出左边的直线
        path.close();

        int c = canvas.saveLayer(0, 0, mContentWidth, mContentHeight, mPaint, Canvas.ALL_SAVE_FLAG);
//        canvas.drawBitmap(mCenterCircleBackgroundBitmap, mCenterCircleLeft, mCenterCircleTop, mPaint);

//        mPaint.setXfermode(mPorterDuffXfermode);
//        //画波浪(dst目标像素)


        mCenterCircleBoundPath.addCircle(mContentWidth / 2, mContentHeight / 2, mCenterCircleWidth / 2, Path.Direction.CW);
        canvas.clipPath(mCenterCircleBoundPath);




        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mWaveSurfaceColor);
        canvas.drawPath(mPathWaveSurface, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mWaveFillColor);
        canvas.drawPath(path, mPaint);
//        mPaint.setXfermode(null);
//        canvas.restoreToCount(c);
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


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, 300);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, 300);
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
}
