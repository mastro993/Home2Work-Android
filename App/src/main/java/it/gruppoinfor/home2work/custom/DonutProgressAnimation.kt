package it.gruppoinfor.home2work.custom


import android.view.animation.Animation
import android.view.animation.Transformation

import com.github.lzyzsd.circleprogress.DonutProgress

class DonutProgressAnimation internal constructor(private val donutProgress: DonutProgress, private val from: Float, private val to: Float) : Animation() {

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)
        val value = from + (to - from) * interpolatedTime
        donutProgress.progress = value.toInt().toFloat()
    }


}