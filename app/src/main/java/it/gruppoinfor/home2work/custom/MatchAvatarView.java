package it.gruppoinfor.home2work.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;

public class MatchAvatarView extends RelativeLayout {


    @BindView(R.id.score_progress)
    DonutProgress scoreProgress;
    @BindView(R.id.user_avatar)
    ImageView userAvatar;
    private Context context;

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

    }

    public void setScore(Integer score) {
        DonutProgressAnimation animation = new DonutProgressAnimation(scoreProgress, 0, score);
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        scoreProgress.startAnimation(animation);

        ValueAnimator animator = ValueAnimator.ofInt(0, score);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation1 -> {
                    int n = (int) animation1.getAnimatedValue();

                }
        );
        animator.start();

        int color = getScoreColor(score);
        scoreProgress.setFinishedStrokeColor(color);

    }

    public int getScoreColor(int score) {
        if (score < 60) {
            return ContextCompat.getColor(context, R.color.red_500);
        } else if (score < 70) {
            return ContextCompat.getColor(context, R.color.orange_600);
        } else if (score < 80) {
            return ContextCompat.getColor(context, R.color.amber_400);
        }  else if (score < 90) {
            return ContextCompat.getColor(context, R.color.light_green_500);
        } else {
            return ContextCompat.getColor(context, R.color.green_500);
        }
    }

    private void initUI() {
        View view = inflate(getContext(), R.layout.custom_match_avatar_view, this);
        ButterKnife.bind(this, view);
    }

}
