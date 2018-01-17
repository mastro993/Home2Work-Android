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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2workapi.model.ShareGuest;

public class ShareGuestsAdapter extends RecyclerView.Adapter<ShareGuestsAdapter.ViewHolder> {


    private Activity activity;
    private ArrayList<ShareGuest> shareGuests;
    private ItemClickCallbacks itemClickCallbacks;

    private DecimalFormat mDf;

    public ShareGuestsAdapter(Activity activity, List<ShareGuest> values) {
        this.activity = activity;
        this.shareGuests = new ArrayList<>(values);
        mDf = new DecimalFormat("#.##");
        mDf.setRoundingMode(RoundingMode.CEILING);
    }

    public void setItemClickCallbacks(ItemClickCallbacks itemClickCallbacks) {
        this.itemClickCallbacks = itemClickCallbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_guest, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ShareGuest shareGuest = shareGuests.get(position);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder);

        Glide.with(activity)
                .load(shareGuest.getGuest().getAvatarURL())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(holder.userAvatar);


        holder.nameView.setText(shareGuest.getGuest().toString());
        holder.jobView.setText(shareGuest.getGuest().getCompany().toString());

        holder.container.setOnClickListener((v) -> itemClickCallbacks.onItemClick(v, position));
        holder.container.setOnLongClickListener((v) -> itemClickCallbacks.onLongItemClick(v, position));

        if (shareGuest.getStatus() == ShareGuest.Status.COMPLETED) {
            holder.viewStatusCompleted.setVisibility(View.VISIBLE);
        } else if (shareGuest.getStatus() == ShareGuest.Status.CANCELED) {
            holder.viewStatusLeaved.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(0.7f);
        }

    }

    @Override
    public int getItemCount() {
        return shareGuests.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_avatar)
        ImageView userAvatar;
        @BindView(R.id.name_view)
        TextView nameView;
        @BindView(R.id.job_view)
        TextView jobView;
        @BindView(R.id.container)
        View container;
        @BindView(R.id.guest_status_completed)
        View viewStatusCompleted;
        @BindView(R.id.guest_status_leaved)
        View viewStatusLeaved;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
