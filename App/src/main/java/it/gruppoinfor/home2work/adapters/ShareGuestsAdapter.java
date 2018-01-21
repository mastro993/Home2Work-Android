package it.gruppoinfor.home2work.adapters;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks;
import it.gruppoinfor.home2workapi.model.Guest;

public class ShareGuestsAdapter extends RecyclerView.Adapter<ShareGuestsAdapter.ViewHolder> {


    private Context mContext;
    private ArrayList<Guest> mGuests;
    private ItemClickCallbacks mItemClickCallbacks;

    public ShareGuestsAdapter(Context context, List<Guest> values) {
        mContext = context;
        mGuests = new ArrayList<>(values);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_guest, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Guest guest = mGuests.get(position);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder);

        Glide.with(mContext)
                .load(guest.getUser().getAvatarURL())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(holder.userAvatar);


        holder.nameView.setText(guest.getUser().toString());
        holder.jobView.setText(guest.getUser().getCompany().toString());

        holder.container.setOnClickListener((v) -> mItemClickCallbacks.onItemClick(v, position));
        holder.container.setOnLongClickListener((v) -> mItemClickCallbacks.onLongItemClick(v, position));

        if (guest.getStatus() == Guest.Status.COMPLETED) {
            holder.viewStatusCompleted.setVisibility(View.VISIBLE);
        } else if (guest.getStatus() == Guest.Status.CANCELED) {
            holder.viewStatusLeaved.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(0.7f);
        }

    }

    @Override
    public int getItemCount() {
        return mGuests.size();
    }

    public void setItemClickCallbacks(ItemClickCallbacks itemClickCallbacks) {
        mItemClickCallbacks = itemClickCallbacks;
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
