package it.gruppoinfor.home2work.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.MatchActivity;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.models.Match;
import it.gruppoinfor.home2work.models.MatchItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    private MainActivity activity;
    private ArrayList<MatchItem> matches;
    private Resources res;

    public MatchAdapter(Activity activity, List<MatchItem> values) {
        this.activity = (MainActivity) activity;
        this.matches = new ArrayList<>(values);
        this.res = activity.getResources();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MatchItem matchItem = matches.get(position);

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        holder.scoreProgress.setProgress(Integer.parseInt(matchItem.getScore().toString()));

        Glide.with(activity)
                .load(matchItem.getHostUser().getAvatarURL())
                .placeholder(R.drawable.ic_avatar_placeholder)
                .dontAnimate()
                .into(holder.userAvatar);

        holder.scoreText.setText(String.format(Locale.ITALY, "%1$d%%", matchItem.getScore()));
        holder.nameView.setText(matchItem.getHostUser().toString());
        holder.distanceView.setText(
                String.format(
                        res.getString(R.string.match_item_distance),
                        df.format(matchItem.getDistance())
                )
        );
        /*holder.consumptionView.setText(
                String.format(
                        res.getString(R.string.match_item_gas),
                        df.format(matchItem.get())
                )
        );

        holder.emissionView.setText(
                String.format(
                        res.getString(R.string.match_item_emission),
                        df.format(matchItem.getEmission())
                )
        );*/

        if (!matchItem.isNew()) holder.newIcon.setVisibility(View.GONE);

        int color;
        Drawable bg = ContextCompat.getDrawable(activity, R.drawable.bg_match_score_percent);
        if (matchItem.getScore() < 50) {
            color = ContextCompat.getColor(activity, R.color.red_500);
        } else if (matchItem.getScore() < 70) {
            color = ContextCompat.getColor(activity, R.color.orange_500);
        } else if (matchItem.getScore() < 90) {
            color = ContextCompat.getColor(activity, R.color.green_500);
        } else {
            color = ContextCompat.getColor(activity, R.color.colorAccent);
        }

        holder.scoreProgress.setFinishedStrokeColor(color);
        bg.setTint(color);
        holder.scoreText.setBackground(bg);

        /*        holder.userAvatar.setOnClickListener(new View.OnClickListener() {
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

        holder.container.setOnClickListener((v) -> {
            if (matchItem.isNew()) setMatchAsViewed(position);

            Intent matchIntent = new Intent(activity, MatchActivity.class);
            matchIntent.putExtra("matchID", matchItem.getMatchID());

            int left = 0, top = 0;
            int width = holder.container.getMeasuredWidth(), height = holder.container.getMeasuredHeight();

            ActivityOptionsCompat opts = ActivityOptionsCompat.makeClipRevealAnimation(holder.container, left, top, width, height);
            activity.startActivity(matchIntent, opts.toBundle());
        });

        holder.container.setOnLongClickListener((v) -> {
            PopupMenu popup = new PopupMenu(activity, v);
            popup.getMenuInflater().inflate(R.menu.match_menu, popup.getMenu());
            popup.setOnMenuItemClickListener((item) -> {
                switch (item.getItemId()) {
                    case R.id.show_match_details:
                        showMatchDetails(position);
                        break;
                    case R.id.show_match_profile:
                        //showMatchUserProfile(match);
                        break;
                    case R.id.hide_match:
                        showHideMatchDialog(position);
                        break;
                    default:
                        break;
                }
                return true;
            });
            popup.show();
            return true;
        });

    }


    private void showMatchDetails(final int position) {
        MatchItem matchItem = matches.get(position);

        /*if (match.isNew()) setMatchAsViewed(position);
        Intent matchIntent = new Intent(activity, MatchActivity.class);
        matchIntent.putExtra("matchID", matchItem.getMatchID());
        activity.startActivity(matchIntent);*/

    }

/*    private void showMatchUserProfile(Match match) {
        Intent userIntent = new Intent(activity, ShowUserActivity.class);
        User matchedUser = match.getHost();
        userIntent.putExtra("userID", matchedUser.getId());
        activity.startActivity(userIntent);
    }*/

    @Override
    public int getItemCount() {
        return matches.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new ViewHolder(v);
    }

    private void showHideMatchDialog(final int position) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        builder.setTitle(res.getString(R.string.match_item_hide_dialog_title));
        builder.setMessage(res.getString(R.string.match_item_hide_dialog_content));
        builder.setPositiveButton(
                res.getString(R.string.match_item_hide_dialog_confirm),
                (dialogInterface, i) -> hideMatch(position)
        );
        builder.setNegativeButton(
                res.getString(R.string.match_item_hide_dialog_cancel),
                null
        );
        builder.show();
    }

    private void hideMatch(final int position) {
        /*final MatchItem match = matches.get(position);

        match.setHidden(true);

        Client.getAPI().editMatch(match).enqueue(new SessionManagerCallback<Match>() {
            @Override
            public void onResponse(Call<Match> call, Response<Match> response) {
                matches.remove(position);
                notifyItemRemoved(position);
                Toasty.success(activity, res.getString(R.string.match_item_hided), Toast.LENGTH_SHORT, true).show();
            }

            @Override
            public void onFailure(Call<Match> call, Throwable t) {
                Toasty.error(activity, res.getString(R.string.match_item_hide_error)).show();
                t.printStackTrace();

            }
        });*/
    }

    private void setMatchAsViewed(final int position) {
        matches.get(position).setNew(false);

        /* TODO edit match
        Client.getAPI().editMatch(matches.get(position)).enqueue(new SessionManagerCallback<Match>() {
            @Override
            public void onResponse(Call<Match> call, Response<Match> response) {
                Stream<Match> matchStream = matches.stream();
                long newMatches = matchStream.filter(Match::isNew).count();

                if (newMatches > 0) {
                    activity.bottomNavigation.setNotification(Long.toString(newMatches), 1);
                }

                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Match> call, Throwable t) {

            }
        });*/

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
        @BindView(R.id.consumption_view)
        TextView consumptionView;
        @BindView(R.id.emission_view)
        TextView emissionView;
        @BindView(R.id.new_label)
        TextView newIcon;
        @BindView(R.id.container)
        RelativeLayout container;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
