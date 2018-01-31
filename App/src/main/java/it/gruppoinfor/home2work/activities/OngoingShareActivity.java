package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.adapters.ShareGuestsAdapter;
import it.gruppoinfor.home2work.custom.AvatarView;
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks;
import it.gruppoinfor.home2work.services.MessagingService;
import it.gruppoinfor.home2work.utils.QREncoder;
import it.gruppoinfor.home2work.utils.Tools;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.model.Guest;
import it.gruppoinfor.home2workapi.model.Share;
import it.gruppoinfor.home2workapi.model.User;

public class OngoingShareActivity extends AppCompatActivity implements ItemClickCallbacks {

    public static final String EXTRA_SHARE = "share";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    @BindView(R.id.layout_show_code)
    LinearLayout layoutShowCode;
    @BindView(R.id.guests_recycler_view)
    RecyclerView guestsRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.button_complete_share)
    Button buttonCompleteShare;
    @BindView(R.id.host_layout)
    View hostLayout;
    @BindView(R.id.guest_layout)
    View guestLayout;
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
    @BindView(R.id.layout_host)
    LinearLayout layoutHost;
    @BindView(R.id.header_view)
    TextView headerView;

    private Share mShare;
    // private Guest mUserShareGuest;
    @Nullable
    private MaterialDialog qrCodeDialog;
    private MaterialDialog loadingDialog;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (qrCodeDialog != null) qrCodeDialog.dismiss();
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
        }

        Intent intent = getIntent();
        mShare = (Share) intent.getSerializableExtra(EXTRA_SHARE);

        if (mShare.getType() == Share.Type.DRIVER)
            initHostUI();
        else {
            //Stream<Guest> shareGuestStream = Stream.of(mShare.getGuests());
            //mUserShareGuest = shareGuestStream.filter(value -> value.getGuest().equals(HomeToWorkClient.getUser())).findFirst().get();
            initGuestUI();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ongoing_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_cancel_share:
                switch (mShare.getType()) {
                    case DRIVER:
                        new MaterialDialog.Builder(this)
                                .title("Annulla condivisione")
                                .content("Sei sicuro di voler annullare la condivisione corrente? Tutti i progressi verranno persi")
                                .onPositive((dialog, which) -> {
                                    MaterialDialog materialDialog = new MaterialDialog.Builder(OngoingShareActivity.this)
                                            .content(R.string.activity_ongoing_share_cancel_dialog_content)
                                            .contentGravity(GravityEnum.CENTER)
                                            .progress(true, 150, true)
                                            .show();

                                    HomeToWorkClient.getInstance().cancelShare(mShare, responseBody -> {
                                        materialDialog.dismiss();
                                        finish();
                                    }, e -> {
                                        Toasty.error(OngoingShareActivity.this, getString(R.string.activity_signin_server_error)).show();
                                        materialDialog.dismiss();
                                        e.printStackTrace();
                                    });
                                })
                                .positiveText("Conferma")
                                .negativeText("Indietro")
                                .show();

                        break;
                    case GUEST:
                        new MaterialDialog.Builder(this)
                                .title("Annulla condivisione")
                                .content("Sei sicuro di voler annullare la condivisione corrente? Tutti i progressi verranno persi")
                                .onPositive((dialog, which) -> {
                                    MaterialDialog materialDialog = new MaterialDialog.Builder(OngoingShareActivity.this)
                                            .content(R.string.activity_ongoing_share_leave_dialog_content)
                                            .contentGravity(GravityEnum.CENTER)
                                            .progress(true, 150, true)
                                            .show();


                                    HomeToWorkClient.getInstance().leaveShare(mShare, responseBody -> {
                                        materialDialog.dismiss();
                                        finish();
                                    }, e -> {
                                        Toasty.error(OngoingShareActivity.this, getString(R.string.activity_signin_server_error)).show();
                                        materialDialog.dismiss();
                                        e.printStackTrace();
                                    });
                                })
                                .positiveText("Conferma")
                                .negativeText("Indietro")
                                .show();
                        break;
                }
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
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                new IntentFilter(MessagingService.SHARE_COMPLETE_REQUEST)
        );
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                new IntentFilter(MessagingService.SHARE_DETACH_REQUEST)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }

    @Override
    public void onItemClick(View view, int position) {
        String[] options;

        if (mShare.getGuests().get(position).getStatus().equals(Guest.Status.CANCELED)) {
            options = getResources().getStringArray(R.array.activity_ongoing_share_user_options_leaved);
        } else {
            options = getResources().getStringArray(R.array.activity_ongoing_share_user_options);
        }

        new MaterialDialog.Builder(this)
                .items(options)
                .itemsCallback((dialog, itemView, p, text) -> {
                    switch (p) {
                        case 0:
                            Intent userIntent = new Intent(this, ShowUserActivity.class);
                            userIntent.putExtra("user",  mShare.getGuests().get(position));
                            startActivity(userIntent);
                            break;
                        case 1:
                            HomeToWorkClient.getInstance().expelGuest(mShare, mShare.getGuests().get(position), share -> {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String[] stringData = scanResult.getContents().split(",");
            Long shareId = Long.parseLong(stringData[0]);
            LatLng latLng = new LatLng(Double.parseDouble(stringData[1]), Double.parseDouble(stringData[2]));
            checkShareCode(shareId, latLng);
        } else
            Toasty.error(this, getString(R.string.activity_ongoing_share_invalid_code));
    }

    @OnClick(R.id.layout_show_code)
    public void onShareCodeButtonClicked() {
        qrCodeDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.activity_ongoing_dialog_share_code_title))
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
                    Toasty.error(OngoingShareActivity.this, getString(R.string.activity_ongoing_share_code_unavailable)).show();
                    return;
                }

                String latlngString = location.getLatitude() + "," + location.getLongitude();

                HomeToWorkClient.getInstance().createShare(share -> {
                    try {
                        Bitmap bitmap = QREncoder.EncodeText(share.getId() + "," + latlngString);
                        qrCodeImage.setImageBitmap(bitmap);
                        loadingView.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        qrCodeDialog.hide();
                        Toasty.error(OngoingShareActivity.this, getString(R.string.activity_ongoing_share_code_unavailable)).show();
                    }
                }, e -> {
                    qrCodeDialog.hide();
                    Toasty.error(OngoingShareActivity.this, getString(R.string.activity_ongoing_share_code_unavailable)).show();
                });
            });

        }

    }

    @OnClick(R.id.button_complete_share)
    public void onCompleteButtonClick() {

        switch (mShare.getType()) {
            case DRIVER:
                if (mShare.getGuests().size() == 0) {
                    new MaterialDialog.Builder(this)
                            .title(R.string.activity_ongoing_share_dialog_completition_error_title)
                            .content(R.string.activity_ongoing_share_dialog_completition_error_content)
                            .show();
                    break;
                }

                loadingDialog = new MaterialDialog.Builder(this)
                        .content(R.string.activity_ongoing_share_dialog_completition)
                        .contentGravity(GravityEnum.CENTER)
                        .progress(true, 150, true)
                        .show();

                HomeToWorkClient.getInstance().finishShare(mShare, share -> {
                    loadingDialog.dismiss();
                    Toasty.success(OngoingShareActivity.this, getString(R.string.activity_ongoing_share_dialog_completition_success)).show();
                    finish();
                }, e -> {
                    loadingDialog.dismiss();
                    Toasty.error(OngoingShareActivity.this, getString(R.string.activity_ongoing_share_dialog_completition_error)).show();
                    e.printStackTrace();
                });

                break;
            case GUEST:

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                    intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    intentIntegrator.setPrompt(getString(R.string.activity_ongoing_share_dialog_qr_prompt));
                    intentIntegrator.setOrientationLocked(false);
                    intentIntegrator.initiateScan();
                }

                break;

        }


    }

    @OnClick(R.id.layout_host)
    public void onHostLayoutClick() {
        Intent userIntent = new Intent(this, ShowUserActivity.class);
        userIntent.putExtra("user", mShare.getHost());
        startActivity(userIntent);
    }

    private boolean enableCompleteButton() {
        if (mShare.getGuests().size() == 0) return false;

        Stream<Guest> shareGuestStream = Stream.of(mShare.getGuests());

        Optional<Guest> shareGuestOptional = shareGuestStream.filter(value -> value.getStatus().equals(Guest.Status.JOINED)).findFirst();

        return !shareGuestOptional.isPresent();

    }

    private void checkShareCode(Long shareID, LatLng hostLocation) {


        loadingDialog = new MaterialDialog.Builder(this)
                .content(R.string.activity_ongoing_share_check)
                .contentGravity(GravityEnum.CENTER)
                .progress(true, 150, true)
                .show();

        if (!mShare.getId().equals(shareID)) {
            Toasty.error(this, getString(R.string.activity_ongoing_share_check_wrong_code)).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(endLocation -> {

                if (endLocation == null) {
                    Toasty.error(this, getString(R.string.activity_ongoing_share_check_error)).show();
                    loadingDialog.dismiss();
                    return;
                }

                if (Tools.getDistance(endLocation, hostLocation) > 500) {
                    Toasty.error(this, getString(R.string.activity_ongoing_share_check_wrong_code)).show();
                    loadingDialog.dismiss();
                    return;
                }

                HomeToWorkClient.getInstance().completeShare(mShare, endLocation, share -> {
                    loadingDialog.dismiss();
                    finish();
                    Toasty.success(this, getString(R.string.activity_ongoing_share_check_success)).show();
                }, e -> {
                    loadingDialog.dismiss();
                    Toasty.error(OngoingShareActivity.this, getString(R.string.activity_ongoing_share_check_error)).show();
                    e.printStackTrace();
                });

            });

        }
    }

    private void initHostUI() {

        hostLayout.setVisibility(View.VISIBLE);
        guestLayout.setVisibility(View.GONE);

        if (mShare.getGuests() != null && mShare.getGuests().size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            headerView.setVisibility(View.GONE);
            guestsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            headerView.setVisibility(View.VISIBLE);
            guestsRecyclerView.setVisibility(View.VISIBLE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation);

            guestsRecyclerView.setLayoutManager(layoutManager);
            guestsRecyclerView.setLayoutAnimation(animation);

            ShareGuestsAdapter mShareGuestsAdapter = new ShareGuestsAdapter(this, mShare.getGuests());
            mShareGuestsAdapter.setItemClickCallbacks(this);
            guestsRecyclerView.setAdapter(mShareGuestsAdapter);
        }

        buttonCompleteShare.setEnabled(enableCompleteButton());

    }

    private void initGuestUI() {

        hostLayout.setVisibility(View.GONE);
        guestLayout.setVisibility(View.VISIBLE);

        nameTextView.setText(mShare.getHost().toString());
        jobTextView.setText(mShare.getHost().getCompany().toString());

        avatarView.setAvatarURL(mShare.getHost().getAvatarURL());
        avatarView.setExp(mShare.getHost().getExp());

        buttonCompleteShare.setEnabled(true);


    }

    private void refreshGuests() {

        HomeToWorkClient.getInstance().getShare(mShare.getId(), share -> {
            mShare = share;
            initHostUI();
        }, Throwable::printStackTrace);

    }
}
