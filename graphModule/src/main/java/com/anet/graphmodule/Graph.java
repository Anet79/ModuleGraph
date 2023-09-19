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

public class Graph extends View {

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

    public Graph(Context context) {
        super(context);
        init();
    }

    public Graph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Graph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        float maxValue = 0;
        for (ItemType itemType : itemTypeList) {
            maxValue = Math.max(maxValue, itemType.value);
        }

        // Draw Y-axis and grid lines
        float yAxisX = startX - 20; // Adjust the position of the Y-axis as needed
        float yAxisTop = chartRectF.top;
        float yAxisBottom = chartRectF.bottom;
        mPaint.setColor(Color.GRAY); // Set the color for grid lines

        for (int i = 0; i <= 5; i++) {
            float gridY = yAxisTop + (i * maxBarHeight / 5f);
            canvas.drawLine(yAxisX, gridY, chartRectF.right, gridY, mPaint);

            String gridLabel = String.valueOf((int) (maxValue * (1f - i / 5f))); // Scale the value for grid labels
            canvas.drawText(gridLabel, yAxisX - 10, gridY, mPaint);
        }

        // Draw the max value on the grid
        float maxGridY = chartRectF.top; // The max value corresponds to the top of the chartRectF
        mPaint.setColor(Color.GRAY); // Set the color for the max value grid line
        canvas.drawLine(yAxisX, maxGridY, chartRectF.right, maxGridY, mPaint);
        String maxGridLabel = String.valueOf((int) maxValue);
        canvas.drawText(maxGridLabel, yAxisX - 10, maxGridY, mPaint);

        // Draw grid points and lines
        mPaint.setColor(Color.RED); // Set the color for grid points and lines
        for (int i = 0; i < itemCount; i++) {
            ItemType itemType = itemTypeList.get(i);

            float barLeft = startX + (i * (barWidth + barSpacing));
            float barTop = chartRectF.bottom - (itemType.animatedValue / maxValue) * maxBarHeight;
            float barRight = barLeft + barWidth;
            float barBottom = chartRectF.bottom;

            float pointX = barLeft + (barWidth / 2);
            float pointY = barTop-getPaddingBottom()-getPaddingTop();

            // Draw grid points
            canvas.drawCircle(pointX, pointY, 8, mPaint);

            // Draw lines connecting the grid points
            if (i > 0) {
                ItemType prevItemType = itemTypeList.get(i-1);
                float prevBarLeft = startX + ((i - 1) * (barWidth + barSpacing));
                float prevPointX = prevBarLeft + (barWidth / 2);
                float prevPointY = chartRectF.bottom - (prevItemType.animatedValue / maxValue) * maxBarHeight;
                canvas.drawLine(prevPointX, prevPointY, pointX, pointY-10, mPaint);
            }

            // Place text on the grid points
            String labelTextItem = itemType.label;
            // Place text on the grid points
            String labelTextValue= " " + (int) itemType.animatedValue ;
            mPaint.setColor(itemType.color);
            mPaint.setTextSize(30);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(labelTextItem+"\n"+labelTextValue, pointX, pointY+maxValue , mPaint);
            //canvas.drawText(labelTextValue, pointX, pointY+maxValue , mPaint);
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
