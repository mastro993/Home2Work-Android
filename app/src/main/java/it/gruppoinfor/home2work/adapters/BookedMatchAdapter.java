package it.gruppoinfor.home2work.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.models.BookedMatchItem;
import it.gruppoinfor.home2work.models.MatchItem;

public class BookedMatchAdapter extends RecyclerView.Adapter<BookedMatchAdapter.ViewHolder> {

    private MainActivity activity;
    private ArrayList<BookedMatchItem> bookedMatches;
    private Resources res;

    public BookedMatchAdapter(Activity activity, List<BookedMatchItem> values) {
        this.activity = (MainActivity) activity;
        this.bookedMatches = new ArrayList<>(values);
        this.res = activity.getResources();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final BookedMatchItem bookedMatchItem = bookedMatches.get(position);
        final MatchItem matchItem = bookedMatchItem.getBookedMatch();

        // TODO info prenotazione

    }

    @Override
    public int getItemCount() {
        return bookedMatches.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booked_match, parent, false);
        return new ViewHolder(v);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_avatar)
        CircleImageView userAvatar;
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
