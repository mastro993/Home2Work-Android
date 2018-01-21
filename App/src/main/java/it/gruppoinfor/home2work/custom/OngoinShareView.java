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

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.OngoingShareActivity;
import it.gruppoinfor.home2workapi.model.Guest;
import it.gruppoinfor.home2workapi.model.Share;

import static it.gruppoinfor.home2work.activities.OngoingShareActivity.EXTRA_SHARE;

public class OngoinShareView extends FrameLayout {

    @BindView(R.id.text_ongoing_share_info)
    TextView textOngoingShareInfo;
    @BindView(R.id.text_ongoin_share_guests)
    TextView textOngoinShareGuests;
    @BindView(R.id.share_item_ongoing_container)
    RelativeLayout shareItemOngoingContainer;
    @BindView(R.id.layout_guests_number)
    LinearLayout layoutGuestsNumber;
    private Context mContext;

    public OngoinShareView(Context context) {
        super(context);
        mContext = context;
        initUI();
    }

    public OngoinShareView(Context context, AttributeSet attributes) {
        super(context, attributes);
        mContext = context;
        initUI();
    }

    public OngoinShareView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        mContext = context;
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

        long guestSize = Stream.of(share.getGuests()).filter(value -> !value.getStatus().equals(Guest.Status.CANCELED)).count();
        textOngoinShareGuests.setText(String.valueOf(guestSize));

        if (share.getType() == Share.Type.DRIVER) {

            textOngoingShareInfo.setText(R.string.layout_ongoing_share_host);

        } else {

            textOngoingShareInfo.setText(R.string.layout_ongoing_share_guest);
            layoutGuestsNumber.setVisibility(GONE);
        }

        shareItemOngoingContainer.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, OngoingShareActivity.class);
            intent.putExtra(EXTRA_SHARE, share);
            mContext.startActivity(intent);
        });

    }
}
