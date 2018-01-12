package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.App;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.services.MessagingService;
import it.gruppoinfor.home2work.utils.QREncoder;
import it.gruppoinfor.home2workapi.model.Share;

public class OngoingShareActivity extends AppCompatActivity {

    @BindView(R.id.loading_view)
    FrameLayout loadingView;

    Share share;
    @BindView(R.id.qr_code_button)
    Button qrCodeButton;
    @BindView(R.id.guests_recycler_view)
    RecyclerView guestsRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO messaggio ricevuto ricarico gli ospiti
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_share);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingView.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        Long shareId = intent.getLongExtra("SHARE_ID", 0L);

        // TODO passare share

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                new IntentFilter(MessagingService.SHARE_JOIN_REQUEST)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }

    @OnClick(R.id.qr_code_button)
    public void onShareCodeButtonClicked() {
        MaterialDialog qrCodeDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_share_qr_code, false)
                .build();

        ImageView qrCodeImage = (ImageView) qrCodeDialog.findViewById(R.id.qr_code_image_view);
        FrameLayout loadingView = (FrameLayout) qrCodeDialog.findViewById(R.id.loading_view);

        loadingView.setVisibility(View.VISIBLE);
        qrCodeDialog.show();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

                if (location == null) {
                    qrCodeDialog.hide();
                    Toasty.error(OngoingShareActivity.this, "Impossibile ottenere QR Code al momento").show();
                    return;
                }

                String latlngString = location.getLatitude() + "," + location.getLongitude();

                App.home2WorkClient.createShare(share -> {
                    try {
                        Bitmap bitmap = QREncoder.EncodeText(share.getId() + "," + latlngString);
                        qrCodeImage.setImageBitmap(bitmap);
                        loadingView.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        qrCodeDialog.hide();
                        Toasty.error(OngoingShareActivity.this, "Impossibile ottenere QR Code al momento").show();
                    }
                }, e -> {
                    qrCodeDialog.hide();
                    Toasty.error(OngoingShareActivity.this, "Impossibile avviare la condivisione al momento").show();
                });
            });

        }

    }

    private void initUI() {
        loadingView.setVisibility(View.GONE);


        if (share.getGuests() != null || share.getGuests().size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            guestsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            guestsRecyclerView.setVisibility(View.VISIBLE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation);

            guestsRecyclerView.setLayoutManager(layoutManager);
            guestsRecyclerView.setLayoutAnimation(animation);

/*            achievementAdapter = new AchievementAdapter(getActivity(), ProfileFragment.AchievementList);
            achievementAdapter.setItemClickCallbacks(this);
            achievementRecyclerView.setAdapter(achievementAdapter);*/
        }

    }
}
