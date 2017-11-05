package it.gruppoinfor.home2work.adapters;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2workapi.enums.BookingStatus;
import it.gruppoinfor.home2workapi.model.Achievement;
import it.gruppoinfor.home2workapi.model.Booking;
import it.gruppoinfor.home2workapi.model.Match;

import static it.gruppoinfor.home2work.utils.Converters.dateToString;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private MainActivity activity;
    private ArrayList<Achievement> achievements;

    public AchievementAdapter(Activity activity, List<Achievement> values) {
        this.activity = (MainActivity) activity;
        this.achievements = new ArrayList<>(values);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievement, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Achievement achievement = achievements.get(position);



    }


    @Override
    public int getItemCount() {
        return achievements.size();
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
        @BindView(R.id.arrival_time_view)
        TextView arrivalTimeView;
        @BindView(R.id.departure_time_view)
        TextView departureTimeView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
