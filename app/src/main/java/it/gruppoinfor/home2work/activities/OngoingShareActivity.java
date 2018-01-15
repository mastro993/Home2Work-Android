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
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2work.adapters.ShareGuestsAdapter;
import it.gruppoinfor.home2work.custom.AvatarView;
import it.gruppoinfor.home2work.services.MessagingService;
import it.gruppoinfor.home2work.utils.QREncoder;
import it.gruppoinfor.home2workapi.model.Share;
import it.gruppoinfor.home2workapi.model.ShareGuest;

public class OngoingShareActivity extends AppCompatActivity implements ItemClickCallbacks {

    @BindView(R.id.layout_show_code)
    LinearLayout layoutShowCode;
    @BindView(R.id.guests_recycler_view)
    RecyclerView guestsRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.button_complete_share)
    Button buttonCompleteShare;
    @BindView(R.id.button_cancel_share)
    TextView buttonDeleteShare;
    @BindView(R.id.host_layout)
    LinearLayout hostLayout;
    @BindView(R.id.guest_layout)
    LinearLayout guestLayout;
    @BindView(R.id.ongoing_share_layout)
    RelativeLayout ongoingShareLayout;
    @BindView(R.id.avatar_view)
    AvatarView avatarView;
    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.job_text_view)
    TextView jobTextView;
    @BindView(R.id.text_share_distance)
    TextView textShareDistance;
    @BindView(R.id.text_share_xp)
    TextView textShareXp;
    @BindView(R.id.buttons_layout)
    LinearLayout buttonsLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Share mShare;
    private ShareGuestsAdapter mShareGuestsAdapter;
    private MaterialDialog qrCodeDialog;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            qrCodeDialog.dismiss();
            refreshGuests();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_share);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Intent intent = getIntent();
        mShare = (Share) intent.getSerializableExtra("SHARE");

        if (mShare.getType() == Share.Type.DRIVER)
            initHostUI();
        else {
            initGuestUI();
        }

        // TODO informazioni condivisione: tempo trascorso, chiloemtri percorsi, ecc..

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

    @Override
    public void onItemClick(View view, int position) {
        String[] options = new String[]{"Mostra profilo", "Espelli"};

        new MaterialDialog.Builder(this)
                .items(options)
                .itemsCallback((dialog, itemView, p, text) -> {
                    switch (p) {
                        case 0:
                            // TODO mostrare profilo
                            break;
                        case 1:
                            App.home2WorkClient.expelGuest(mShare.getId(), mShare.getGuests().get(position).getGuest().getId(), share -> {
                                mShare = share;
                                initHostUI();
                            }, Throwable::printStackTrace);
                            break;
                        case 2:

                            break;
                    }
                })
                .show();
    }

    @Override
    public boolean onLongItemClick(View view, int position) {
        return false;
    }

    @OnClick(R.id.layout_show_code)
    public void onShareCodeButtonClicked() {
        qrCodeDialog = new MaterialDialog.Builder(this)
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

    @OnClick(R.id.button_cancel_share)
    public void onDeleteButtonClick() {
        switch (mShare.getType()) {
            case DRIVER:
                new MaterialDialog.Builder(this)
                        .title("Annulla condivisione")
                        .content("Sei sicuro di voler annullare la condivisione corrente? Tutti i progressi verranno persi")
                        .onPositive((dialog, which) -> App.home2WorkClient.cancelShare(mShare.getId(), responseBody -> finish(), Throwable::printStackTrace))
                        .positiveText("Conferma annullamento")
                        .negativeText("Indietro")
                        .show();

                break;
            case GUEST:
                new MaterialDialog.Builder(this)
                        .title("Annulla condivisione")
                        .content("Sei sicuro di voler annullare la condivisione corrente? Tutti i progressi verranno persi")
                        .onPositive((dialog, which) -> App.home2WorkClient.leaveShare(mShare.getId(), responseBody -> finish(), Throwable::printStackTrace))
                        .positiveText("Conferma annullamento")
                        .negativeText("Indietro")
                        .show();
                break;

        }
    }

    @OnClick(R.id.button_complete_share)
    public void onCompleteButtonClick() {

        switch (mShare.getType()) {
            case DRIVER:
                if (mShare.getGuests().size() == 0) {
                    new MaterialDialog.Builder(this)
                            .title("Impossibile completare")
                            .content("Non puoi segnalare come completata una condivisione senza passeggeri")
                            .show();
                }

                for (ShareGuest shareGuest : mShare.getGuests()) {
                    if (shareGuest.getStatus() != ShareGuest.Status.COMPLETED) {
                        new MaterialDialog.Builder(this)
                                .title("Impossibile completare")
                                .content("Prima di poter completare la condivisione tutti i passeggeri devono convalidarla tramite il codice")
                                .show();
                    }
                }

                // Completa condivisione

                break;
            case GUEST:
                // Todo validare condivisione
                break;

        }


    }

    private void initHostUI() {

        hostLayout.setVisibility(View.VISIBLE);
        guestLayout.setVisibility(View.GONE);

        if (mShare.getGuests() != null && mShare.getGuests().size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            guestsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            guestsRecyclerView.setVisibility(View.VISIBLE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation);

            guestsRecyclerView.setLayoutManager(layoutManager);
            guestsRecyclerView.setLayoutAnimation(animation);

            mShareGuestsAdapter = new ShareGuestsAdapter(this, mShare.getGuests());
            mShareGuestsAdapter.setItemClickCallbacks(this);
            guestsRecyclerView.setAdapter(mShareGuestsAdapter);
        }

    }

    private void initGuestUI() {

        hostLayout.setVisibility(View.GONE);
        guestLayout.setVisibility(View.VISIBLE);

        nameTextView.setText(mShare.getHost().toString());
        jobTextView.setText(mShare.getHost().getCompany().toString());

        avatarView.setAvatarURL(mShare.getHost().getAvatarURL());
        avatarView.setExp(mShare.getHost().getExp());


    }

    private void refreshGuests() {

        App.home2WorkClient.getShare(mShare.getId(), share -> {
            mShare = share;
            initHostUI();
        }, Throwable::printStackTrace);

    }
}
