package it.gruppoinfor.home2work.adapters;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2workapi.model.Booking;
import it.gruppoinfor.home2workapi.model.Match;

import static it.gruppoinfor.home2work.utils.Converters.dateToString;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private MainActivity activity;
    private ArrayList<Booking> bookedMatches;
    private Resources res;
    private ItemClickCallbacks itemClickCallbacks;

    public RequestAdapter(Activity activity, List<Booking> values) {
        this.activity = (MainActivity) activity;
        this.bookedMatches = new ArrayList<>(values);
        this.res = activity.getResources();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Booking bookedMatchItem = bookedMatches.get(position);
        final Match matchItem = bookedMatchItem.getBookedMatch();

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar_placeholder).dontAnimate();

        Glide.with(activity)
                .load(matchItem.getGuest().getAvatarURL())
                .apply(requestOptions)
                .into(holder.userAvatar);

        holder.nameView.setText(matchItem.getGuest().toString());

        int color;
        String statusText;
        int icon;
        Drawable bg = ContextCompat.getDrawable(activity, R.drawable.shape_badge_circle);


        switch (bookedMatchItem.getBookingStatus()) {
            case Booking.ACCEPTED:
                color = ContextCompat.getColor(activity, R.color.green_500);
                icon = R.drawable.ic_check;
                holder.statusIcon.setVisibility(View.GONE);
                break;
            case Booking.PENDING:
                color = ContextCompat.getColor(activity, R.color.amber_500);
                icon = R.drawable.ic_clock;
                break;
            case Booking.CANCELED:
            default:
                color = ContextCompat.getColor(activity, R.color.red_500);
                icon = R.drawable.ic_close;
                break;

        }

        bg.setTint(color);
        holder.statusIcon.setBackground(bg);
        holder.statusIcon.setImageResource(icon);

        holder.timeView.setText(dateToString(matchItem.getStartTime()) + " - " + dateToString(matchItem.getEndTime()));

        holder.container.setOnClickListener((v) -> itemClickCallbacks.onItemClick(v, position));
        holder.container.setOnLongClickListener((v) -> itemClickCallbacks.onLongItemClick(v, position));

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ITALIAN);
        String dateString = dateFormat.format(bookedMatchItem.getBookedDate());

        holder.dateView.setText(WordUtils.capitalize(dateString));

        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                holder.statusIcon,
                PropertyValuesHolder.ofFloat("scaleX", 0.8f),
                PropertyValuesHolder.ofFloat("scaleY", 0.8f));
        scaleDown.setDuration(250);

        scaleDown.setRepeatCount(1);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

        scaleDown.start();

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
        @BindView(R.id.user_avatar)
        ImageView userAvatar;
        @BindView(R.id.container)
        LinearLayout container;
        @BindView(R.id.name_view)
        TextView nameView;
        @BindView(R.id.date_view)
        TextView dateView;
        @BindView(R.id.time_view)
        TextView timeView;
        @BindView(R.id.status_badge_icon)
        ImageView statusIcon;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
