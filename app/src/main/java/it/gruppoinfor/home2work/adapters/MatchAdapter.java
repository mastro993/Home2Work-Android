package it.gruppoinfor.home2work.adapters;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2workapi.model.Match;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    private ItemClickCallbacks itemClickCallbacks;
    private MainActivity activity;
    private ArrayList<Match> matches;
    private Resources res;

    public MatchAdapter(Activity activity, List<Match> values) {
        this.activity = (MainActivity) activity;
        this.matches = new ArrayList<>(values);
        this.res = activity.getResources();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Match match = matches.get(position);

        if (!match.isNew()) {
            holder.newBadgeView.setVisibility(View.GONE);
        } else {
            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    holder.newBadgeView,
                    PropertyValuesHolder.ofFloat("scaleX", 0.5f),
                    PropertyValuesHolder.ofFloat("scaleY", 0.5f));
            scaleDown.setDuration(250);

            scaleDown.setRepeatCount(1);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

            scaleDown.start();
        }

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder);

        Glide.with(activity)
                .load(match.getHost().getAvatarURL())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(holder.userAvatar);

        ValueAnimator animator = ValueAnimator.ofInt(0, match.getScore());
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(valueAnimator -> holder.scoreText.setText(valueAnimator.getAnimatedValue() + "%"));
        animator.start();

        int color = getScoreColor(match.getScore());
        holder.scoreText.setTextColor(color);

        holder.nameView.setText(match.getHost().toString());
        holder.jobView.setText(match.getHost().getCompany().toString());
        holder.homeView.setText("Da " + match.getHost().getAddress().getCity());

        holder.container.setOnClickListener((v) -> itemClickCallbacks.onItemClick(v, position));
        holder.container.setOnLongClickListener((v) -> itemClickCallbacks.onLongItemClick(v, position));

        if (match.getScore() == 0) holder.scoreText.setVisibility(View.INVISIBLE);

        /*
        TODO Activity info utente
        holder.userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ShowUserActivity.class);

                intent.putExtra("userID", match.getHost().getId());
                intent.putExtra("name", match.getHost().getName());
                intent.putExtra("surname", match.getHost().getSurname());

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, v, "avatar");

                activity.startActivity(intent, options.toBundle());

            }
        });*/

    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    private int getScoreColor(int score) {
        if (score < 60) {
            return ContextCompat.getColor(activity, R.color.red_500);
        } else if (score < 70) {
            return ContextCompat.getColor(activity, R.color.orange_600);
        } else if (score < 80) {
            return ContextCompat.getColor(activity, R.color.amber_400);
        } else if (score < 90) {
            return ContextCompat.getColor(activity, R.color.light_green_500);
        } else {
            return ContextCompat.getColor(activity, R.color.green_500);
        }
    }

    public void remove(int position) {
        matches.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, matches.size());
    }

    public void setItemClickCallbacks(ItemClickCallbacks itemClickCallbacks) {
        this.itemClickCallbacks = itemClickCallbacks;
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
        ImageView newBadgeView;
        @BindView(R.id.container)
        ConstraintLayout container;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
