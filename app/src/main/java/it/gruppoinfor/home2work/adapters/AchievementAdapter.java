package it.gruppoinfor.home2work.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2workapi.model.Achievement;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private MainActivity activity;
    private ArrayList<Achievement> achievements;
    private ItemClickCallbacks itemClickCallbacks;

    public AchievementAdapter(Activity activity, List<Achievement> values) {
        this.activity = (MainActivity) activity;
        this.achievements = new ArrayList<>(values);
    }

    public void setItemClickCallbacks(ItemClickCallbacks itemClickCallbacks) {
        this.itemClickCallbacks = itemClickCallbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievement, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Achievement achievement = achievements.get(position);

        RequestOptions requestOptions = new RequestOptions().placeholder(R.color.grey_200).dontAnimate();

        Glide.with(activity)
                .load(achievement.getAchievementID())
                .apply(requestOptions)
                .into(holder.achievementIcon);

        holder.achievementName.setText(achievement.getName());
        holder.achievementDescription.setText(achievement.getDescription());
        holder.progressText.setText(String.format("%1$s/%2$s", achievement.getCurrent(), achievement.getGoal()));
        holder.progressBar.setProgress(achievement.getProgress());
        holder.expView.setText(String.format("+%1$s", achievement.getExp()));

        int color = ContextCompat.getColor(activity, R.color.colorPrimary);

        if (achievement.getProgress() == 100) {
            holder.unlockDate.setVisibility(View.VISIBLE);
            holder.progressPercentile.setVisibility(View.GONE);
            holder.expView.setTextColor(color);
            SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy", Locale.ITALIAN);
            String dateString = dateFormat.format(achievement.getUnlockDate());
            holder.unlockDate.setText(dateString);
        } else {
            holder.unlockDate.setVisibility(View.GONE);
            holder.progressPercentile.setVisibility(View.VISIBLE);
            holder.progressPercentile.setText(String.format("%1$s%%", holder.progressBar.getProgress()));
        }

    }


    @Override
    public int getItemCount() {
        return achievements.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.achievement_icon)
        ImageView achievementIcon;
        @BindView(R.id.achievement_name)
        TextView achievementName;
        @BindView(R.id.achievement_description)
        TextView achievementDescription;
        @BindView(R.id.unlock_date)
        TextView unlockDate;
        @BindView(R.id.progress_text)
        TextView progressText;
        @BindView(R.id.container)
        LinearLayout container;
        @BindView(R.id.progress_bar)
        MaterialProgressBar progressBar;
        @BindView(R.id.progress_percentile)
        TextView progressPercentile;
        @BindView(R.id.achievement_exp)
        TextView expView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
