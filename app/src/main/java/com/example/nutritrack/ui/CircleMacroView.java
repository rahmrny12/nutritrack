package com.example.nutritrack.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nutritrack.R;

public class CircleMacroView extends FrameLayout {

    private ImageView imgCircle;
    private TextView tvPercent;

    private int max = 100;

    public CircleMacroView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.macro_progress_circle_base, this);
        imgCircle = findViewById(R.id.imgCircle);
        tvPercent = findViewById(R.id.tvPercent);
    }

    public void setProgress(int value) {
        int percent = (int)((value * 100f) / max);
        tvPercent.setText(percent + "%");

        // TODO: dynamic image update (draw arc overlay if needed)
    }

    public void setMaxProgress(int maxValue) {
        this.max = maxValue;
    }

    public void setLabelColor(int color) {
        tvPercent.setTextColor(color);
    }
}
