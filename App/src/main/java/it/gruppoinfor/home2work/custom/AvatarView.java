package it.gruppoinfor.home2work.custom;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.MediaStoreSignature;

import org.jetbrains.annotations.Contract;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;

public class AvatarView extends RelativeLayout {

    @BindView(R.id.user_propic)
    ImageView userPropic;
    @BindView(R.id.exp_level)
    TextView expLevel;
    @BindView(R.id.level_frame)
    ImageView levelFrame;
    @BindView(R.id.level_container)
    RelativeLayout levelContainer;

    private Context mContext;
    private Integer mLastLevel = 0;

    public AvatarView(Context context) {
        super(context);
        mContext = context;
        initUI();
    }

    public AvatarView(Context context, AttributeSet attributes) {
        super(context, attributes);
        mContext = context;
        initUI();
    }

    public AvatarView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        mContext = context;
        initUI();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setAvatarURL(String avatarURL) {
        RequestOptions requestOptions = new RequestOptions()
                .circleCrop()
                .signature(new MediaStoreSignature("image/jpeg", System.currentTimeMillis(), 180))
                .placeholder(R.drawable.ic_avatar_placeholder)
                .dontAnimate();
        Glide.with(this)
                .load(avatarURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions).into(userPropic);
    }

    public void setLevel(int level) {

        final int animDuration = 500;

        int lvl = Math.min(100, level);

        ValueAnimator animator = ValueAnimator.ofInt(mLastLevel, lvl);
        animator.setDuration(animDuration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(anim ->
                expLevel.setText(anim.getAnimatedValue().toString())
        );
        animator.start();

        Shader textShader;

        if (lvl == 100) {
            textShader = new LinearGradient(
                    0, 0, 0, 60,
                    ContextCompat.getColor(mContext, R.color.colorAccent),
                    ContextCompat.getColor(mContext, R.color.colorPrimary),
                    Shader.TileMode.CLAMP);
        } else {
            int color = getLevelColor(lvl);
            textShader = new LinearGradient(
                    0, 0, 0, 60,
                    ContextCompat.getColor(mContext, color),
                    ContextCompat.getColor(mContext, color),
                    Shader.TileMode.CLAMP);
        }

        expLevel.getPaint().setShader(textShader);


        //Drawable shieldIcon = getLevelShield(lvl);
        //shieldIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_shield_8);
        //levelFrame.setImageDrawable(shieldIcon);


        if (mLastLevel < lvl) {

            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    levelContainer,
                    PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f));
            scaleDown.setDuration(250);

            scaleDown.setRepeatCount(1);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

            scaleDown.start();

        }

        mLastLevel = lvl;
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

    private int getLevelColor(int level) {
        if (isBetween(level, 1, 4))
            return R.color.blue_grey_900;
        else if (isBetween(level, 5, 9))
            return R.color.brown_600;
        else if (isBetween(level, 10, 19))
            return R.color.grey_600;
        else if (isBetween(level, 20, 34))
            return R.color.amber_600;
        else if (isBetween(level, 35, 49))
            return R.color.green_700;
        else if (isBetween(level, 50, 69))
            return R.color.blue_700;
        else
            return R.color.purple_700;
    }

    @Contract(pure = true)
    private boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    private void initUI() {
        View view = inflate(getContext(), R.layout.custom_avatar_view, this);
        ButterKnife.bind(this, view);
    }


}
