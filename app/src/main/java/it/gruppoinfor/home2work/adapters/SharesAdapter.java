package it.gruppoinfor.home2work.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.utils.DateFormatUtils;
import it.gruppoinfor.home2workapi.Home2WorkClient;
import it.gruppoinfor.home2workapi.model.Share;
import it.gruppoinfor.home2workapi.model.ShareGuest;

public class SharesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private MainActivity activity;
    private ArrayList<Share> shares;
    private ItemClickCallbacks itemClickCallbacks;

    private DecimalFormat mDf;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public SharesAdapter(Activity activity, List<Share> values) {
        this.activity = (MainActivity) activity;
        this.shares = new ArrayList<>(values);
        mDf = new DecimalFormat("#.##");
        mDf.setRoundingMode(RoundingMode.CEILING);
    }

    public void setItemClickCallbacks(ItemClickCallbacks itemClickCallbacks) {
        this.itemClickCallbacks = itemClickCallbacks;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_header, parent, false);
            return new HeaderViewHolder(layoutView);
        } else if (viewType == TYPE_ITEM) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share, parent, false);
            return new ItemViewHolder(layoutView);
        }
        throw new RuntimeException("No match for " + viewType + ".");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder h, final int position) {
        if (h instanceof ItemViewHolder) {
            ItemViewHolder holder = (ItemViewHolder) h;
            final Share share = shares.get(position);

            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .circleCrop()
                    .placeholder(R.drawable.ic_avatar_placeholder);

            Glide.with(activity)
                    .load(share.getHost().getAvatarURL())
                    .apply(requestOptions)
                    .into(holder.hostAvatarView);

            holder.dateView.setText(DateFormatUtils.formatDate(share.getDate()));
            holder.hostNameView.setText(share.getHost().toString());

            int guestSize = share.getGuests().size();

            holder.guestsView.setText(guestSize + " passeggeri");

            if (share.getHost().equals(Home2WorkClient.User)) {

                int totalMt = 0;
                for (ShareGuest shareguest : share.getGuests()) {
                    totalMt += shareguest.getDistance();
                }

                Double totalKm = totalMt / 1000.0;
                holder.distanceView.setText(mDf.format(totalKm / 1000.0) + " Km");
                holder.xpView.setText(totalKm.intValue() * 10 + " Xp");

            } else {
                for (ShareGuest shareGuest : share.getGuests()) {
                    if (shareGuest.getGuest().equals(Home2WorkClient.User)) {
                        Double totalKm = shareGuest.getDistance() / 1000.0;
                        holder.distanceView.setText(mDf.format(totalKm / 1000.0) + " Km");
                        holder.xpView.setText(totalKm.intValue() * 10 + " Xp");
                    }
                }
            }

            if (position == shares.size() - 1) holder.divider.setVisibility(View.GONE);

        }


    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return shares.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.host_avatar_view)
        ImageView hostAvatarView;
        @BindView(R.id.host_name_view)
        TextView hostNameView;
        @BindView(R.id.date_view)
        TextView dateView;
        @BindView(R.id.guests_view)
        TextView guestsView;
        @BindView(R.id.distance_view)
        TextView distanceView;
        @BindView(R.id.xp_view)
        TextView xpView;
        @BindView(R.id.divider)
        View divider;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
