package it.gruppoinfor.home2work.user

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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature
import it.gruppoinfor.home2work.R
import kotlinx.android.synthetic.main.custom_avatar_view.view.*

class AvatarView : RelativeLayout {

    enum class Size constructor(val value: Int) {
        SMALL(0),
        NORMAL(1);

        companion object {
            fun fromInt(x: Int): Size {

                return when (x) {
                    0 -> SMALL
                    else -> NORMAL
                }
            }
        }
    }

    private var mLastLevel: Int = 0
    private val animationTime: Long = 500
    private var levelColor: Int = 0
    var size: Size = Size.NORMAL

    constructor(context: Context) : super(context) {

        View.inflate(context, R.layout.custom_avatar_view, this)

    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {

        val ta = context.obtainStyledAttributes(attributes, R.styleable.avatar_view_attrs, 0, 0)
        try {
            val ssize = ta.getInt(R.styleable.avatar_view_attrs_size, Size.NORMAL.value)
            size = Size.fromInt(ssize)
        } finally {
            ta.recycle()
        }

        when (size) {
            Size.SMALL -> View.inflate(context, R.layout.custom_avatar_view_small, this)
            Size.NORMAL -> View.inflate(context, R.layout.custom_avatar_view, this)
        }

    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {

        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.avatar_view_attrs, 0, 0)
        try {
            val ssize = ta.getInt(R.styleable.avatar_view_attrs_size, Size.NORMAL.value)
            size = Size.fromInt(ssize)
        } finally {
            ta.recycle()
        }

        when (size) {
            Size.SMALL -> View.inflate(context, R.layout.custom_avatar_view_small, this)
            Size.NORMAL -> View.inflate(context, R.layout.custom_avatar_view, this)
        }

    }

    fun setAvatarURL(avatarURL: String?) {

        val requestOptions = RequestOptions()
                .circleCrop()
                .signature(MediaStoreSignature("image/jpeg", System.currentTimeMillis(), 180))
                .placeholder(R.drawable.ic_avatar_placeholder)
                .dontAnimate()

        Glide.with(this)
                .load(avatarURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions).into(user_propic)

    }

    fun setLevel(level: Int?) {

        if (level == null) {

            level_container.visibility = View.GONE

        } else {

            level_container.visibility = View.VISIBLE

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
                        level_container,
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
