package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.directions.route.AbstractRouting;
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
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.RouteUtils;
import it.gruppoinfor.home2workapi.Mockup;
import it.gruppoinfor.home2workapi.model.MatchInfo;

public class MatchActivity extends AppCompatActivity {

    private static final String GOOGLE_API_KEY = "AIzaSyCh8NUxxBR-ayyEq_EGFUU1JFVVFVwUq-I";


    GoogleMap googleMap;
    Long matchId;
    MatchInfo match;
    SupportMapFragment mapFragment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.infoText)
    TextView infoText;
    @BindView(R.id.request_shere_button)
    Button requestShereButton;
    @BindView(R.id.request_loading_view)
    AVLoadingIndicatorView requestLoadingView;
    @BindView(R.id.match_loading_view)
    FrameLayout matchLoadingView;

    private boolean requesting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        ButterKnife.bind(this);

        matchLoadingView.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        matchId = getIntent().getLongExtra("matchID", 0L);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.onCreate(savedInstanceState);

        Mockup.getMatchInfo(matchInfo -> {
            match = matchInfo;
            mapFragment.getMapAsync(new MyMapReadyCollback(MatchActivity.this));
        });

    }

    @Override
    public void onBackPressed() {
        if (requesting) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                requestLoadingView.setVisibility(View.GONE);
                infoText.setText(getString(R.string.match_share_request_accepted));
            } else {
                requestLoadingView.setVisibility(View.GONE);
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

        requestShereButton.setVisibility(View.GONE);
        requestLoadingView.setVisibility(View.VISIBLE);
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
                matchWaypoints.add(match.getStartLocation());
                matchWaypoints.add(match.getEndLocation());

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

            LatLng first = match.getStartLocation();
            LatLng last = match.getEndLocation();

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
            matchLoadingView.setVisibility(View.GONE);

        }


        @Override
        public void onRoutingCancelled() {

        }
    }

}
