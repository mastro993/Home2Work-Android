package it.gruppoinfor.home2work.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Share;

public class OngoingSharesAdapter extends RecyclerView.Adapter<OngoingSharesAdapter.ViewHolder> {

    private MainActivity activity;
    private ArrayList<Share> shares;
    private ItemClickCallbacks itemClickCallbacks;

    public OngoingSharesAdapter(Activity activity, List<Share> values) {
        this.activity = (MainActivity) activity;
        this.shares = new ArrayList<>(values);
    }

    public void setItemClickCallbacks(ItemClickCallbacks itemClickCallbacks) {
        this.itemClickCallbacks = itemClickCallbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ongoing_share, parent, false);
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

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_avatar_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop();

        if (share.getBooking().getBookedMatch().getGuest().getId().equals(Client.User.getId())) {

            Glide.with(activity)
                    .load(share.getBooking().getBookedMatch().getHost().getAvatarURL())
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.userAvatar);

            holder.userName.setText(share.getBooking().getBookedMatch().getHost().toString());
            karmaPoints = kmDistance.intValue();
            exp = (int) (kmDistance * 10);
        } else {

            Glide.with(activity)
                    .load(share.getBooking().getBookedMatch().getGuest().getAvatarURL())
                    .apply(requestOptions)
                    .into(holder.userAvatar);

            holder.userName.setText(share.getBooking().getBookedMatch().getGuest().toString());
            karmaPoints = (int) (kmDistance.intValue() * 1.2);
            exp = (int) (kmDistance * 12);
        }

        holder.container.setOnClickListener(view -> itemClickCallbacks.onItemClick(view, position));
        holder.container.setOnLongClickListener(view -> itemClickCallbacks.onLongItemClick(view, position));

    }


    @Override
    public int getItemCount() {
        return shares.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_avatar)
        ImageView userAvatar;
        @BindView(R.id.user_name_view)
        TextView userName;
        @BindView(R.id.container)
        LinearLayout container;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
