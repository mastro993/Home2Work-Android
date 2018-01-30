package it.gruppoinfor.home2work.custom;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
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
import com.github.lzyzsd.circleprogress.DonutProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.Tools;

public class AvatarView extends RelativeLayout {

    @BindView(R.id.karma_donut_progress)
    DonutProgress karmaDonutProgress;
    @BindView(R.id.user_propic)
    ImageView userPropic;
    @BindView(R.id.exp_level)
    TextView expLevel;
    @BindView(R.id.level_frame)
    ImageView levelFrame;
    @BindView(R.id.level_container)
    RelativeLayout levelContainer;

    private Context mContext;
    private Long mLastExp = 0L;
    private Integer mLastLevel = 0;
    private Float mLastProgress = 0.0f;
    private int mAvatarVersion = 0;

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

    public void setExp(Long exp) {

        final int animDuration = 500;

        int level = ((Double) (1 + 0.10 * Math.sqrt(exp))).intValue();

        if (level > 100) {

            DonutProgressAnimation animation = new DonutProgressAnimation(karmaDonutProgress, mLastProgress, 100);
            animation.setDuration(animDuration);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            karmaDonutProgress.startAnimation(animation);

        } else {

            int thisLevelExp = (int) Math.pow(10.0 * (level - 1.0), 2.0);
            int nextLevelExp = (int) Math.pow(10 * level, 2.0);
            int toNextLevelExp = nextLevelExp - thisLevelExp;
            long expDelta = exp - thisLevelExp;
            float progress = (100.0f / toNextLevelExp) * expDelta;

            DonutProgressAnimation animation = new DonutProgressAnimation(karmaDonutProgress, mLastProgress, progress);
            animation.setDuration(animDuration);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            karmaDonutProgress.startAnimation(animation);

            mLastProgress = progress;
        }

        level = Math.min(100, level);

        ValueAnimator animator = ValueAnimator.ofInt(mLastLevel, level);
        animator.setDuration(animDuration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(anim ->
                expLevel.setText(anim.getAnimatedValue().toString())
        );
        animator.start();

        Drawable shieldIcon = getLevelShield(level);

        if (level > 99) {
            Shader textShader = new LinearGradient(
                    0, 0, 0, 60,
                    ContextCompat.getColor(mContext, R.color.colorAccent),
                    ContextCompat.getColor(mContext, R.color.colorPrimary),
                    Shader.TileMode.CLAMP);
            expLevel.getPaint().setShader(textShader);
            shieldIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_shield_8);
        }

        levelFrame.setImageDrawable(shieldIcon);


        if (mLastExp != null && mLastLevel < level) {

            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    levelContainer,
                    PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f));
            scaleDown.setDuration(250);

            scaleDown.setRepeatCount(1);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

            scaleDown.start();

        }

        mLastExp = exp;
        mLastLevel = level;
    }

    private Drawable getLevelShield(int level) {
        if (Tools.isBetween(level, 1, 4))
            return ContextCompat.getDrawable(mContext, R.drawable.ic_shield_1);
        else if (Tools.isBetween(level, 5, 9))
            return ContextCompat.getDrawable(mContext, R.drawable.ic_shield_2);
        else if (Tools.isBetween(level, 10, 19))
            return ContextCompat.getDrawable(mContext, R.drawable.ic_shield_3);
        else if (Tools.isBetween(level, 20, 34))
            return ContextCompat.getDrawable(mContext, R.drawable.ic_shield_4);
        else if (Tools.isBetween(level, 35, 49))
            return ContextCompat.getDrawable(mContext, R.drawable.ic_shield_5);
        else if (Tools.isBetween(level, 50, 69))
            return ContextCompat.getDrawable(mContext, R.drawable.ic_shield_6);
        else
            return ContextCompat.getDrawable(mContext, R.drawable.ic_shield_7);

    }

    private void initUI() {
        View view = inflate(getContext(), R.layout.custom_avatar_view, this);
        ButterKnife.bind(this, view);
    }


}
