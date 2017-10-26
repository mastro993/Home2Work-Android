package it.gruppoinfor.home2work.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.models.MatchInfo;
import it.gruppoinfor.home2work.utils.Converters;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.RouteUtils;
import it.gruppoinfor.home2work.models.RoutePoint;

public class MatchActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private static final String GOOGLE_API_KEY = "AIzaSyCh8NUxxBR-ayyEq_EGFUU1JFVVFVwUq-I";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    ConstraintLayout container;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.request_shere_button)
    Button shareRequestButton;
    @BindView(R.id.infoText)
    TextView infoText;
    @BindView(R.id.request_loading_view)
    AVLoadingIndicatorView loadingView;

    GoogleMap googleMap;
    Long matchId;
    MatchInfo match;
    SupportMapFragment mapFragment;

    private boolean requesting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        matchId = getIntent().getLongExtra("matchID", 0L);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.onCreate(savedInstanceState);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            MapsInitializer.initialize(this);

            ArrayList<LatLng> matchWaypoints = RouteUtils.getWayPoints(match.getRoute(), 10);

            final Routing matchRouting = new Routing.Builder()
                    .travelMode(Routing.TravelMode.WALKING)
                    .withListener(this)
                    .waypoints(matchWaypoints)
                    .key(GOOGLE_API_KEY)
                    .build();

            matchRouting.execute();

        }

    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Toasty.error(this, "Non è al momento possibile recuperare le informazioni del percorso. Riprova più tardi").show();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
        com.directions.route.Route route = arrayList.get(0);

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.width(12 + i * 3);
        polyOptions.color(ContextCompat.getColor(this, R.color.red_500));
        polyOptions.addAll(route.getPoints());

        googleMap.addPolyline(polyOptions);

        final LatLngBounds latLngBounds = RouteUtils.getRouteBounds(route.getPoints());

        List<RoutePoint> matchedRoute = match.getRoute();
        RoutePoint start = matchedRoute.get(0);
        RoutePoint finish = matchedRoute.get(matchedRoute.size() - 1);

        LatLng first = start.getLatLng();
        LatLng last = finish.getLatLng();

        final Marker startMarker = googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_start))
                .position(first)
                .title("Partenza")
                .snippet(getString(R.string.match_start_time, Converters.timestampToTime(start.getTime(), "hh:mm")))
        );
        startMarker.showInfoWindow();


        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_end))
                .position(last)
                .title("Arrivo")
                .snippet(getString(R.string.match_end_time, Converters.timestampToTime(finish.getTime(), "hh:mm")))
        );

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {


            }
        });

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(startMarker.getPosition()));
        progressBar.setVisibility(View.GONE);

    }


    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onBackPressed() {
        if (requesting) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);
            builder.setTitle(R.string.match_abort_request);
            builder.setMessage(R.string.match_abort_request_message);
            builder.setPositiveButton("Annulla richiesta", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MatchActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("Continua", null);
            builder.show();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (requesting) {
            Bundle bundle = intent.getExtras();
            Boolean response = bundle.getBoolean("shareResponse");
            if (response) {
                loadingView.setVisibility(View.GONE);
                infoText.setText(getString(R.string.match_share_request_accepted));
            } else {
                loadingView.setVisibility(View.GONE);
                infoText.setText(getString(R.string.match_share_request_refused));
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        }
    }

    @OnClick(R.id.request_shere_button)
    void requestShare() {

        shareRequestButton.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        infoText.setText(getString(R.string.match_confirmation_awaiting));

        /*Client.getAPI().requestShare(match.getId()).enqueue(new Callback<ShareRequest>() {
            @Override
            public void onResponse(Call<ShareRequest> call, Response<ShareRequest> response) {
                requesting = true;
            }

            @Override
            public void onFailure(Call<ShareRequest> call, Throwable t) {
                infoText.setText(getString(R.string.match_share_request_error));
                shareRequestButton.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.GONE);
            }
        });*/

    }

}
