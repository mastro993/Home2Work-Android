package it.gruppoinfor.home2work.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks;
import it.gruppoinfor.home2work.utils.DateFormatUtils;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.model.Guest;
import it.gruppoinfor.home2workapi.model.Share;

public class SharesAdapter extends RecyclerView.Adapter<SharesAdapter.ViewHolder> {


    private Context mContext;
    private ArrayList<Share> mShares;
    private ItemClickCallbacks mItemCallbacks;

    private DecimalFormat mDf;

    public SharesAdapter(Context context, List<Share> values) {
        mContext = context;
        mShares = new ArrayList<>(values);
        mDf = new DecimalFormat("#.##");
        mDf.setRoundingMode(RoundingMode.CEILING);
    }

    public void setItemClickCallbacks(ItemClickCallbacks itemClickCallbacks) {
        mItemCallbacks = itemClickCallbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Share share = mShares.get(position);

        holder.textShareDatetime.setText(DateFormatUtils.formatDate(share.getDate()));

        int guestSize = share.getGuests().size();

        holder.textShareGuests.setText(String.valueOf(guestSize));

        if (share.getType() == Share.Type.DRIVER) {

            holder.textShareInfo.setText("Hai condiviso la tua auto");

            holder.textShareType.setText("Driver");
            holder.textShareType.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));

            int totalMt = 0;
            for (Guest shareguest : share.getGuests()) {
                totalMt += shareguest.getDistance();
            }

            Double totalKm = totalMt / 1000.0;
            holder.textShareDistance.setText(mDf.format(totalKm));
            holder.textShareXp.setText(String.valueOf(totalKm.intValue() * 10));


        } else {

            holder.textShareInfo.setText("Hai condiviso l'auto di " + share.getHost().toString());

            holder.textShareType.setText("Guest");
            holder.textShareType.setTextColor(mContext.getResources().getColor(R.color.colorAccent));

            for (Guest guest : share.getGuests()) {
                if (guest.getUser().equals(HomeToWorkClient.getUser())) {
                    Double totalKm = guest.getDistance() / 1000.0;
                    holder.textShareDistance.setText(mDf.format(totalKm));
                    holder.textShareXp.setText(String.valueOf(totalKm.intValue() * 10));
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return mShares.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_share_type)
        TextView textShareType;
        @BindView(R.id.text_share_datetime)
        TextView textShareDatetime;
        @BindView(R.id.text_share_guests)
        TextView textShareGuests;
        @BindView(R.id.text_share_distance)
        TextView textShareDistance;
        @BindView(R.id.text_share_xp)
        TextView textShareXp;
        @BindView(R.id.text_share_info)
        TextView textShareInfo;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
