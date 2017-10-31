package it.gruppoinfor.home2work.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class ProfileAchievementAdapter extends RecyclerView.Adapter<ProfileAchievementAdapter.ViewHolder> {

    private MainActivity activity;
    private ArrayList<Achievement> achievements;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

    public ProfileAchievementAdapter(Activity activity, List<Achievement> values) {
        this.activity = (MainActivity) activity;
        this.achievements = new ArrayList<>(values);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Achievement achievement = achievements.get(position);

        holder.achievementTitle.setText(achievement.getName());
        holder.achievementKarma.setText(String.format(Locale.ITALY, "+%1$d", achievement.getDelta()));
        holder.achievementDate.setText(dateFormat.format(achievement.getDate()));

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar_placeholder).dontAnimate();


        Glide.with(activity)
                .load(achievement.getIconURL())
                .apply(requestOptions)
                .into(holder.achievementIcon);


    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_achievement, parent, false);
        return new ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.achievement_icon)
        ImageView achievementIcon;
        @BindView(R.id.achievement_title)
        TextView achievementTitle;
        @BindView(R.id.achievement_date)
        TextView achievementDate;
        @BindView(R.id.achievement_karma)
        TextView achievementKarma;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
