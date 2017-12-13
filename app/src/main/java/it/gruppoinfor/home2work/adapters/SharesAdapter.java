package it.gruppoinfor.home2work.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Share;

public class SharesAdapter extends RecyclerView.Adapter<SharesAdapter.ViewHolder> {

    private MainActivity activity;
    private ArrayList<Share> shares;
    private ItemClickCallbacks itemClickCallbacks;

    public SharesAdapter(Activity activity, List<Share> values) {
        this.activity = (MainActivity) activity;
        this.shares = new ArrayList<>(values);
    }

    public void setItemClickCallbacks(ItemClickCallbacks itemClickCallbacks) {
        this.itemClickCallbacks = itemClickCallbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Share share = shares.get(position);

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        Double kmDistance = share.getBooking().getBookedMatch().getDistance() / 1000.0;
        int karmaPoints;
        int exp;

        if (share.getBooking().getBookedMatch().getGuest().getId() == Client.User.getId()) {
            holder.nameInfoView.setText(String.format("Condivisione con %1$s", share.getBooking().getBookedMatch().getHost().toString()));
            karmaPoints = kmDistance.intValue();
            exp = (int) (kmDistance * 10);
        } else {
            holder.nameInfoView.setText(String.format("Condivisione con %1$s", share.getBooking().getBookedMatch().getGuest().toString()));
            karmaPoints = (int)(kmDistance.intValue() * 1.2);
            exp = (int) (kmDistance * 12);
        }

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar_placeholder).dontAnimate();

        Glide.with(activity)
                .load(share.getBooking().getBookedMatch().getGuest().getAvatarURL())
                .apply(requestOptions)
                .into(holder.guestAvatar);

        Glide.with(activity)
                .load(share.getBooking().getBookedMatch().getHost().getAvatarURL())
                .apply(requestOptions)
                .into(holder.hostAvatar);



        holder.distanceView.setText(String.format("%1$s Km", share.getBooking().getBookedMatch().getDistance()));
        holder.karmaView.setText(String.format("%1$s punti Karma", karmaPoints));
        holder.expView.setText(String.format("%1$s XP", exp));

    }


    @Override
    public int getItemCount() {
        return shares.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.guest_avatar)
        CircleImageView guestAvatar;
        @BindView(R.id.host_avatar)
        CircleImageView hostAvatar;
        @BindView(R.id.name_info_view)
        TextView nameInfoView;
        @BindView(R.id.distance_view)
        TextView distanceView;
        @BindView(R.id.karma_view)
        TextView karmaView;
        @BindView(R.id.exp_view)
        TextView expView;
        @BindView(R.id.container)
        LinearLayout container;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
