package it.gruppoinfor.home2work.adapters;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.custom.MatchAvatarView;
import it.gruppoinfor.home2workapi.model.Match;

import static it.gruppoinfor.home2work.utils.Converters.dateToString;

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

        holder.matchAvatarView.setAvatarURL(match.getHost().getAvatarURL());
        holder.matchAvatarView.setScore(match.getScore());

        holder.nameView.setText(match.getHost().toString());
        holder.jobView.setText(match.getHost().getCompany().toString());

        holder.timeText.setText(
                String.format(
                        activity.getResources().getString(R.string.match_time),
                        dateToString(match.getStartTime()),
                        dateToString(match.getEndTime())
                )
        );

        ArrayList<String> days = new ArrayList<>();

        for(int d : match.getWeekdays())
            days.add(activity.getResources().getStringArray(R.array.giorni)[d]);
        holder.daysText.setText(TextUtils.join(", ", days));

        holder.container.setOnClickListener((v) -> itemClickCallbacks.onItemClick(v, position));
        holder.container.setOnLongClickListener((v) -> itemClickCallbacks.onLongItemClick(v, position));

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

    public void remove(int position) {
        matches.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, matches.size());
    }

    public void setItemClickCallbacks(ItemClickCallbacks itemClickCallbacks) {
        this.itemClickCallbacks = itemClickCallbacks;
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.match_avatar_view)
        MatchAvatarView matchAvatarView;
        @BindView(R.id.name_view)
        TextView nameView;
        @BindView(R.id.job_view)
        TextView jobView;
        @BindView(R.id.time_view)
        TextView timeText;
        @BindView(R.id.days_view)
        TextView daysText;
        @BindView(R.id.new_badge)
        ImageView newBadgeView;
        @BindView(R.id.container)
        RelativeLayout container;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
