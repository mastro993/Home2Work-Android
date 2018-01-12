package it.gruppoinfor.home2work.adapters;

import android.app.Activity;
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
import it.gruppoinfor.home2work.App;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.utils.DateFormatUtils;
import it.gruppoinfor.home2workapi.model.Share;
import it.gruppoinfor.home2workapi.model.ShareGuest;

public class SharesAdapter extends RecyclerView.Adapter<SharesAdapter.ViewHolder> {


    private MainActivity activity;
    private ArrayList<Share> shares;
    private ItemClickCallbacks itemClickCallbacks;

    private DecimalFormat mDf;

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Share share = shares.get(position);

        holder.textShareDatetime.setText(DateFormatUtils.formatDate(share.getDate()));

        int guestSize = share.getGuests().size();

        holder.textShareGuests.setText(guestSize + " passeggeri");

        if (share.getType() == Share.Type.DRIVER) {


            holder.textShareType.setText("Driver");
            holder.textShareType.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            holder.imageShareType.setBackgroundResource(R.drawable.shape_share_driver);

            int totalMt = 0;
            for (ShareGuest shareguest : share.getGuests()) {
                totalMt += shareguest.getDistance();
            }

            Double totalKm = totalMt / 1000.0;
            holder.textShareDistance.setText(mDf.format(totalKm / 1000.0) + " Km");
            holder.textShareXp.setText(totalKm.intValue() * 10 + " Xp");


        } else {

            holder.textShareType.setText("Guest");
            holder.textShareType.setTextColor(activity.getResources().getColor(R.color.colorAccent));
            holder.imageShareType.setBackgroundResource(R.drawable.shape_share_guest);

            for (ShareGuest shareGuest : share.getGuests()) {
                if (shareGuest.getGuest().equals(App.home2WorkClient.getUser())) {
                    Double totalKm = shareGuest.getDistance() / 1000.0;
                    holder.textShareDistance.setText(mDf.format(totalKm / 1000.0) + " Km");
                    holder.textShareXp.setText(totalKm.intValue() * 10 + " Xp");
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return shares.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_share_type)
        ImageView imageShareType;
        @BindView(R.id.text_share_type)
        TextView textShareType;
        @BindView(R.id.text_share_datetime)
        TextView textShareDatetime;
        @BindView(R.id.text_share_guests)
        TextView textShareGuests;
        @BindView(R.id.text_share_distance)
        TextView textShareDistance;
        @BindView(R.id.text_share_fuel)
        TextView textShareFuel;
        @BindView(R.id.text_share_emission)
        TextView textShareEmission;
        @BindView(R.id.text_share_xp)
        TextView textShareXp;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
