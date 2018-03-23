package it.gruppoinfor.home2work.common


import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar

class ProgressBarAnimation(private val progressBar: ProgressBar, private var from: Float?, private var to: Float) : Animation() {

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)

        from?.let { from ->
            val value = from + (to - from) * interpolatedTime
            progressBar.progress = value.toInt()
            return
        }

        val value = to * interpolatedTime
        progressBar.progress = value.toInt()

    }

}