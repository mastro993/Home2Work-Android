package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import it.gruppoinfor.home2workapi.model.Match;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static it.gruppoinfor.home2work.utils.Converters.dateToString;

public class MatchActivity extends AppCompatActivity {

    public static final int NEW_BOOKIG_REQUEST = 888;
    public static final int BOOKING_ADDED = 2;
    public static final int BOOKING_NOT_ADDED = 3;
    private static final String GOOGLE_API_KEY = "AIzaSyCh8NUxxBR-ayyEq_EGFUU1JFVVFVwUq-I";
    GoogleMap googleMap;
    Long matchId;
    Match match;
    SupportMapFragment mapFragment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.request_shere_button)
    Button requestShereButton;
    @BindView(R.id.match_loading_view)
    FrameLayout matchLoadingView;
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
    TextView timeView;
    @BindView(R.id.days_view)
    TextView daysView;
    @BindView(R.id.karma_preview)
    TextView karmaPreview;
    @BindView(R.id.exp_preview)
    TextView expPreview;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    @BindView(R.id.status_text)
    TextView statusText;

    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        ButterKnife.bind(this);
        res = getResources();


        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        matchId = getIntent().getLongExtra("matchID", 0L);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.onCreate(savedInstanceState);

        Client.getAPI().getMatch(matchId).enqueue(new Callback<Match>() {
            @Override
            public void onResponse(Call<Match> call, Response<Match> response) {
                match = response.body();
                initUI();
                mapFragment.getMapAsync(new MyMapReadyCallback(MatchActivity.this));
            }

            @Override
            public void onFailure(Call<Match> call, Throwable t) {
                finish();
            }
        });

        matchLoadingView.setVisibility(View.VISIBLE);

    }

    private void initUI() {

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        Double kmDistance = match.getDistance() / 1000.0;

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar_placeholder).dontAnimate();
        Glide.with(this)
                .load(match.getHost().getAvatarURL())
                .apply(requestOptions)
                .into(userAvatar);

        nameView.setText(match.getHost().toString());
        jobView.setText(match.getHost().getCompany().toString());

        int color = ScoreColorUtility.getScoreColor(this, match.getScore());
        Drawable bg = ContextCompat.getDrawable(this, R.drawable.bg_match_score_percent);

        scoreProgress.setFinishedStrokeColor(color);
        bg.setTint(color);
        scoreText.setBackground(bg);

        timeView.setText(dateToString(match.getStartTime()) + " - " + dateToString(match.getEndTime()));

        ArrayList<String> days = new ArrayList<>();
        for (int day : match.getWeekdays()) {
            days.add(getResources().getStringArray(R.array.giorni)[day]);
        }

        daysView.setText(TextUtils.join(", ", days));

        int karmaPoints = kmDistance.intValue();
        int exp = (int) (kmDistance * 10);
        karmaPreview.setText(String.format(res.getString(R.string.match_karma_preview), karmaPoints));
        expPreview.setText(String.format(res.getString(R.string.match_exo_preview), exp));

        if (match.isOngoing()) {
            requestShereButton.setVisibility(View.GONE);
            statusText.setVisibility(View.VISIBLE);
        }

    }

    private void refreshUI() {
        matchLoadingView.setVisibility(View.GONE);

        ArcProgressAnimation animation = new ArcProgressAnimation(scoreProgress, 0, match.getScore());
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        scoreProgress.startAnimation(animation);

        ValueAnimator animator = ValueAnimator.ofInt(0, match.getScore());
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation1 ->
                scoreText.setText(String.format(Locale.ITALY, "%1$s%%", animation1.getAnimatedValue().toString()))
        );
        animator.start();
    }


    @OnClick(R.id.request_shere_button)
    void requestShare() {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        MaterialDialog confirmationDialog = new MaterialDialog.Builder(this)
                .title("Conferma prenotazione")
                .customView(R.layout.dialog_new_booking, false)
                .positiveText("Conferma")
                .negativeText("Annulla")
                .onPositive(((MaterialDialog dialog, DialogAction which) -> {

                    EditText noteInput = (EditText) dialog.findViewById(R.id.notes_input);

                    String notes = noteInput.getText().toString();
                    Date date = c.getTime();

                    Booking booking = new Booking();
                    booking.setBookedDate(date);
                    booking.setBookedMatch(match);
                    booking.setNotes(notes);

                    Client.getAPI().bookMatch(booking).enqueue(new Callback<Booking>() {
                        @Override
                        public void onResponse(Call<Booking> call, Response<Booking> response) {

                            if (response.code() == 200) {
                                setResult(BOOKING_ADDED);
                                finish();
                            } else {
                                setResult(BOOKING_NOT_ADDED);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Booking> call, Throwable t) {
                            finish();
                        }
                    });

                }))
                .build();

        TextView dateText = (TextView) confirmationDialog.findViewById(R.id.selected_date_text);
        TextView noteTitle = (TextView) confirmationDialog.findViewById(R.id.notes_title);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
            c.set(year, month, day);

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ITALIAN);
            String dateString = dateFormat.format(c.getTime());
            dateText.setText(dateString);

            noteTitle.setText(String.format(res.getString(R.string.new_booking_notes_title), match.getHost().getName()));

            confirmationDialog.show();


        }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 360000 * 240);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 360000 * 240 * 14);

        datePickerDialog.setTitle("Seleziona la data di prenotazione");


        datePickerDialog.show();

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


            // TODO inserire informazioni di partenza ed arrivo

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
            refreshUI();
        }


        @Override
        public void onRoutingCancelled() {

        }
    }

}
