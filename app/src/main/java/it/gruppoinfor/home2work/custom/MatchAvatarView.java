package it.gruppoinfor.home2work.custom;

import android.animation.ValueAnimator;
import android.content.Context;
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
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import it.gruppoinfor.home2work.R;

public class MatchAvatarView extends RelativeLayout {


    @BindView(R.id.score_progress)
    ArcProgress scoreProgress;
    @BindView(R.id.user_avatar)
    ImageView userAvatar;
    @BindView(R.id.score_text)
    TextView scoreText;
    private Context context;

    private Integer exp;
    private Integer level;
    private Float progress;

    private Drawable shieldIcon;

    public MatchAvatarView(Context context) {
        super(context);
        this.context = context;
        initUI();
    }

    public MatchAvatarView(Context context, AttributeSet attributes) {
        super(context, attributes);
        this.context = context;
        initUI();
    }

    public MatchAvatarView(Context context, AttributeSet attributeSet, int defStyleAttr) {
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
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder);
        Glide.with(this)
                .load(avatarURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(userAvatar);
    }

    public void setScore(Integer score) {

        ArcProgressAnimation animation = new ArcProgressAnimation(scoreProgress, 0, score);
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        scoreProgress.startAnimation(animation);

        ValueAnimator animator = ValueAnimator.ofInt(0, score);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation1 ->
                scoreText.setText(String.format(Locale.ITALY, "%1$s%%", animation1.getAnimatedValue().toString()))
        );
        animator.start();

        int color = getScoreColor(score);
        Drawable bg = ContextCompat.getDrawable(context, R.drawable.bg_match_score_percent);

        scoreProgress.setFinishedStrokeColor(color);
        bg.setTint(color);
        scoreText.setBackground(bg);
    }

    public int getScoreColor(int score) {
        if (score < 50) {
            return ContextCompat.getColor(context, R.color.red_500);
        } else if (score < 60) {
            return ContextCompat.getColor(context, R.color.orange_600);
        } else if (score < 70) {
            return ContextCompat.getColor(context, R.color.amber_400);
        } else if (score < 80) {
            return ContextCompat.getColor(context, R.color.lime_500);
        } else if (score < 90) {
            return ContextCompat.getColor(context, R.color.light_green_500);
        } else {
            return ContextCompat.getColor(context, R.color.green_500);
        }
    }

    private void initUI() {
        View view = inflate(getContext(), R.layout.custom_match_avatar_view, this);
        ButterKnife.bind(this, view);
    }

    public Integer getLevel() {
        return level;
    }

    public Drawable getShieldIcon() {
        return shieldIcon;
    }
}
