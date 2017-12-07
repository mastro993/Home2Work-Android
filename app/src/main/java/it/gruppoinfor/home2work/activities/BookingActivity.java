package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.github.lzyzsd.circleprogress.ArcProgress;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.custom.ArcProgressAnimation;
import it.gruppoinfor.home2work.utils.RouteUtils;
import it.gruppoinfor.home2work.utils.ScoreColorUtility;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Booking;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static it.gruppoinfor.home2work.utils.Converters.dateToString;

public class BookingActivity extends AppCompatActivity {

    private static final String GOOGLE_API_KEY = "AIzaSyCh8NUxxBR-ayyEq_EGFUU1JFVVFVwUq-I";

    public static final int BOOKING_REJECTED = 0;
    public static final int BOOKING_PENDING = 1;
    public static final int BOOKING_ACCEPTED = 2;
    public static final int BOOKING_CANCELED = 3;

    GoogleMap googleMap;
    Long bookingId;
    Booking booking;
    SupportMapFragment mapFragment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.score_progress)
    ArcProgress scoreProgress;
    @BindView(R.id.user_avatar)
    CircleImageView userAvatar;
    @BindView(R.id.score_text)
    TextView scoreText;
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

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        ArcProgressAnimation animation = new ArcProgressAnimation(scoreProgress, 0, booking.getBookedMatch().getScore());
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        scoreProgress.startAnimation(animation);

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar_placeholder).dontAnimate();

        Glide.with(this)
                .load(booking.getBookedMatch().getHost().getAvatarURL())
                .apply(requestOptions)
                .into(userAvatar);

        scoreText.setText(String.format(Locale.ITALY, "%1$d%%", booking.getBookedMatch().getScore()));

        nameView.setText(booking.getBookedMatch().getHost().toString());
        jobView.setText(booking.getBookedMatch().getHost().getCompany().toString());

        int color = ScoreColorUtility.getScoreColor(this, booking.getBookedMatch().getScore());
        Drawable bg = ContextCompat.getDrawable(this, R.drawable.bg_match_score_percent);

        scoreProgress.setFinishedStrokeColor(color);
        bg.setTint(color);
        scoreText.setBackground(bg);

        arrivalTimeView.setText(dateToString(booking.getBookedMatch().getStartTime()) + " - " + dateToString(booking.getBookedMatch().getEndTime()));

        ArrayList<String> days = new ArrayList<>();
        for (int day : booking.getBookedMatch().getWeekdays()) {
            days.add(getResources().getStringArray(R.array.giorni)[day]);
        }

        daysText.setText(TextUtils.join(", ", days));


        switch (booking.getBookingStatus()) {
            case BOOKING_ACCEPTED:
                startShareButton.setVisibility(View.VISIBLE);
                statusText.setVisibility(View.GONE);
                break;
            case BOOKING_PENDING:
                statusText.setText("La prenotazione è ancora in attesa di risposta");
                break;
            case BOOKING_REJECTED:
                statusText.setText("La prenotazione non è stata accettata");
                statusText.setTextColor(ContextCompat.getColor(this, R.color.red_500));
                break;
            case BOOKING_CANCELED:
                statusText.setText("La prenotazione è stata cancellata");
                statusText.setTextColor(ContextCompat.getColor(this, R.color.red_500));
                break;
        }


    }

    @OnClick(R.id.start_share_button)
    public void onViewClicked() {

        // TODO SCANNER CODICE PER CONDIVISIONE CON ZXING
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
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_start))
                            .position(first)
                            .title("Partenza")
                    //.snippet(getString(R.string.match_start_time, Converters.timestampToTime(start.getTime(), "hh:mm")))
            );
            startMarker.showInfoWindow();


            googleMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_end))
                            .position(last)
                            .title("Arrivo")
                    //.snippet(getString(R.string.match_end_time, Converters.timestampToTime(finish.getTime(), "hh:mm")))
            );

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(startMarker.getPosition()));
            bookingLoadingView.setVisibility(View.GONE);

        }


        @Override
        public void onRoutingCancelled() {

        }
    }

}
