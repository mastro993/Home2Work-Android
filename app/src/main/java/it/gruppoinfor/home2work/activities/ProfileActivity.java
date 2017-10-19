package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.Converters;
import it.gruppoinfor.home2work.FileUtils;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.SessionManager;
import it.gruppoinfor.home2work.Tools;
import it.gruppoinfor.home2work.api.APIClient;
import it.gruppoinfor.home2work.api.Account;
import it.gruppoinfor.home2work.models.Company;
import it.gruppoinfor.home2work.models.Job;
import it.gruppoinfor.home2work.models.Statistics;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final int PHOTO_INTENT = 0;


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.user_avatar)
    CircleImageView userAvatar;
    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.job_text_view)
    TextView jobTextView;
    @BindView(R.id.profileOverview)
    LinearLayout profileOverview;
    @BindView(R.id.stats_text_view)
    TextView statsTextView;
    @BindView(R.id.regdate_text_view)
    TextView regdateTextView;
    @BindView(R.id.distance_text_view)
    TextView distanceTextView;
    @BindView(R.id.gas_text_view)
    TextView gasTextView;
    @BindView(R.id.emissions_text_view)
    TextView emissionsTextView;
    @BindView(R.id.shares_text_view)
    TextView sharesTextView;
    @BindView(R.id.shared_distance_text_view)
    TextView sharedDistanceTextView;
    @BindView(R.id.saved_gas_text_view)
    TextView savedGasTextView;
    @BindView(R.id.saved_emissions_text_view)
    TextView savedEmissionsTextView;
    @BindView(R.id.home_address_text_view)
    TextView homeAddressTextView;
    @BindView(R.id.home_address_container)
    LinearLayout homeAddressContainer;
    @BindView(R.id.job_address_text_view)
    TextView jobAddressTextView;
    @BindView(R.id.job_address_container)
    LinearLayout jobAddressContainer;
    @BindView(R.id.job_start_time_text_view)
    TextView jobStartTimeTextView;
    @BindView(R.id.job_end_time_text_view)
    TextView jobEndTimeTextView;
    @BindView(R.id.job_time_container)
    ConstraintLayout jobTimeContainer;
    @BindView(R.id.home_address_loading)
    AVLoadingIndicatorView homeAddressLoading;
    @BindView(R.id.job_address_loading)
    AVLoadingIndicatorView jobAddressLoading;
    @BindView(R.id.imageView)
    ImageView imageView;

    private Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }

        initUI();

    }

    private void initUI() {
        Resources res = getResources();

        nameTextView.setText(APIClient.getAccount().toString());

        Company company = APIClient.getAccount().getJob().getCompany();

        jobTextView.setText(company.getName() + " (" + company.getAddress().getDistrict() + ")");

        Glide.with(this)
                .load(APIClient.getAccount().getAvatarURL())
                .placeholder(R.drawable.ic_avatar_placeholder)
                .dontAnimate()
                .into(userAvatar);

        Statistics stats = APIClient.getAccount().getStatistics();

        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.CEILING);

        regdateTextView.setText(String.format(res.getString(R.string.profile_regdate), Converters.dateToString(APIClient.getAccount().getRegistrationDate(), "dd/MM/yyyy")));
        distanceTextView.setText(String.format(res.getString(R.string.profile_distance), df.format(stats.getDistance())));
        gasTextView.setText(String.format(res.getString(R.string.profile_gas), df.format(stats.getConsumption())));
        emissionsTextView.setText(String.format(res.getString(R.string.profile_emissions), df.format(stats.getEmission())));
        sharesTextView.setText(String.format(res.getString(R.string.profile_shares), stats.getShares()));
        sharedDistanceTextView.setText(String.format(res.getString(R.string.profile_shared_distance), df.format(stats.getSharedDistance())));
        savedGasTextView.setText(String.format(res.getString(R.string.profile_saved_gas), df.format(stats.getSavedConsumption())));
        savedEmissionsTextView.setText(String.format(res.getString(R.string.profile_saved_emissions), df.format(stats.getSavedEmission())));

        Converters.latLngToAddress(this, APIClient.getAccount().getLocation(), (address -> {
            homeAddressTextView.setText(address.getAddressLine(0));
            homeAddressLoading.setVisibility(View.GONE);
            homeAddressTextView.setVisibility(View.VISIBLE);
        }));

        Converters.latLngToAddress(this, APIClient.getAccount().getJob().getCompany().getLocation(), (address -> {
            jobAddressTextView.setText(address.getAddressLine(0));
            jobAddressLoading.setVisibility(View.GONE);
            jobAddressTextView.setVisibility(View.VISIBLE);
        }));

        Job job = APIClient.getAccount().getJob();

        jobStartTimeTextView.setText(Converters.timestampToTime(job.getStart(), "HH:mm"));
        jobEndTimeTextView.setText(Converters.timestampToTime(job.getEnd(), "HH:mm"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_logout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.logout_dialog_title);
            builder.setMessage(R.string.logout_dialog_content);
            builder.setPositiveButton(R.string.logout_dialog_confirm, ((dialogInterface, i) -> logout()));
            builder.setNegativeButton(R.string.logout_dialog_cancel, null);
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.signOutUser();

        // Avvio Activity di login
        Intent i = new Intent(this, SignInActivity.class);
        i.putExtra(SessionManager.AUTH_CODE, SessionManager.AuthCode.SIGNED_OUT);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }


    @OnClick(R.id.user_avatar)
    public void onUserAvatarClicked() {
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED || writePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            selectPhotoIntent();
        }
        // TODO modifica avatar
    }


    @OnClick(R.id.home_address_container)
    public void onHomeAddressContainerClicked() {

        MaterialDialog editAddressDialog = new MaterialDialog.Builder(this)
                .title("Modifica indirizzo")
                .customView(R.layout.dialog_edit_address, false)
                .positiveText("Salva")
                .negativeText("Annulla")
                .onPositive(((dialog, which) -> {

                    homeAddressLoading.setVisibility(View.VISIBLE);
                    homeAddressTextView.setVisibility(View.GONE);

                    Boolean valid = true;

                    View view = dialog.getCustomView();

                    EditText cityInput = view.findViewById(R.id.city_input);
                    EditText capInput = view.findViewById(R.id.cap_input);
                    EditText addressInput = view.findViewById(R.id.address_input);

                    String addr = addressInput.getText().toString();
                    String city = cityInput.getText().toString();
                    String CAP = capInput.getText().toString();

                    if (addr.isEmpty()) {
                        addressInput.setError(getString(R.string.config_address_insert));
                        valid = false;
                    }

                    if (city.isEmpty()) {
                        capInput.setError(getString(R.string.config_cap_insert));
                        valid = false;
                    }

                    if (CAP.isEmpty()) {
                        cityInput.setError(getString(R.string.config_city_insert));
                        valid = false;
                    }

                    if (valid) {
                        LatLng latLng = Converters.addressToLatLng(this, addr + ", " + city + " " + CAP);
                        if (latLng != null) {
                            APIClient.getAccount().setLocation(latLng);
                            Converters.latLngToAddress(this, latLng, (address -> {
                                homeAddressTextView.setText(address.getAddressLine(0));
                            }));

                            commitChanges();

                        } else {
                            Toasty.warning(this, getString(R.string.config_no_address_found), Toast.LENGTH_LONG).show();
                        }
                    }

                    homeAddressLoading.setVisibility(View.GONE);
                    homeAddressTextView.setVisibility(View.VISIBLE);

                }))
                .build();

        View view = editAddressDialog.getCustomView();

        EditText cityInput = view.findViewById(R.id.city_input);
        EditText capInput = view.findViewById(R.id.cap_input);
        EditText addressInput = view.findViewById(R.id.address_input);

        Converters.latLngToAddress(this, APIClient.getAccount().getLocation(), (address -> {
            if (address != null) {
                addressInput.setText(address.getAddressLine(0));
                capInput.setText(address.getPostalCode());
                cityInput.setText(address.getLocality());
            }
        }));

        editAddressDialog.show();

    }


    private void commitChanges() {
        // TODO modifica informazioni
        APIClient.API().updateUser(APIClient.getAccount()).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                Toasty.success(ProfileActivity.this, "Modifiche avvenute con successo").show();
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Toasty.error(ProfileActivity.this, "Impossibile apportare modifiche al momento, riprova più tardi").show();
            }
        });
    }

    private void selectPhotoIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.edit_avatar_photo_selection)), PHOTO_INTENT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    getString(R.string.edit_avatar_photo_selection)), PHOTO_INTENT);
        } else {
            onUserAvatarClicked();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_INTENT && resultCode == RESULT_OK) {

            Uri selectedImageURI = data.getData();
            File file = FileUtils.getFile(this, selectedImageURI);
            String decodedAvatar = Tools.decodeFile(file.getPath(), 300, 300);
            File decodedFile = new File(decodedAvatar);

            String mime = Tools.getMimeType(decodedFile.getPath());
            MediaType mediaType = MediaType.parse(mime);

            RequestBody requestFile = RequestBody.create(mediaType, decodedFile);

            String filename = APIClient.getAccount().getId() + ".jpg";

            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", filename, requestFile);


            APIClient.API().uploadAvatar(APIClient.getAccount().getId(), body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Toasty.success(ProfileActivity.this, "Avatar caricato con successo").show();
                    Glide.with(ProfileActivity.this)
                            .load(APIClient.getAccount().getAvatarURL())
                            .placeholder(R.drawable.ic_avatar_placeholder)
                            .dontAnimate()
                            .into(userAvatar);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @OnClick(R.id.job_start_time_text_view)
    public void onJobStartTimeTextViewClicked() {

        Long timestamp = APIClient.getAccount().getJob().getStart();

        int hour = Integer.parseInt(Converters.timestampToTime(timestamp, "HH"));
        int minute = Integer.parseInt(Converters.timestampToTime(timestamp, "mm"));

        final TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String formattedMinute = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
                jobStartTimeTextView.setText(selectedHour + ":" + formattedMinute);
                Long timestamp = Converters.timeToTimestamp(selectedHour, selectedMinute, 0);
                APIClient.getAccount().getJob().setStart(timestamp);
                commitChanges();
            }
        }, hour, minute, true);

        mTimePicker.setTitle(R.string.config_job_start_selection);
        mTimePicker.show();

    }

    @OnClick(R.id.job_end_time_text_view)
    public void onJobEndTimeTextViewClicked() {

        Long timestamp = APIClient.getAccount().getJob().getEnd();

        int hour = Integer.parseInt(Converters.timestampToTime(timestamp, "HH"));
        int minute = Integer.parseInt(Converters.timestampToTime(timestamp, "mm"));

        final TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String formattedMinute = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
                jobStartTimeTextView.setText(selectedHour + ":" + formattedMinute);
                Long timestamp = Converters.timeToTimestamp(selectedHour, selectedMinute, 0);
                APIClient.getAccount().getJob().setEnd(timestamp);
                commitChanges();
            }
        }, hour, minute, true);

        mTimePicker.setTitle(R.string.config_job_end_selection);
        mTimePicker.show();
    }
}
