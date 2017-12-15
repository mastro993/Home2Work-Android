package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.RouteUtils;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Match;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchActivity extends AppCompatActivity {

    private static final String GOOGLE_API_KEY = "AIzaSyCh8NUxxBR-ayyEq_EGFUU1JFVVFVwUq-I";
    @BindView(R.id.score_text)
    TextView scoreText;
    private GoogleMap googleMap;
    private Match match;

    SupportMapFragment mapFragment;
    @BindView(R.id.match_loading_view)
    FrameLayout matchLoadingView;
    @BindView(R.id.user_avatar)
    ImageView userAvatar;
    @BindView(R.id.name_view)
    TextView nameView;
    @BindView(R.id.job_view)
    TextView jobView;
    @BindView(R.id.profile_container)
    ConstraintLayout userProfileContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.onCreate(savedInstanceState);

        matchLoadingView.setVisibility(View.VISIBLE);

        Long matchId = getIntent().getLongExtra("matchID", 0L);
        Client.getAPI().getMatch(matchId).enqueue(new Callback<Match>() {
            @Override
            public void onResponse(Call<Match> call, Response<Match> response) {
                match = response.body();
                initUI();
                mapFragment.getMapAsync(new MyMapReadyCallback(MatchActivity.this));
            }

            @Override
            public void onFailure(Call<Match> call, Throwable t) {
                Toasty.error(MatchActivity.this, "Impossibile ottenere informazioni match").show();
                finish();
            }
        });

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

    private void initUI() {

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder);

        Glide.with(this)
                .load(match.getHost().getAvatarURL())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(userAvatar);

        nameView.setText(match.getHost().toString());
        jobView.setText(match.getHost().getCompany().toString());

        /*timeView.setText(
                String.format(
                        getResources().getString(R.string.match_time),
                        dateToString(match.getStartTime()),
                        dateToString(match.getEndTime())
                )
        );

        ArrayList<String> days = new ArrayList<>();
        for (int d : match.getWeekdays())
            days.add(getResources().getStringArray(R.array.giorni)[d]);
        daysView.setText(TextUtils.join(", ", days));*/
    }

    private int getScoreColor(int score) {
        if (score < 60) {
            return ContextCompat.getColor(this, R.color.red_500);
        } else if (score < 70) {
            return ContextCompat.getColor(this, R.color.orange_600);
        } else if (score < 80) {
            return ContextCompat.getColor(this, R.color.amber_400);
        } else if (score < 90) {
            return ContextCompat.getColor(this, R.color.light_green_500);
        } else {
            return ContextCompat.getColor(this, R.color.green_500);
        }
    }

    private void refreshUI() {
        matchLoadingView.setVisibility(View.GONE);
        ValueAnimator animator = ValueAnimator.ofInt(0, match.getScore());
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(valueAnimator -> scoreText.setText(valueAnimator.getAnimatedValue() + "%"));
        animator.start();

        int color = getScoreColor(match.getScore());
        scoreText.setTextColor(color);
    }

    @OnClick(R.id.profile_container)
    public void onViewClicked() {
        /*
        TODO Activity profilo utente
        Intent userIntent = new Intent(activity, ShowUserActivity.class);
        User matchedUser = match.getHost();
        userIntent.putExtra("userID", matchedUser.getId());
        activity.startActivity(userIntent);*/
    }


    private class MyMapReadyCallback implements OnMapReadyCallback, RoutingListener {

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

                List<LatLng> matchWaypoints = new ArrayList<>();
                matchWaypoints.add(match.getStartLocation());
                matchWaypoints.add(match.getEndLocation());

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
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(first)
                    .title("Casa")
            );


            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(last)
                    .title("Lavoro")
            );

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));

            refreshUI();
        }


        @Override
        public void onRoutingCancelled() {

        }
    }


}
