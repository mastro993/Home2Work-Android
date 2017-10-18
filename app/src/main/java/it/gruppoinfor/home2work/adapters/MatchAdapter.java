package it.gruppoinfor.home2work.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MatchActivity;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.models.Match;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<Match> matches;
    private ViewHolder holder;

    public MatchAdapter(Activity activity, List<Match> values) {
        this.activity = activity;
        this.matches = new ArrayList<>(values);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Match match = matches.get(position);

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        holder.scoreProgress.setProgress(match.getScore());

        Glide.with(activity)
                .load(match.getHost().getAvatarURL())
                .placeholder(R.drawable.ic_user)
                .dontAnimate()
                .into(holder.userAvatar);

        holder.scoreText.setText(match.getScore() + "%");
        holder.nameView.setText(match.getHost().toString());
        holder.distanceView.setText("Lunghezza match: " + df.format(match.getLength()) + " Km");
        holder.consumptionView.setText("Carburante risparmiato " + df.format(match.getCunsumption()) + " l");
        holder.emissionView.setText("Emissioni evitate: " + df.format(match.getEmission()) + " g CO2");
        if (!match.isNew()) holder.newIcon.setVisibility(View.GONE);

        int color;
        Drawable bg = ContextCompat.getDrawable(activity, R.drawable.bg_match_score_percent);
        if (match.getScore() < 60) {
            color = ContextCompat.getColor(activity, R.color.red_500);
        } else if (match.getScore() < 80) {
            color = ContextCompat.getColor(activity, R.color.orange_500);
        } else {
            color = ContextCompat.getColor(activity, R.color.green_500);
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

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (match.isNew()) setMatchAsViewed(match);

                Intent matchIntent = new Intent(activity, MatchActivity.class);
                matchIntent.putExtra("match", match);

                int left = 0, top = 0;
                int width = holder.container.getMeasuredWidth(), height = holder.container.getMeasuredHeight();

                ActivityOptionsCompat opts = ActivityOptionsCompat.makeClipRevealAnimation(holder.container, left, top, width, height);
                activity.startActivity(matchIntent, opts.toBundle());
            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(activity, v);
                popup.getMenuInflater().inflate(R.menu.match_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.show_match_details:
                                showMatchDetails(match);
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
                    }
                });
                popup.show();
                return true;
            }
        });
    }


   private void showMatchDetails(Match match) {
        if (match.isNew()) setMatchAsViewed(match);
        Intent matchIntent = new Intent(activity, MatchActivity.class);
        matchIntent.putExtra("match", match);
        activity.startActivity(matchIntent);
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
        builder.setTitle("Nascondi match");
        builder.setMessage("Nascondendo questo match non avrai più la possibilità di visualizzarlo. Sei sicuro di voler continuare?");
        builder.setPositiveButton("Nascondi match", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hideMatch(position);
            }
        });
        builder.setNegativeButton("Annulla", null);
        builder.show();
    }

    private void hideMatch(final int position) {
        final Match match = matches.get(position);

        match.setHidden(true);

        Client.getAPI().editMatch(match).enqueue(new Callback<Match>() {
            @Override
            public void onResponse(Call<Match> call, Response<Match> response) {
                matches.remove(position);
                notifyItemRemoved(position);
                Toasty.success(activity, "Match rimosso", Toast.LENGTH_SHORT, true).show();
            }

            @Override
            public void onFailure(Call<Match> call, Throwable t) {
                Toasty.error(activity, "Impossibile rimuovere il match al momento, riprova più tardi").show();
                t.printStackTrace();

            }
        });
    }

    private void setMatchAsViewed(Match match) {

        match.setNew(false);

        Client.getAPI().editMatch(match).enqueue(new Callback<Match>() {
            @Override
            public void onResponse(Call<Match> call, Response<Match> response) {
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Match> call, Throwable t) {

            }
        });

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
