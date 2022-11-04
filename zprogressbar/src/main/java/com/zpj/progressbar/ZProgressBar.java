package com.zpj.progressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

public class ZProgressBar extends View {

    private static final Float CIRCULAR = 360f;

    /**
     * 画笔
     */
    private final Paint mPaint = new Paint();
    private final Paint mTextPaint = new Paint();
    private final RectF mRect = new RectF();

    private float mProgress;
    private float mMinProgress;
    private float mMaxProgress;

    private float mProgressBarWidth;
    private float mBorderWidth;

    private int mBackgroundColor;
    private int mProgressBarColor;
    private int mBorderColor;

    private float mWidth;
    private float mHeight;
    private float mRadius;

    private boolean showProgressText;
    private boolean autoTextSize;
    private float mProgressTextSize;
    private int mProgressTextColor;

    private float mStartAngle = -90f;

    private boolean mIsIntermediateMode;
    private ValueAnimator valueAnimator;


    public ZProgressBar(Context context) {
        this(context, null);
    }

    public ZProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int defaultProgressBarWidth = (int) (context.getResources().getDisplayMetrics().density * 4 + 0.5f);
        int defaultRadius = defaultProgressBarWidth * 5;


        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZProgressBar);
        mMaxProgress = typedArray.getInt(R.styleable.ZProgressBar_zbp_max_progress, 100);
        mMinProgress = typedArray.getInt(R.styleable.ZProgressBar_zbp_min_progress, (int) (mMaxProgress / 10));
        mProgress = typedArray.getInt(R.styleable.ZProgressBar_zbp_progress, (int) (mMaxProgress / 2));
        mRadius = typedArray.getDimensionPixelSize(R.styleable.ZProgressBar_zbp_radius, defaultRadius);
        mBackgroundColor = typedArray.getColor(R.styleable.ZProgressBar_zbp_background_color, Color.TRANSPARENT);
        mBorderColor = typedArray.getColor(R.styleable.ZProgressBar_zbp_border_color, mBackgroundColor);
        mProgressBarWidth = typedArray.getDimensionPixelSize(R.styleable.ZProgressBar_zbp_progress_bar_width, defaultProgressBarWidth);
        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.ZProgressBar_zbp_border_width, 0);
        if (mBorderWidth <= 0) {
            mBorderWidth = mProgressBarWidth;
        }
        mProgressBarColor = typedArray.getColor(R.styleable.ZProgressBar_zbp_progress_bar_color, getColorPrimary());
        mIsIntermediateMode = typedArray.getBoolean(R.styleable.ZProgressBar_zbp_intermediate_mode, true);

        mProgressTextColor = typedArray.getColor(R.styleable.ZProgressBar_zbp_progress_text_color, Color.BLACK);
        mProgressTextSize = typedArray.getDimensionPixelSize(R.styleable.ZProgressBar_zbp_progress_text_size, 0);
        autoTextSize = mProgressTextSize <= 0;
        showProgressText = typedArray.getBoolean(R.styleable.ZProgressBar_zbp_show_progress_text, !mIsIntermediateMode);

        typedArray.recycle();

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(40);

        if (mIsIntermediateMode) {
            startIntermediateAnim();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams params = getLayoutParams();

        if (params.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mWidth = mRadius * 2 + getPaddingStart() + getPaddingEnd();
        } else {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
        }

        if (params.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mHeight = mRadius * 2 + getPaddingTop() + getPaddingBottom();
        } else {
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension((int) mWidth, (int) mHeight);

        float maxRadius = Math.min(mWidth - getPaddingStart() - getPaddingEnd(),
                mHeight - getPaddingTop() - getPaddingBottom()) / 2;
        mRadius = Math.min(maxRadius, mRadius);

        mRect.set(mWidth / 2 - mRadius + mBorderWidth / 2, mHeight / 2 - mRadius + mBorderWidth / 2,
                mWidth / 2 + mRadius - mBorderWidth / 2, mHeight / 2 + mRadius - mBorderWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(mBackgroundColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius - mBorderWidth, mPaint);

        float angle = CIRCULAR / mMaxProgress * mProgress;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderWidth);
        canvas.drawArc(mRect, mStartAngle + angle, CIRCULAR - angle, false, mPaint);

        mPaint.setColor(mProgressBarColor);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mProgressBarWidth);
        canvas.drawArc(mRect, mStartAngle, angle, false, mPaint);

        if (showProgressText) {
            if (autoTextSize) {
                mProgressTextSize = Math.min(mRadius - mBorderWidth, mRadius - mProgressBarWidth) / 2;
            }
            mTextPaint.setTextSize(mProgressTextSize);
            mTextPaint.setColor(mProgressTextColor);
            String text = Math.round(mProgress / mMaxProgress * 100) + "%";
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            float baseline = mHeight / 2 + distance;
            canvas.drawText(text, mWidth / 2, baseline, mTextPaint);
        }
    }

    public float getRadius() {
        return mRadius;
    }

    public void setProgressBarRadius(float mRadius) {
        this.mRadius = mRadius;
        requestLayout();
    }

    public void setProgressBarBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
        invalidate();
    }

    public int getProgressBarBackgroundColor() {
        return mBackgroundColor;
    }

    public void setProgress(float progress) {
        if (this.mProgress == progress || progress > mMaxProgress) {
            return;
        }
        this.mProgress = progress;
        invalidate();
    }

    public float getProgress() {
        return mProgress;
    }

    public void setMinProgress(float minProgress) {
        if (this.mMinProgress == minProgress) {
            return;
        }
        this.mMinProgress = minProgress;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
        if (mIsIntermediateMode) {
            startIntermediateAnim();
        } else {
            invalidate();
        }
    }

    public float getMinProgress() {
        return mMinProgress;
    }

    public void setMaxProgress(float maxProgress) {
        if (this.mMaxProgress == maxProgress) {
            return;
        }
        this.mMaxProgress = maxProgress;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
        if (mIsIntermediateMode) {
            startIntermediateAnim();
        } else {
            invalidate();
        }
    }

    public float getMaxProgress() {
        return mMaxProgress;
    }

    public void setProgressBarWidth(float mProgressBarWidth) {
        this.mProgressBarWidth = mProgressBarWidth;
        invalidate();
    }

    public float getProgressBarWidth() {
        return mProgressBarWidth;
    }

    public void setProgressBarColor(int mProgressBarColor) {
        this.mProgressBarColor = mProgressBarColor;
        invalidate();
    }

    public int getProgressBarColor() {
        return mProgressBarColor;
    }

    public void setProgressTextColor(int mProgressTextColor) {
        this.mProgressTextColor = mProgressTextColor;
        invalidate();
    }

    public int getProgressTextColor() {
        return mProgressTextColor;
    }

    public void setProgressTextSize(float mProgressTextSize) {
        this.mProgressTextSize = mProgressTextSize;
        autoTextSize = mProgressTextSize <= 0;
        invalidate();
    }

    public float getProgressTextSize() {
        return mProgressTextSize;
    }

    public void setShowProgressText(boolean showProgressText) {
        if (mIsIntermediateMode) {
            this.showProgressText = showProgressText;
        } else {
            this.showProgressText = false;
        }
        invalidate();
    }

    public boolean isShowProgressText() {
        return showProgressText;
    }

    public void setBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
        invalidate();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderWidth(float mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
        invalidate();
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    public void setIsIntermediateMode(boolean mIsIntermediateMode) {
        if (this.mIsIntermediateMode == mIsIntermediateMode) {
            return;
        }
        this.mIsIntermediateMode = mIsIntermediateMode;
        showProgressText = !mIsIntermediateMode;
        if (mIsIntermediateMode) {
            if (valueAnimator != null) {
                if (!valueAnimator.isRunning()) {
                    startIntermediateAnim();
                }
            } else {
                startIntermediateAnim();
            }
        } else {
            if (valueAnimator != null) {
                valueAnimator.cancel();
                valueAnimator = null;
            }
            mStartAngle = -90;
            invalidate();
        }
    }

    public boolean isIntermediateMode() {
        return mIsIntermediateMode;
    }

    private void startIntermediateAnim() {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(mMinProgress, mMaxProgress - mMinProgress, mMinProgress);
            valueAnimator.setDuration(2000);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                private float temp = mMinProgress;

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value = (float) valueAnimator.getAnimatedValue();
                    setProgress(value);
                    if (valueAnimator.getAnimatedFraction() > 0.5f) {
                        mStartAngle += (4 + (temp - value) / mMaxProgress * CIRCULAR);
                    } else {
                        mStartAngle += 4;
                    }
                    temp = value;
                }

            });
        }
        valueAnimator.setRepeatCount(-1);
        valueAnimator.start();
    }

    public int getColorPrimary(){
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

}
