package it.gruppoinfor.home2work.custom;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.function.Predicate;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.OngoingShareActivity;
import it.gruppoinfor.home2workapi.model.Share;
import it.gruppoinfor.home2workapi.model.ShareGuest;

public class OngoinShareView extends FrameLayout {

    @BindView(R.id.text_ongoing_share_info)
    TextView textOngoingShareInfo;
    @BindView(R.id.text_ongoin_share_guests)
    TextView textOngoinShareGuests;
    @BindView(R.id.share_item_ongoing_container)
    RelativeLayout shareItemOngoingContainer;
    @BindView(R.id.layout_guests_number)
    LinearLayout layoutGuestsNumber;
    private Context context;
    private Share share;

    public OngoinShareView(Context context) {
        super(context);
        this.context = context;
        initUI();
    }

    public OngoinShareView(Context context, AttributeSet attributes) {
        super(context, attributes);
        this.context = context;
        initUI();
    }

    public OngoinShareView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.context = context;
        initUI();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private void initUI() {
        View view = inflate(getContext(), R.layout.layout_share_ongoing, this);
        ButterKnife.bind(this, view);
    }

    public void setShare(Share share) {

        long guestSize = Stream.of(share.getGuests()).filter(value -> !value.getStatus().equals(ShareGuest.Status.CANCELED)).count();
        textOngoinShareGuests.setText(String.valueOf(guestSize));

        if (share.getType() == Share.Type.DRIVER) {

            textOngoingShareInfo.setText("Stai condividendo la tua auto");

        } else {

            textOngoingShareInfo.setText("Stai partecipando ad una condivisione");
            layoutGuestsNumber.setVisibility(GONE);
        }

        shareItemOngoingContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OngoingShareActivity.class);
                intent.putExtra("SHARE", share);
                context.startActivity(intent);
            }
        });

    }
}
