package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.Converters;
import it.gruppoinfor.home2work.utils.RouteUtils;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.Mockup;
import it.gruppoinfor.home2workapi.enums.BookingStatus;
import it.gruppoinfor.home2workapi.model.BookingInfo;

import static it.gruppoinfor.home2work.utils.Converters.dateToString;

public class RequestActivity extends AppCompatActivity {

    public static final int REQUEST_RESPONSE_CODE = 433;
    public static final int REQUEST_ACCEPTED = 3;
    public static final int REQUEST_REJECTED = 4;
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
    @BindView(R.id.meeting_location_and_time)
    TextView meetingLocationAndTime;
    @BindView(R.id.user_avatar)
    CircleImageView userAvatar;
    @BindView(R.id.name_view)
    TextView nameView;
    @BindView(R.id.distance_view)
    TextView distanceView;
    @BindView(R.id.arrival_time_view)
    TextView arrivalTimeView;
    @BindView(R.id.departure_time_view)
    TextView departureTimeView;

    private Long bookingId;
    private int position;
    private BookingInfo booking;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        position = getIntent().getIntExtra("request_position", 0);
        bookingId = Client.getUserRequests().get(position).getBookingID();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.onCreate(savedInstanceState);

        // TODO ottenere info bookign dal srver
        Mockup.getRequestInfo(bookingId, bookingInfo -> {
            booking = bookingInfo;
            initUI();
            mapFragment.getMapAsync(new MyMapReadyCollback(this));
        });

        requestLoadingView.setVisibility(View.VISIBLE);

    }

    private void initUI() {
        Resources res = getResources();


        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            startShareButton.setVisibility(View.VISIBLE);
            acceptRequestButton.setVisibility(View.GONE);
            rejectRequestButton.setVisibility(View.GONE);
        } else {
            startShareButton.setVisibility(View.GONE);
            acceptRequestButton.setVisibility(View.VISIBLE);
            rejectRequestButton.setVisibility(View.VISIBLE);
        }

        Converters.latLngToAddress(this, booking.getMatchInfo().getStartLocation(), address -> {
            String location = address.getAddressLine(0);
            String time = "8:00"; // TODO orario di incontro

            meetingLocationAndTime.setText(
                    Html.fromHtml(
                            "In <b>" + location + "</b> alle <b>" + time + "</b>"
                    )
            );
        });

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar_placeholder).dontAnimate();

        Glide.with(this)
                .load(booking.getBookedMatch().getGuest().getAvatarURL())
                .apply(requestOptions)
                .into(userAvatar);

        nameView.setText(booking.getBookedMatch().getGuest().toString());

        distanceView.setText(String.format(res.getString(R.string.match_item_shared_distance), booking.getBookedMatch().getSharedDistance().toString()));
        arrivalTimeView.setText(String.format(res.getString(R.string.match_item_arrival_time), dateToString(booking.getBookedMatch().getArrivalTime())));
        departureTimeView.setText(String.format(res.getString(R.string.match_item_departure_time), dateToString(booking.getBookedMatch().getDepartureTime())));

    }


    @OnClick(R.id.reject_request_button)
    public void onRejectRequestButtonClicked() {
        MaterialDialog hideDialog = new MaterialDialog.Builder(this)
                .title("Rifiuta prenotazione")
                .content("Sei sicuro di voler rifiuatare questa prenotazione?")
                .positiveText("Rifiuta")
                .negativeText("Annulla")
                .onPositive((dialog, which) -> {
                    // TODO risposta al server
                    Client.getUserRequests().remove(position);
                    setResult(REQUEST_REJECTED);
                    finish();
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
                    // TODO risposta al server
                    Client.getUserRequests().get(position).setBookingStatus(BookingStatus.CONFIRMED);
                    setResult(REQUEST_ACCEPTED);

                    startShareButton.setVisibility(View.VISIBLE);
                    acceptRequestButton.setVisibility(View.GONE);
                    rejectRequestButton.setVisibility(View.GONE);
                })
                .build();

        hideDialog.show();
    }

    @OnClick(R.id.start_share_button)
    public void onStartShareButtonClicked() {
    }

    private class MyMapReadyCollback implements OnMapReadyCallback {

        Context context;

        MyMapReadyCollback(Context context) {
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
                matchWaypoints.add(booking.getMatchInfo().getStartLocation());
                matchWaypoints.add(booking.getMatchInfo().getEndLocation());

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

            LatLng first = booking.getMatchInfo().getStartLocation();
            LatLng last = booking.getMatchInfo().getEndLocation();

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
            requestLoadingView.setVisibility(View.GONE);

        }


        @Override
        public void onRoutingCancelled() {

        }
    }
}
