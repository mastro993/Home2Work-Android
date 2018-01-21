package it.gruppoinfor.home2work.custom;


import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.github.lzyzsd.circleprogress.DonutProgress;

public class DonutProgressAnimation extends Animation {
    private DonutProgress donutProgress;
    private float from;
    private float to;

    DonutProgressAnimation(DonutProgress donutProgress, float from, float to) {
        super();
        this.donutProgress = donutProgress;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        donutProgress.setProgress((int) value);
    }



}