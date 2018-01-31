package it.gruppoinfor.home2work.adapters;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.ShowUserActivity;
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks;
import it.gruppoinfor.home2workapi.model.Match;
import it.gruppoinfor.home2workapi.model.User;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    private ItemClickCallbacks mItemClickCallbacks;
    private Context mContext;
    private ArrayList<Match> mMatches;

    public MatchAdapter(Context context, List<Match> values) {
        mContext = context;
        mMatches = new ArrayList<>(values);
    }

    @Override
    public MatchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchAdapter.ViewHolder(layoutView);

    }

    @Override
    public void onBindViewHolder(final MatchAdapter.ViewHolder holder, final int position) {
        final Match match = mMatches.get(position);

        if (!match.isNew()) {
            holder.newBadgeView.setVisibility(View.INVISIBLE);
        } else {
            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    holder.newBadgeView,
                    PropertyValuesHolder.ofFloat("scaleX", 0.8f),
                    PropertyValuesHolder.ofFloat("scaleY", 0.8f));
            scaleDown.setDuration(150);

            scaleDown.setRepeatCount(1);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

            scaleDown.start();
        }

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder);

        Glide.with(mContext)
                .load(match.getHost().getAvatarURL())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(holder.userAvatar);

        ValueAnimator animator = ValueAnimator.ofInt(0, match.getScore());
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(valueAnimator -> holder.scoreText.setText(String.format(Locale.ITALIAN, "%1$d%%", (Integer) valueAnimator.getAnimatedValue())));
        animator.start();

        int color = getScoreColor(match.getScore());
        holder.scoreText.setTextColor(color);

        holder.nameView.setText(match.getHost().toString());
        holder.jobView.setText(match.getHost().getCompany().toString());
        holder.homeView.setText(match.getHost().getAddress().getCity());

        holder.container.setOnClickListener((v) -> mItemClickCallbacks.onItemClick(v, position));
        holder.container.setOnLongClickListener((v) -> mItemClickCallbacks.onLongItemClick(v, position));

        if (match.getScore() == 0) holder.scoreText.setVisibility(View.INVISIBLE);

        if (position == 0) holder.divider.setVisibility(View.GONE);

        holder.userAvatar.setOnClickListener(v -> {
            Intent userIntent = new Intent(mContext, ShowUserActivity.class);
            userIntent.putExtra("user", match.getHost());
            mContext.startActivity(userIntent);

        });
    }

    @Override
    public int getItemCount() {
        return mMatches.size();
    }

    private int getScoreColor(int score) {
        if (score < 60) {
            return ContextCompat.getColor(mContext, R.color.red_500);
        } else if (score < 70) {
            return ContextCompat.getColor(mContext, R.color.orange_600);
        } else if (score < 80) {
            return ContextCompat.getColor(mContext, R.color.amber_400);
        } else if (score < 90) {
            return ContextCompat.getColor(mContext, R.color.light_green_500);
        } else {
            return ContextCompat.getColor(mContext, R.color.green_500);
        }
    }

    public void remove(int position) {
        mMatches.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mMatches.size());
    }

    public void setItemClickCallbacks(ItemClickCallbacks itemClickCallbacks) {
        mItemClickCallbacks = itemClickCallbacks;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_avatar)
        ImageView userAvatar;
        @BindView(R.id.score_text)
        TextView scoreText;
        @BindView(R.id.name_view)
        TextView nameView;
        @BindView(R.id.job_view)
        TextView jobView;
        @BindView(R.id.home_view)
        TextView homeView;
        @BindView(R.id.new_badge)
        View newBadgeView;
        @BindView(R.id.container)
        View container;
        @BindView(R.id.divider)
        View divider;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
