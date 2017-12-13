package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.services.MessagingService;
import it.gruppoinfor.home2work.utils.GeofenceUtils;
import it.gruppoinfor.home2work.utils.QREncoder;
import it.gruppoinfor.home2work.utils.RouteUtils;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Booking;
import it.gruppoinfor.home2workapi.model.Share;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestActivity extends AppCompatActivity {

    public static final int REQUEST_RESPONSE_CODE = 433;
    public static final int REQUEST_ACCEPTED = 23;
    public static final int REQUEST_REJECTED = 24;
    private static final String GOOGLE_API_KEY = "AIzaSyCh8NUxxBR-ayyEq_EGFUU1JFVVFVwUq-I";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.reject_request_button)
    Button rejectRequestButton;
    @BindView(R.id.accept_request_button)
    Button acceptRequestButton;
    @BindView(R.id.start_share_button)
    Button startShareButton;
    @BindView(R.id.request_loading_view)
    FrameLayout requestLoadingView;
    @BindView(R.id.user_avatar)
    CircleImageView userAvatar;
    @BindView(R.id.name_view)
    TextView nameView;
    @BindView(R.id.job_view)
    TextView jobView;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.linearLayout3)
    LinearLayout linearLayout3;
    @BindView(R.id.exp_preview)
    TextView expPreview;
    @BindView(R.id.karma_preview)
    TextView karmaPreview;
    @BindView(R.id.boooking_date_view)
    TextView boookingDateView;

    private Long bookingId;
    private int position;
    private Booking booking;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private MaterialDialog qrCodeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        ButterKnife.bind(this);

        setTitle("Dettagli richiesta");

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getLongExtra("bookingID", 0) != 0) {
            bookingId = getIntent().getLongExtra("bookingID", 0);
        } else {
            position = getIntent().getIntExtra("request_position", 0);
            bookingId = Client.getUserRequests().get(position).getBookingID();
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.onCreate(savedInstanceState);

        requestLoadingView.setVisibility(View.VISIBLE);

        Client.getAPI().getBooking(bookingId).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                booking = response.body();
                initUI();
                mapFragment.getMapAsync(new MyMapReadyCallback(RequestActivity.this));
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {

            }
        });

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            qrCodeDialog.hide();
            RequestActivity.this.setResult(BookingActivity.SHARE_STARTED);
            finish();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver), new IntentFilter("REQUEST_SHARE_START")
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }

    private void initUI() {
        Resources res = getResources();

        switch (booking.getBookingStatus()) {
            case Booking.PENDING:
                startShareButton.setVisibility(View.GONE);
                acceptRequestButton.setVisibility(View.VISIBLE);
                rejectRequestButton.setVisibility(View.VISIBLE);
                break;
            case Booking.ACCEPTED:
                initSharingView();
                break;
            case Booking.CANCELED:
                break;
        }

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar_placeholder).dontAnimate();

        Glide.with(this)
                .load(booking.getBookedMatch().getGuest().getAvatarURL())
                .apply(requestOptions)
                .into(userAvatar);

        nameView.setText(booking.getBookedMatch().getGuest().toString());
        jobView.setText(booking.getBookedMatch().getGuest().getCompany().toString());

        Double kmDistance = booking.getBookedMatch().getDistance() / 1000.0;

        int karmaPoints = (int) (kmDistance.intValue() * 1.2);
        int exp = (int) (kmDistance * 12);
        karmaPreview.setText(String.format(res.getString(R.string.match_karma_preview), karmaPoints));
        expPreview.setText(String.format(res.getString(R.string.match_exo_preview), exp));

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ITALIAN);
        String dateString = dateFormat.format(booking.getBookedDate());
        boookingDateView.setText(dateString);

    }

    private void initSharingView() {

        startShareButton.setVisibility(View.VISIBLE);
        acceptRequestButton.setVisibility(View.GONE);
        rejectRequestButton.setVisibility(View.GONE);

        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(booking.getBookedDate().getTime());

        if (Calendar.getInstance().get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            startShareButton.setEnabled(true);
        } else {
            startShareButton.setEnabled(false);
        }

        // TODO rimuovere per debug
        startShareButton.setEnabled(true);

    }

    @OnClick(R.id.reject_request_button)
    public void onRejectRequestButtonClicked() {
        MaterialDialog hideDialog = new MaterialDialog.Builder(this)
                .title("Rifiuta prenotazione")
                .content("Sei sicuro di voler rifiuatare questa prenotazione?")
                .positiveText("Rifiuta")
                .negativeText("Annulla")
                .onPositive((dialog, which) -> {

                    booking.setBookingStatus(Booking.REJECTED);

                    Client.getAPI().editBooking(booking).enqueue(new Callback<Booking>() {
                        @Override
                        public void onResponse(Call<Booking> call, Response<Booking> response) {
                            Client.getUserRequests().remove(position);
                            setResult(REQUEST_REJECTED);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Booking> call, Throwable t) {

                        }
                    });

                })
                .build();

        hideDialog.show();
    }

    @OnClick(R.id.accept_request_button)
    public void onAcceptRequestButtonClicked() {
        MaterialDialog hideDialog = new MaterialDialog.Builder(this)
                .title("Accetta prenotazione")
                .content("Accettando la prenotazione ti impegnerai a rispettare l'accordo preso con l'utente")
                .positiveText("Accetta")
                .negativeText("Annulla")
                .onPositive((dialog, which) -> {

                    booking.setBookingStatus(Booking.ACCEPTED);

                    Client.getAPI().editBooking(booking).enqueue(new Callback<Booking>() {
                        @Override
                        public void onResponse(Call<Booking> call, Response<Booking> response) {

                            Client.getUserRequests().get(position).setBookingStatus(2);
                            setResult(REQUEST_ACCEPTED);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Booking> call, Throwable t) {

                        }
                    });

                })
                .build();

        hideDialog.show();
    }

    @OnClick(R.id.start_share_button)
    public void onStartShareButtonClicked() {

        qrCodeDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_share_qr_code, false)
                .build();

        ImageView qrCodeImage = (ImageView) qrCodeDialog.findViewById(R.id.qr_code_image_view);
        FrameLayout loadingView = (FrameLayout) qrCodeDialog.findViewById(R.id.loading_view);

        loadingView.setVisibility(View.VISIBLE);
        qrCodeDialog.show();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

                if (location == null) {
                    qrCodeDialog.hide();
                    Toasty.error(RequestActivity.this, "Impossibile ottenere QR Code al momento").show();
                    return;
                }

                String latlngString = location.getLatitude() + "," + location.getLongitude();

                Client.getAPI().newShare(booking.getBookingID(), latlngString).enqueue(new Callback<Share>() {
                    @Override
                    public void onResponse(Call<Share> call, Response<Share> response) {
                        try {
                            Bitmap bitmap = QREncoder.EncodeText(response.body().getShareID().toString());
                            qrCodeImage.setImageBitmap(bitmap);
                            loadingView.setVisibility(View.INVISIBLE);
                        } catch (Exception e) {
                            qrCodeDialog.hide();
                            Toasty.error(RequestActivity.this, "Impossibile ottenere QR Code al momento").show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Share> call, Throwable t) {
                        qrCodeDialog.hide();
                        Toasty.error(RequestActivity.this, "Impossibile avviare la condivisione al momento").show();
                    }

                });

            });

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private class MyMapReadyCallback implements OnMapReadyCallback {

        Context context;

        MyMapReadyCallback(Context context) {
            this.context = context;
        }


        @Override
        public void onMapReady(final GoogleMap gmap) {
            googleMap = gmap;
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                MapsInitializer.initialize(context);

                // TODO migliorare percorso con punti intermedi
                // ArrayList<LatLng> matchWaypoints = RouteUtils.getWayPoints(match.getRoute(), 10);


                List<LatLng> matchWaypoints = new ArrayList<>();
                matchWaypoints.add(booking.getBookedMatch().getStartLocation());
                matchWaypoints.add(booking.getBookedMatch().getEndLocation());

                final Routing matchRouting = new Routing.Builder()
                        .travelMode(Routing.TravelMode.WALKING)
                        .withListener(new MyRoutingListener(context))
                        .waypoints(matchWaypoints)
                        .key(GOOGLE_API_KEY)
                        .build();

                matchRouting.execute();

            }

        }
    }

    private class MyRoutingListener implements RoutingListener {

        Context context;

        MyRoutingListener(Context context) {
            this.context = context;
        }

        @Override
        public void onRoutingFailure(RouteException e) {
            Toasty.error(context, "Non è al momento possibile recuperare le informazioni del percorso. Riprova più tardi").show();
            finish();
        }

        @Override
        public void onRoutingStart() {

        }

        @Override
        public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
            Route route = arrayList.get(0);

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.width(12 + i * 3);
            polyOptions.color(ContextCompat.getColor(context, R.color.red_500));
            polyOptions.addAll(route.getPoints());

            googleMap.addPolyline(polyOptions);

            final LatLngBounds latLngBounds = RouteUtils.getRouteBounds(route.getPoints());

            LatLng first = booking.getBookedMatch().getStartLocation();
            LatLng last = booking.getBookedMatch().getEndLocation();

            final Marker startMarker = googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(first)
                    .title("Casa")
            );
            startMarker.showInfoWindow();


            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(last)
                    .title("Lavoro")
            );

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(startMarker.getPosition()));


            requestLoadingView.setVisibility(View.GONE);

        }


        @Override
        public void onRoutingCancelled() {

        }
    }
}
