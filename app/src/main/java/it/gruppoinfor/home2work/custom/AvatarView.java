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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.lzyzsd.circleprogress.DonutProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2workapi.model.Karma;

public class AvatarView extends RelativeLayout {


    @BindView(R.id.karma_donut_progress)
    DonutProgress karmaDonutProgress;
    @BindView(R.id.user_propic)
    CircleImageView userPropic;
    @BindView(R.id.karma_level)
    TextView karmaLevel;
    @BindView(R.id.level_container)
    RelativeLayout levelContainer;
    private String avatarURL;
    private Karma karma;
    private int levelColor;
    private Drawable levelShape;
    private Context context;


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

    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar_placeholder).dontAnimate();
        Glide.with(this).load(avatarURL).apply(requestOptions).into(userPropic);

    }

    public void setKarma(Karma karma) {

        DonutProgressAnimation animation = new DonutProgressAnimation(
                karmaDonutProgress,
                this.karma == null ? 0 : this.karma.getLevelProgress(),
                karma.getLevelProgress());

        animation.setDuration(this.karma == null ? 500 : 200);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());

        karmaDonutProgress.startAnimation(animation);

        ValueAnimator animator = ValueAnimator.ofInt(this.karma == null ? 0 : this.karma.getLevel(), karma.getLevel());

        animator.setDuration(this.karma == null ? 500 : 200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(anim ->
                karmaLevel.setText(anim.getAnimatedValue().toString())
        );

        animator.start();

        if(karma.getLevel() <= 99)
            karmaLevel.setTextColor(getLevelColor(karma.getLevel()));
        else {
            Shader textShader=new LinearGradient(
                    0, 0, 0, 60,
                    ContextCompat.getColor(context, R.color.colorAccent),
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    Shader.TileMode.CLAMP);
            karmaLevel.getPaint().setShader(textShader);
        }


        if (this.karma != null && this.karma.getLevel() < karma.getLevel()) {

            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    levelContainer,
                    PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f));
            scaleDown.setDuration(250);

            scaleDown.setRepeatCount(1);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

            scaleDown.start();

        }

        this.karma = karma;

    }

    private int getLevelColor(int level) {

        if (isBetween(level, 1, 9))
            return ContextCompat.getColor(context, R.color.level_1_9_color);
        else if (isBetween(level, 10, 19))
            return ContextCompat.getColor(context, R.color.level_10_19_color);
        else if (isBetween(level, 20, 29))
            return ContextCompat.getColor(context, R.color.level_20_29_color);
        else if (isBetween(level, 30, 49))
            return ContextCompat.getColor(context, R.color.level_30_49_color);
        else if (isBetween(level, 50, 69))
            return ContextCompat.getColor(context, R.color.level_50_69_color);
        else
            return ContextCompat.getColor(context, R.color.level_70_99_color);

    }

    private void initUI() {
        View view = inflate(getContext(), R.layout.custom_avatar_view, this);
        ButterKnife.bind(this, view);
    }


}
