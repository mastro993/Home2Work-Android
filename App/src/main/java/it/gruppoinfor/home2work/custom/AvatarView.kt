package it.gruppoinfor.home2work.custom

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
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature

import org.jetbrains.annotations.Contract

import butterknife.BindView
import butterknife.ButterKnife
import it.gruppoinfor.home2work.R

class AvatarView : RelativeLayout {

    @BindView(R.id.user_propic)
    internal var userPropic: ImageView? = null
    @BindView(R.id.exp_level)
    internal var expLevel: TextView? = null
    @BindView(R.id.level_frame)
    internal var levelFrame: ImageView? = null
    @BindView(R.id.level_container)
    internal var levelContainer: RelativeLayout? = null

    private var mContext: Context? = null
    private var mLastLevel: Int? = 0

    constructor(context: Context) : super(context) {
        mContext = context
        initUI()
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        mContext = context
        initUI()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        mContext = context
        initUI()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    fun setAvatarURL(avatarURL: String) {
        val requestOptions = RequestOptions()
                .circleCrop()
                .signature(MediaStoreSignature("image/jpeg", System.currentTimeMillis(), 180))
                .placeholder(R.drawable.ic_avatar_placeholder)
                .dontAnimate()
        Glide.with(this)
                .load(avatarURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions).into(userPropic!!)
    }

    fun setLevel(level: Int) {

        val animDuration = 500

        val lvl = Math.min(100, level)

        val animator = ValueAnimator.ofInt(mLastLevel, lvl)
        animator.duration = animDuration.toLong()
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { anim -> expLevel!!.text = anim.animatedValue.toString() }
        animator.start()

        val textShader: Shader

        if (lvl == 100) {
            textShader = LinearGradient(
                    0f, 0f, 0f, 60f,
                    ContextCompat.getColor(mContext!!, R.color.colorAccent),
                    ContextCompat.getColor(mContext!!, R.color.colorPrimary),
                    Shader.TileMode.CLAMP)
        } else {
            val color = getLevelColor(lvl)
            textShader = LinearGradient(
                    0f, 0f, 0f, 60f,
                    ContextCompat.getColor(mContext!!, color),
                    ContextCompat.getColor(mContext!!, color),
                    Shader.TileMode.CLAMP)
        }

        expLevel!!.paint.shader = textShader


        //Drawable shieldIcon = getLevelShield(lvl);
        //shieldIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_shield_8);
        //levelFrame.setImageDrawable(shieldIcon);


        if (mLastLevel < lvl) {

            val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    levelContainer,
                    PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f))
            scaleDown.duration = 250

            scaleDown.repeatCount = 1
            scaleDown.repeatMode = ObjectAnimator.REVERSE

            scaleDown.start()

        }

        mLastLevel = lvl
    }

    /*    private Drawable getLevelShield(int level) {
        if (isBetween(level, 1, 4))
            return ContextCompat.getDrawable(mContext, R.drawable.ic_shield_1);
        else if (isBetween(level, 5, 9))
            return ContextCompat.getDrawable(mContext, R.drawable.ic_shield_2);
        else if (isBetween(level, 10, 19))
            return ContextCompat.getDrawable(mContext, R.drawable.ic_shield_3);
        else if (isBetween(level, 20, 34))
            return ContextCompat.getDrawable(mContext, R.drawable.ic_shield_4);
        else if (isBetween(level, 35, 49))
            return ContextCompat.getDrawable(mContext, R.drawable.ic_shield_5);
        else if (isBetween(level, 50, 69))
            return ContextCompat.getDrawable(mContext, R.drawable.ic_shield_6);
        else
            return ContextCompat.getDrawable(mContext, R.drawable.ic_shield_7);
    }*/

    private fun getLevelColor(level: Int): Int {
        return if (isBetween(level, 1, 4))
            R.color.blue_grey_900
        else if (isBetween(level, 5, 9))
            R.color.brown_600
        else if (isBetween(level, 10, 19))
            R.color.grey_600
        else if (isBetween(level, 20, 34))
            R.color.amber_600
        else if (isBetween(level, 35, 49))
            R.color.green_700
        else if (isBetween(level, 50, 69))
            R.color.blue_700
        else
            R.color.purple_700
    }

    @Contract(pure = true)
    private fun isBetween(x: Int, lower: Int, upper: Int): Boolean {
        return lower <= x && x <= upper
    }

    private fun initUI() {
        val view = View.inflate(context, R.layout.custom_avatar_view, this)
        ButterKnife.bind(this, view)
    }


}
