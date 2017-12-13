package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.custom.MatchAvatarView;
import it.gruppoinfor.home2work.utils.GeofenceUtils;
import it.gruppoinfor.home2work.utils.RouteUtils;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Booking;
import it.gruppoinfor.home2workapi.model.Share;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static it.gruppoinfor.home2work.utils.Converters.dateToString;

public class BookingActivity extends AppCompatActivity {

    private static final String GOOGLE_API_KEY = "AIzaSyCh8NUxxBR-ayyEq_EGFUU1JFVVFVwUq-I";

    public static final int SHARE_STARTED = 77;

    GoogleMap googleMap;
    Long bookingId;
    Booking booking;
    SupportMapFragment mapFragment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.match_avatar_view)
    MatchAvatarView matchAvatarView;
    @BindView(R.id.name_view)
    TextView nameView;
    @BindView(R.id.job_view)
    TextView jobView;
    @BindView(R.id.time_view)
    TextView arrivalTimeView;
    @BindView(R.id.days_view)
    TextView daysText;
    @BindView(R.id.booking_loading_view)
    FrameLayout bookingLoadingView;
    @BindView(R.id.start_share_button)
    Button startShareButton;
    @BindView(R.id.status_text)
    TextView statusText;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.linearLayout3)
    LinearLayout linearLayout3;
    @BindView(R.id.boooking_date_view)
    TextView boookingDateView;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        bookingId = getIntent().getLongExtra("bookingID", 0L);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.onCreate(savedInstanceState);

        bookingLoadingView.setVisibility(View.VISIBLE);


        Client.getAPI().getBooking(bookingId).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                booking = response.body();
                initUI();
                mapFragment.getMapAsync(new MyMapReadyCallback(BookingActivity.this));
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {

            }
        });

    }


    private void initUI() {

        matchAvatarView.setAvatarURL(booking.getBookedMatch().getHost().getAvatarURL());
        matchAvatarView.setScore(booking.getBookedMatch().getScore());

        nameView.setText(booking.getBookedMatch().getHost().toString());
        jobView.setText(booking.getBookedMatch().getHost().getCompany().toString());

        arrivalTimeView.setText(
                String.format(
                        getResources().getString(R.string.match_time),
                        dateToString(booking.getBookedMatch().getStartTime()),
                        dateToString(booking.getBookedMatch().getEndTime())
                )
        );

        ArrayList<String> days = new ArrayList<>();
        for (int d : booking.getBookedMatch().getWeekdays())
            days.add(getResources().getStringArray(R.array.giorni)[d]);
        daysText.setText(TextUtils.join(", ", days));

        switch (booking.getBookingStatus()) {
            case Booking.ACCEPTED:
                initSharingView();
                break;
            case Booking.PENDING:
                statusText.setText("La prenotazione è ancora in attesa di risposta");
                break;
            case Booking.REJECTED:
                statusText.setText("La prenotazione non è stata accettata");
                statusText.setTextColor(ContextCompat.getColor(this, R.color.red_500));
                break;
            case Booking.CANCELED:
                statusText.setText("La prenotazione è stata cancellata");
                statusText.setTextColor(ContextCompat.getColor(this, R.color.red_500));
                break;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ITALIAN);
        String dateString = dateFormat.format(booking.getBookedDate());
        boookingDateView.setText(dateString);


    }

    private void initSharingView() {

        startShareButton.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.GONE);

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

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;

    @OnClick(R.id.start_share_button)
    public void startShare() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            IntentIntegrator intentIntegrator = new IntentIntegrator(this);
            intentIntegrator.initiateScan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            startShare();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            validateShare(Long.parseLong(scanResult.getContents()));
        } else
            Toasty.error(this, "QR Code non valido");
    }

    private void validateShare(long shareId) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                String latlngString = location.getLatitude() + "," + location.getLongitude();
                Client.getAPI().startShare(shareId, Client.getSignedUser().getId(), latlngString).enqueue(new Callback<Share>() {
                    @Override
                    public void onResponse(Call<Share> call, Response<Share> response) {
                        switch (response.code()) {
                            case 200:
                                setResult(BookingActivity.SHARE_STARTED);
                                Share share = response.body();
                                GeofenceUtils.setupGeofence(
                                        BookingActivity.this,
                                        share.getShareID().toString(),
                                        share.getBooking().getBookedMatch().getEndLocation()
                                );
                                finish();
                                break;
                            case 404:
                                Toasty.error(BookingActivity.this, "Codice non valido").show();
                                break;
                            case 403:
                                Toasty.error(BookingActivity.this, "Non puoi avviare questa condivisione al momento").show();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Share> call, Throwable t) {
                        Toasty.error(BookingActivity.this, "Non puoi avviare questa condivisione al momento").show();
                    }
                });
            });
        }


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

            bookingLoadingView.setVisibility(View.GONE);

        }


        @Override
        public void onRoutingCancelled() {

        }
    }

}
