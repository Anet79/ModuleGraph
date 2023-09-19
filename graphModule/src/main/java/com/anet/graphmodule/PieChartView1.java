package com.anet.graphmodule;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PieChartView1 extends View {



    // Paint Object for background
    private Paint mBgPaint;

    private ArrayList<Paint> dataPaintList;
    private ArrayList<RectF> dataRect;

    private Path boundaryPath;

    private float cornerRadius = 16;
    // width of the view
    private int width;

    // height of the view
    private int height;

    // Context

    private float totalValue;
    private float mScaleFactor;
    private boolean firstAnim = true;

    private List<Float> dataValues;
    private List<Integer> sliceColors;

    private float animatedValue;
    private ValueAnimator animator;

    private boolean shouldStartAnim = false;
    private float length;
    private List<DataModel> dataList;

    @NonNull
    private WeakReference<Context> contextWeakReference;
    @Nullable
    private AttributeSet attributeSet;
    private int defStyleAttr = 0;

    @ColorInt
    private int borderColor;
    private int strokeWidth = 2;
    private int strokeAnimDuration;




    public PieChartView1(Context context) {
        super(context);
        this.contextWeakReference = new WeakReference<>(context);
        init();
    }



    public PieChartView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


        this.contextWeakReference = new WeakReference<>(context);
        attributeSet = attrs;
        init();

    }

    public PieChartView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.contextWeakReference = new WeakReference<>(context);
        this.attributeSet = attrs;
        this.defStyleAttr = defStyleAttr;
        init();
    }

    private void getAttrsFromTypedArray(AttributeSet attributeSet) {
        final TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.PieChartView, 0, 0);


        borderColor = typedArray.getColor(R.styleable.PieChartView_border_color_1, getResources().getColor(R.color.graph_border));
        strokeAnimDuration = typedArray.getInt(R.styleable.PieChartView_border_anim_duration_1, 1000);

        typedArray.recycle();
    }




    private void init() {
        getAttrsFromTypedArray(attributeSet);


        dataValues = new ArrayList<>();
        sliceColors = new ArrayList<>();
        animatedValue = 0;
    }

    public void setData(List<Float> values, List<Integer> colors) {
        if (values.size() != colors.size()) {
            throw new IllegalArgumentException("Data values and colors must have the same size");
        }

        dataValues = values;
        sliceColors = colors;

        // Animate the chart when updating the data
        animateChart();
    }

    private void animateChart() {

        mBgPaint = new Paint();
        mBgPaint.setStrokeWidth(2);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(getResources().getColor(R.color.graph_border));


        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(2000); // Animation duration in milliseconds

        animator.addUpdateListener(animation -> {
            animatedValue = (float) animation.getAnimatedValue();
            invalidate(); // Redraw the view during animation
        });

        animator.start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        if (dataValues.isEmpty()) {
            return; // No data to draw
        }

        float total = 0;
        for (float value : dataValues) {
            total += value;
        }

        RectF bounds = new RectF(0, 0, getWidth(), getHeight());


        float startAngle = 0;



        for (int i = 0; i < dataValues.size(); i++) {
            float sweepAngle = 360 * (dataValues.get(i) / total) * animatedValue;
            boolean useCenter = true;

            Paint paint = new Paint();
            Paint paint1 = new Paint();




            //paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint1.setStyle(Paint.Style.STROKE);
            RectF bounds01 = new RectF(0, 0, getWidth(), getHeight());
            paint.setColor(sliceColors.get(i % sliceColors.size()));
            paint1.setColor(borderColor);
            canvas.drawArc(bounds, startAngle, sweepAngle, useCenter, paint);
            canvas.drawArc(bounds, startAngle, sweepAngle, useCenter, paint1);
            startAngle += sweepAngle;
        }
    }







}
