package com.example.nutritrack.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CircleShape extends View {

    private Paint bgPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private Paint labelPaint;
    private RectF rectF = new RectF();

    private float progress = 0f; // 0–100
    private float animatedProgress = 0f;
    private int progressColor = 0xFF4CAF50;
    private String label = "";

    public CircleShape(Context context) {
        super(context);
        init();
    }

    public CircleShape(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleShape(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(14f);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(progressColor);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(14f);
        bgPaint.setColor(0xFFE0E0E0);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(36f);
        textPaint.setColor(0xFF000000);

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTextSize(22f);
        labelPaint.setColor(0xFF666666);
    }

    public void setProgress(float targetProgress) {

        ValueAnimator animator = ValueAnimator.ofFloat(animatedProgress, targetProgress);
        animator.setDuration(800);
        animator.addUpdateListener(animation -> {
            animatedProgress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
        progress = targetProgress;
    }

    public void setProgressColor(int color) {
        this.progressColor = color;
        progressPaint.setColor(color);
        invalidate();
    }

    public void setLabel(String label) {
        this.label = label;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float radius = Math.min(width, height) / 2f - 20f;

        float cx = width / 2f;
        float cy = height / 2f - 10f; // agak naik biar label muat

        rectF.set(cx - radius, cy - radius, cx + radius, cy + radius);

        // Gambar background abu
        canvas.drawArc(rectF, 0, 360, false, bgPaint);

        // Gambar progress berwarna
        canvas.drawArc(rectF, -90, (animatedProgress / 100f) * 360f, false, progressPaint);

        // Teks persentase tengah
        canvas.drawText((int) animatedProgress + "%", cx, cy + (textPaint.getTextSize() / 3), textPaint);

        // Label bawah
        canvas.drawText(label, cx, cy + radius + 40f, labelPaint);
    }

    public void setPercentage(int percent) {
        setProgress(percent);   // reuse existing method
    }
}
