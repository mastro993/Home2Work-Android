package it.gruppoinfor.home2work.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.utils.ScoreColorUtility;
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
        final Match matchItem = matches.get(position);

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        holder.scoreProgress.setProgress(Integer.parseInt(matchItem.getScore().toString()));

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar_placeholder).dontAnimate();

        Glide.with(activity)
                .load(matchItem.getHost().getAvatarURL())
                .apply(requestOptions)
                .into(holder.userAvatar);

        holder.scoreText.setText(String.format(Locale.ITALY, "%1$d%%", matchItem.getScore()));
        holder.nameView.setText(matchItem.getHost().toString());

        if (!matchItem.isNew()) holder.newBadgeView.setVisibility(View.GONE);

        int color = ScoreColorUtility.getScoreColor(activity, matchItem.getScore());
        Drawable bg = ContextCompat.getDrawable(activity, R.drawable.bg_match_score_percent);

        holder.scoreProgress.setFinishedStrokeColor(color);
        bg.setTint(color);
        holder.scoreText.setBackground(bg);
        holder.distanceView.setText(String.format(res.getString(R.string.match_item_shared_distance), matchItem.getSharedDistance().toString()));
        holder.arrivalTimeText.setText(String.format(res.getString(R.string.match_item_arrival_time), dateToString(matchItem.getArrivalTime())));
        holder.departureTimeText.setText(String.format(res.getString(R.string.match_item_departure_time), dateToString(matchItem.getDepartureTime())));

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
        @BindView(R.id.score_progress)
        ArcProgress scoreProgress;
        @BindView(R.id.user_avatar)
        CircleImageView userAvatar;
        @BindView(R.id.score_text)
        TextView scoreText;
        @BindView(R.id.name_view)
        TextView nameView;
        @BindView(R.id.distance_view)
        TextView distanceView;
        @BindView(R.id.arrival_time_view)
        TextView arrivalTimeText;
        @BindView(R.id.departure_time_view)
        TextView departureTimeText;
        @BindView(R.id.new_badge_view)
        LinearLayout newBadgeView;
        @BindView(R.id.container)
        LinearLayout container;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
