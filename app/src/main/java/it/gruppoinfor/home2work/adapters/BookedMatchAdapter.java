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
import it.gruppoinfor.home2work.models.BookingItem;
import it.gruppoinfor.home2work.models.MatchItem;

import static it.gruppoinfor.home2work.utils.Converters.dateToString;

public class BookedMatchAdapter extends RecyclerView.Adapter<BookedMatchAdapter.ViewHolder> {

    private MainActivity activity;
    private ArrayList<BookingItem> bookedMatches;
    private Resources res;
    private ItemClickCallbacks itemClickCallbacks;

    public BookedMatchAdapter(Activity activity, List<BookingItem> values) {
        this.activity = (MainActivity) activity;
        this.bookedMatches = new ArrayList<>(values);
        this.res = activity.getResources();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_booked, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final BookingItem bookedMatchItem = bookedMatches.get(position);
        final MatchItem matchItem = bookedMatchItem.getBookedMatch();

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar_placeholder).dontAnimate();

        Glide.with(activity)
                .load(matchItem.getHost().getAvatarURL())
                .apply(requestOptions)
                .into(holder.userAvatar);

        holder.scoreText.setText(String.format(Locale.ITALY, "%1$d%%", matchItem.getScore()));
        holder.nameView.setText(matchItem.getHost().toString());

        int color;
        Drawable bg = ContextCompat.getDrawable(activity, R.drawable.bg_match_score_percent);
        if (matchItem.getScore() < 50) {
            color = ContextCompat.getColor(activity, R.color.red_500);
        } else if (matchItem.getScore() < 70) {
            color = ContextCompat.getColor(activity, R.color.amber_500);
        } else if (matchItem.getScore() < 90) {
            color = ContextCompat.getColor(activity, R.color.light_green_500);
        } else {
            color = ContextCompat.getColor(activity, R.color.green_500);
        }

        holder.scoreProgress.setFinishedStrokeColor(color);
        bg.setTint(color);
        holder.scoreText.setBackground(bg);

        holder.scoreProgress.setProgress(Integer.parseInt(matchItem.getScore().toString()));

        holder.arrivalTimeView.setText(String.format(res.getString(R.string.match_item_arrival_time), dateToString(matchItem.getArrivalTime())));
        holder.departureTimeView.setText(String.format(res.getString(R.string.match_item_departure_time), dateToString(matchItem.getDepartureTime())));

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

    @Override
    public int getItemCount() {
        return bookedMatches.size();
    }

    public void setItemClickCallbacks(ItemClickCallbacks itemClickCallbacks) {
        this.itemClickCallbacks = itemClickCallbacks;
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
        @BindView(R.id.arrival_time_view)
        TextView arrivalTimeView;
        @BindView(R.id.departure_time_view)
        TextView departureTimeView;
        @BindView(R.id.container)
        LinearLayout container;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
