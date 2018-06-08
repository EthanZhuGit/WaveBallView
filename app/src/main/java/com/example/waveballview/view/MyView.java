package com.example.waveballview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.waveballview.R;

public class MyView extends View {
    private static final String TAG = "TAG" + "MyView";
    //波浪路径及内部填充画笔
    private Paint mPaint;

    private int mContentWidth;
    private int mContentHeight;

    private Bitmap mCenterCircleBackgroundBitmap;

    private Bitmap mBubbleBitmap;

    private Bitmap mRingBitmap;

    private float mCenterCircleWidth;
    private float mCenterCircleHeight;
    private float mCenterCircleLeft;
    private float mCenterCircleRight;
    private float mCenterCircleTop;
    private float mCenterCircleBottom;


    private Matrix mMatrix;

    private Path mPath;

    private float mProgress;

    private ValueAnimator mValueAnimator;

    public MyView(Context context) {
        super(context);
        init(null, 0);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        mCenterCircleBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_charge);
        mCenterCircleWidth = mCenterCircleBackgroundBitmap.getWidth();
        mCenterCircleHeight = mCenterCircleBackgroundBitmap.getHeight();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);


        mPath = new Path();
        mBubbleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bubble);
        mRingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.outside_ring);
        mMatrix = new Matrix();

        mValueAnimator = ValueAnimator.ofFloat(0f, 1f);
        mValueAnimator.setDuration(3000);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);

        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress += 0.05f;
                Log.d(TAG, "onClick: " + mProgress + " " + mProgress * 360);
                invalidate();
            }
        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: ");
        mContentWidth = getWidth();
        mContentHeight = getHeight();

        mCenterCircleLeft = mContentWidth / 2 - mCenterCircleWidth / 2;
        mCenterCircleRight = mContentWidth / 2 + mCenterCircleWidth / 2;
        mCenterCircleTop = mContentHeight / 2 - mCenterCircleHeight / 2;
        mCenterCircleBottom = mContentHeight / 2 + mCenterCircleHeight / 2;

        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.FILL);
        mPath.moveTo(mCenterCircleLeft, mContentHeight / 2);
        float w = mCenterCircleWidth / 4;
        float h = mCenterCircleHeight / 5;
        mPath.cubicTo(mCenterCircleLeft + w, mCenterCircleTop + h,
                mCenterCircleRight - w, mCenterCircleBottom - h,
                mCenterCircleRight, mContentHeight / 2);
        mPath.lineTo(mCenterCircleRight, mCenterCircleBottom);
        mPath.lineTo(mCenterCircleLeft, mCenterCircleBottom);
        mPath.close();

        int rW = mRingBitmap.getWidth();
        int rH = mRingBitmap.getHeight();
        canvas.save();
        canvas.rotate(mProgress * 360, mContentWidth / 2, mContentHeight / 2);
        mPaint.setFilterBitmap(true);
        canvas.drawBitmap(mRingBitmap, mContentWidth / 2 - rW / 2, mContentHeight / 2 - rH / 2, mPaint);
        canvas.restore();

        int c = canvas.saveLayer(0, 0, mContentWidth, mContentHeight, mPaint, Canvas.ALL_SAVE_FLAG);

        canvas.drawBitmap(mCenterCircleBackgroundBitmap, mCenterCircleLeft, mCenterCircleTop, mPaint);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawPath(mPath, mPaint);


        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        canvas.drawBitmap(mBubbleBitmap, mCenterCircleLeft, mContentHeight / 2 - 15, mPaint);

        mPaint.setXfermode(null);
        canvas.restoreToCount(c);


//        mMatrix.setRotate(mProgress * 360, mContentWidth / 2, mContentHeight / 2);
//        mMatrix.postTranslate(-rW / 2, -rH / 2);
//        canvas.drawBitmap(mRingBitmap, mMatrix, mPaint);

//        drawRotateBitmap(canvas, mPaint, mRingBitmap, mProgress * 360,
//                mContentWidth / 2 - rW / 2, mContentHeight / 2 - rH / 2);
    }

    private void drawRotateBitmap(Canvas canvas, Paint paint, Bitmap bitmap,
                                  float rotation, float posX, float posY) {
        Matrix matrix = new Matrix();
        int offsetX = bitmap.getWidth() / 2;
        int offsetY = bitmap.getHeight() / 2;
        matrix.postTranslate(-offsetX, -offsetY);
        matrix.postRotate(rotation);
        matrix.postTranslate(posX + offsetX, posY + offsetY);
        canvas.drawBitmap(bitmap, matrix, paint);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        mValueAnimator.start();
    }

}
