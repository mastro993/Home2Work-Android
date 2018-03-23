package it.gruppoinfor.home2work.views

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.ImageLoader
import it.gruppoinfor.home2work.common.PicassoCircleTransform
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.extensions.hide
import it.gruppoinfor.home2work.extensions.show
import kotlinx.android.synthetic.main.view_avatar.view.*
import javax.inject.Inject

class AvatarView : RelativeLayout {

    @Inject
    lateinit var imageLoader: ImageLoader

    private var mLastLevel: Int = 0
    private val animationTime: Long = 500
    private var levelColor: Int = 0

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_avatar, this)
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        View.inflate(context, R.layout.view_avatar, this)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        View.inflate(context, R.layout.view_avatar, this)
    }

    fun setAvatarURL(avatarURL: String?) {

        DipendencyInjector.mainComponent.inject(this)

        avatarURL?.let {
            imageLoader.load(
                    url = avatarURL,
                    imageView = user_propic,
                    transformation = PicassoCircleTransform(),
                    placeholder = R.drawable.ic_avatar_placeholder
            )
        }

    }

    fun setLevel(level: Int?) {


        if (level == null) {

            level_frame.hide()
            exp_level.hide()


        } else {

            level_frame.show()
            exp_level.show()

            val lvl = Math.min(100, level)

            val animator = ValueAnimator.ofInt(mLastLevel, lvl)
            animator.duration = animationTime
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.addUpdateListener { anim -> exp_level.text = anim.animatedValue.toString() }
            animator.start()

            levelColor = getLevelColor(lvl)
            val textShader = if (lvl == 100) {
                LinearGradient(
                        0f, 0f, 0f, 60f,
                        ContextCompat.getColor(context, R.color.colorAccent),
                        ContextCompat.getColor(context, R.color.colorPrimary),
                        Shader.TileMode.CLAMP)
            } else {
                LinearGradient(
                        0f, 0f, 0f, 60f,
                        ContextCompat.getColor(context, levelColor),
                        ContextCompat.getColor(context, levelColor),
                        Shader.TileMode.CLAMP)
            }

            exp_level.paint.shader = textShader

            if (mLastLevel < lvl) {
                val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                        level_frame,
                        PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                        PropertyValuesHolder.ofFloat("scaleY", 1.2f))
                scaleDown.duration = 250
                scaleDown.repeatCount = 1
                scaleDown.repeatMode = ObjectAnimator.REVERSE
                scaleDown.start()
            }

            mLastLevel = lvl

        }

    }

    private fun getLevelColor(level: Int): Int {

        return when (level) {
            in 1..4 -> R.color.blue_grey_900
            in 5..9 -> R.color.brown_600
            in 10..19 -> R.color.grey_600
            in 20..34 -> R.color.amber_600
            in 35..49 -> R.color.green_700
            in 50..69 -> R.color.blue_700
            else -> R.color.purple_700
        }
    }


}
