package com.anet.graphmodule;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LinearChartView extends View {

    private Paint mPaint;
    private Paint mGuidelinePaint;
    private Path mPath, drawLinePath = new Path();
    private PathMeasure mPathMeasure;
    private int width, height;
    private RectF chartRectF;
    private Canvas mCanvas;
    private List<ItemType> itemTypeList;
    private RectF pieRectF, tempRectF;
    private int barWidth = 80;
    private int barSpacing = 10;
    private int maxBarHeight;
    private int radius;


    private List<Point> itemPoints;

    private int cell = 0;

    private float innerRadius = 0.0f;

    private float offRadius = 0, offLine;

    private int textAlpha;
    private Point firstPoint;
    private int backGroundColor = 0xffffffff;

    private int itemTextSize = 30, textPadding = 8;

    private int defaultStartAngle = -90;
    private float pieCell;
    private ValueAnimator animator;

    private long animDuration = 2000;

    public LinearChartView(Context context) {
        super(context);
        init();
    }

    public LinearChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinearChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();
        mGuidelinePaint = new Paint();
        mGuidelinePaint.setStyle(Paint.Style.STROKE);
        mGuidelinePaint.setColor(Color.BLACK);
        mGuidelinePaint.setStrokeWidth(1);
        chartRectF = new RectF();
        itemTypeList = new ArrayList<>();
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    public void startAnimation() {
        stopAnimation();

        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(animDuration);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                updateAnimatedValues(animatedValue);
                invalidate();
            }
        });
        animator.start();
    }

    public void stopAnimation() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }

    private void updateAnimatedValues(float progress) {
        for (ItemType itemType : itemTypeList) {
            itemType.animatedValue = itemType.value * progress;
        }
    }



    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }

    public void addItemType(ItemType itemType) {
        if (itemTypeList != null) {
            itemTypeList.add(itemType);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
        maxBarHeight = height - getPaddingTop() - getPaddingBottom();
        chartRectF.set(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            drawChart(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void resetPaint() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(256);
    }

    private void drawChart(Canvas canvas) {
        if (itemTypeList.isEmpty()) {
            return;
        }

        int itemCount = itemTypeList.size();
        int totalBarWidth = (itemCount * barWidth) + ((itemCount - 1) * barSpacing);
        float startX = (width - totalBarWidth) / 2f;
        float startY= (height-maxBarHeight);

        float maxValue = 0;
        for (ItemType itemType : itemTypeList) {

            maxValue = Math.max(maxValue, itemType.value);
        }
        // Draw Y-axis and grid lines
        float yAxisX = startX - 20; // Adjust the position of the Y-axis as needed
        float yAxisTop = chartRectF.top;
        float yAxisBottom = chartRectF.bottom;
//        for (int i = 0; i <= 5; i++) {
//            float gridY = yAxisTop + (i * maxBarHeight / 5f);
//            canvas.drawLine(yAxisX, gridY, chartRectF.right, gridY, mPaint);
//            String gridLabel = String.valueOf((int) (maxValue * (1f - i / 5f))); // Scale the value for grid labels
//            canvas.drawText(gridLabel, yAxisX - 10, gridY, mPaint);
//        }

        for (int i = 0; i < itemCount; i++) {
            ItemType itemType = itemTypeList.get(i);

            float barLeft = startX + (i * (barWidth + barSpacing));
            float barTop = ((chartRectF.bottom) - (itemType.animatedValue / (maxValue+20)) *( maxBarHeight-maxValue));
            float barRight = barLeft + barWidth;
            float barBottom = chartRectF.bottom;

            mPaint.setColor(itemType.color);
            canvas.drawRect(barLeft, barTop, barRight, barBottom+20, mPaint);

            String labelTextItem = itemType.label;
            String labelTextValue= " " + (int) itemType.animatedValue ;
            float gridY = yAxisTop + (i * maxBarHeight / 5f);

            String gridLabel = String.valueOf((int) (maxValue * (1f - i / 5f))); // Scale the value for grid labels
           // canvas.drawText(gridLabel, yAxisX , gridY-20, mPaint);
            float labelX = barLeft + (barWidth / 2);
            float labelY = barTop - 10; // Adjust the label position as needed
            float labelForItem= barTop-2*itemType.value;
            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(30);
            mPaint.setTextAlign(Paint.Align.CENTER);
            //canvas.drawLine(yAxisX, gridY, chartRectF.right, gridY, mPaint);
            canvas.drawText(labelTextItem, labelX, labelForItem, mPaint);

            canvas.drawText(labelTextValue, labelX, labelY, mPaint);
        }
    }

    public static class ItemType {
        String label;
        int value;
        int color;
        float animatedValue;

        public ItemType(String label, int value, int color) {
            this.label = label;
            this.value = value;
            this.color = color;
            this.animatedValue = value;
        }
    }
}
