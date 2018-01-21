package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.RouteUtils;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.model.LatLng;
import it.gruppoinfor.home2workapi.model.Match;

import static it.gruppoinfor.home2work.utils.Converters.dateToString;

public class MatchActivity extends AppCompatActivity {

    public static final String EXTRA_MATCH = "match";
    private static final String GOOGLE_API_KEY = "AIzaSyCh8NUxxBR-ayyEq_EGFUU1JFVVFVwUq-I";
    @BindView(R.id.score_text)
    TextView scoreText;
    @BindView(R.id.home_view)
    TextView homeView;
    SupportMapFragment mapFragment;
    @BindView(R.id.match_loading_view)
    FrameLayout matchLoadingView;
    @BindView(R.id.user_avatar)
    ImageView userAvatar;
    @BindView(R.id.name_view)
    TextView nameView;
    @BindView(R.id.job_view)
    TextView jobView;
    @BindView(R.id.timetable_time)
    TextView timetableTime;
    @BindView(R.id.timetable_weekdays)
    TextView timetableWeekdays;
    private GoogleMap googleMap;
    private Match match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.onCreate(savedInstanceState);

        matchLoadingView.setVisibility(View.VISIBLE);

        mapFragment.getMapAsync(new MyMapReadyCallback(this));

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_MATCH)) {
            match = (Match) intent.getSerializableExtra(EXTRA_MATCH);
            initUI();
        } else {
            Toasty.error(this, getString(R.string.activity_match_error)).show();
            finish();
        }

        HomeToWorkClient.getInstance().editMatch(match);
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

    @OnClick(R.id.profile_container)
    public void onViewClicked() {
        /*
        TODO Activity profilo utente
        Intent userIntent = new Intent(activity, ShowUserActivity.class);
        User matchedUser = match.getHost();
        userIntent.putExtra("userID", matchedUser.getId());
        activity.startActivity(userIntent);*/
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
        homeView.setText(match.getHost().getAddress().getCity());

        timetableTime.setText(
                String.format(
                        getResources().getString(R.string.activity_match_time),
                        dateToString(match.getStartTime()),
                        dateToString(match.getEndTime())
                )
        );

        ArrayList<String> days = new ArrayList<>();
        for (int d : match.getWeekdays())
            days.add(getResources().getStringArray(R.array.giorni)[d]);
        timetableWeekdays.setText(TextUtils.join(", ", days));

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
        animator.addUpdateListener(valueAnimator -> scoreText.setText(String.format(Locale.ITALIAN, "%1$d%%", (Integer) valueAnimator.getAnimatedValue())));
        animator.start();

        int color = getScoreColor(match.getScore());
        scoreText.setTextColor(color);
    }

    private class MyMapReadyCallback implements OnMapReadyCallback, RoutingListener {

        private Context mContext;

        MyMapReadyCallback(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public void onMapReady(final GoogleMap gmap) {
            googleMap = gmap;
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                MapsInitializer.initialize(mContext);

                List<com.google.android.gms.maps.model.LatLng> matchWaypoints = new ArrayList<>();
                matchWaypoints.add(match.getStartLocation().toLatLng());
                matchWaypoints.add(match.getEndLocation().toLatLng());

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
            Toasty.error(mContext, mContext.getString(R.string.activity_match_error)).show();
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
            polyOptions.color(ContextCompat.getColor(mContext, R.color.red_500));
            polyOptions.addAll(route.getPoints());

            googleMap.addPolyline(polyOptions);

            final LatLngBounds latLngBounds = RouteUtils.getRouteBounds(route.getPoints());

            LatLng first = match.getStartLocation();
            LatLng last = match.getEndLocation();

            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(first.toLatLng())
                    .title("Casa")
            );


            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(last.toLatLng())
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
