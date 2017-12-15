package it.gruppoinfor.home2work.custom;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
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
    private Context context;

    private Integer exp;
    private Integer level;
    private Float progress;
    private Drawable shieldIcon;

    public AvatarView(Context context) {
        super(context);
        this.context = context;
        initUI();
    }

    public AvatarView(Context context, AttributeSet attributes) {
        super(context, attributes);
        this.context = context;
        initUI();
    }

    public AvatarView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.context = context;
        initUI();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setAvatarURL(String avatarURL) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder)
                .dontAnimate();
        Glide.with(this)
                .load(avatarURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions).into(userPropic);
    }

    public void setExp(Integer exp, Integer level, Float progress) {

        if (level < 101) {
            DonutProgressAnimation animation = new DonutProgressAnimation(
                    karmaDonutProgress,
                    this.progress == null ? 0 : this.progress,
                    progress);
            animation.setDuration(this.exp == null ? 500 : 200);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            karmaDonutProgress.startAnimation(animation);
        } else {
            DonutProgressAnimation animation = new DonutProgressAnimation(
                    karmaDonutProgress,
                    this.progress == null ? 0 : 100,
                    100);
            animation.setDuration(this.exp == null ? 500 : 200);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            karmaDonutProgress.startAnimation(animation);
        }

        level = Math.min(100, level);

        ValueAnimator animator = ValueAnimator.ofInt(
                this.level == null ? 0 : this.level,
                level);

        animator.setDuration(this.level == null ? 500 : 200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(anim ->
                expLevel.setText(anim.getAnimatedValue().toString())
        );
        animator.start();

        //int levelColor = getLevelColor(level);
        shieldIcon = getLevelShield(level);

        if (level > 99) {
            Shader textShader = new LinearGradient(
                    0, 0, 0, 60,
                    ContextCompat.getColor(context, R.color.colorAccent),
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    Shader.TileMode.CLAMP);
            expLevel.getPaint().setShader(textShader);
            shieldIcon = ContextCompat.getDrawable(context, R.drawable.ic_shield_8);
            //levelContainer.setBackground(shieldIcon);
        }

        levelFrame.setImageDrawable(shieldIcon);


        if (this.exp != null && this.level < level) {

            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    levelContainer,
                    PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f));
            scaleDown.setDuration(250);

            scaleDown.setRepeatCount(1);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

            scaleDown.start();

        }

        this.exp = exp;
        this.level = level;
        this.progress = progress;
    }

    private int getLevelColor(int level) {
        if (Tools.isBetween(level, 1, 4))
            return ContextCompat.getColor(context, R.color.level_1_4_color);
        else if (Tools.isBetween(level, 5, 9))
            return ContextCompat.getColor(context, R.color.level_5_9_color);
        else if (Tools.isBetween(level, 10, 19))
            return ContextCompat.getColor(context, R.color.level_10_19_color);
        else if (Tools.isBetween(level, 20, 34))
            return ContextCompat.getColor(context, R.color.level_20_34_color);
        else if (Tools.isBetween(level, 35, 49))
            return ContextCompat.getColor(context, R.color.level_35_49_color);
        else if (Tools.isBetween(level, 50, 69))
            return ContextCompat.getColor(context, R.color.level_50_69_color);
        else
            return ContextCompat.getColor(context, R.color.level_70_99_color);
    }

    private Drawable getLevelShield(int level) {
        if (Tools.isBetween(level, 1, 4))
            return ContextCompat.getDrawable(context, R.drawable.ic_shield_1);
        else if (Tools.isBetween(level, 5, 9))
            return ContextCompat.getDrawable(context, R.drawable.ic_shield_2);
        else if (Tools.isBetween(level, 10, 19))
            return ContextCompat.getDrawable(context, R.drawable.ic_shield_3);
        else if (Tools.isBetween(level, 20, 34))
            return ContextCompat.getDrawable(context, R.drawable.ic_shield_4);
        else if (Tools.isBetween(level, 35, 49))
            return ContextCompat.getDrawable(context, R.drawable.ic_shield_5);
        else if (Tools.isBetween(level, 50, 69))
            return ContextCompat.getDrawable(context, R.drawable.ic_shield_6);
        else
            return ContextCompat.getDrawable(context, R.drawable.ic_shield_7);

    }

    private void initUI() {
        View view = inflate(getContext(), R.layout.custom_avatar_view, this);
        ButterKnife.bind(this, view);
    }

    public Integer getLevel() {
        return level;
    }

    public Drawable getShieldIcon() {
        return shieldIcon;
    }
}
