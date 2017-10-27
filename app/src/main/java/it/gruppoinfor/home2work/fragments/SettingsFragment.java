package it.gruppoinfor.home2work.fragments;


import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.model.LatLng;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.utils.Converters;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.api.SessionManager;
import it.gruppoinfor.home2work.utils.UserPrefs;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.SignInActivity;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.models.Address;
import it.gruppoinfor.home2work.models.Job;
import it.gruppoinfor.home2work.models.UserMatchPreferences;
import it.gruppoinfor.home2work.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

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
    RelativeLayout jobTimeContainer;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.match_max_time_text_view)
    TextView matchMaxTimeTextView;
    @BindView(R.id.match_max_time_container)
    RelativeLayout matchMaxTimeContainer;
    @BindView(R.id.match_max_distance_text_view)
    TextView matchMaxDistanceTextView;
    @BindView(R.id.match_max_distance_container)
    RelativeLayout matchMaxDistanceContainer;
    @BindView(R.id.match_min_score_text_view)
    TextView matchMinScoreTextView;
    @BindView(R.id.match_min_score_container)
    RelativeLayout matchMinScoreContainer;
    @BindView(R.id.notifications_switch)
    Switch notificationsSwitch;
    @BindView(R.id.news_notification_switch)
    Switch newsNotificationSwitch;
    @BindView(R.id.matches_notification_switch)
    Switch matchesNotificationSwitch;
    @BindView(R.id.messages_notification_switch)
    Switch messagesNotificationSwitch;
    @BindView(R.id.tracking_switch)
    Switch trackingSwitch;
    @BindView(R.id.settings_tracking_container)
    RelativeLayout settingsTrackingContainer;
    @BindView(R.id.sync_mode_spinner)
    Spinner syncModeSpinner;
    @BindView(R.id.settings_sync_mode_container)
    RelativeLayout settingsSyncModeContainer;
    private Unbinder unbinder;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScrollView rootView = (ScrollView) inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        initUI();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_logout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.logout_dialog_title);
            builder.setMessage(R.string.logout_dialog_content);
            builder.setPositiveButton(R.string.logout_dialog_confirm, ((dialogInterface, i) -> logout()));
            builder.setNegativeButton(R.string.logout_dialog_cancel, null);
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initUI() {

        User signedUser = Client.getSignedUser();

        homeAddressTextView.setText(signedUser.getAddress().toString());

        Job job = Client.getSignedUser().getJob();


        jobAddressTextView.setText(job.getCompany().getAddress().toString());

        jobStartTimeTextView.setText(Converters.timestampToTime(job.getStart(), "HH:mm"));
        jobEndTimeTextView.setText(Converters.timestampToTime(job.getEnd(), "HH:mm"));

        UserMatchPreferences matchPreferences = signedUser.getMatchPreferences();

        matchMaxTimeTextView.setText(matchPreferences.getMaxTime() + " minuti");
        matchMinScoreTextView.setText(matchPreferences.getMinScore() + "%");
        matchMaxDistanceTextView.setText(matchPreferences.getMaxDistance() + " metri");

        trackingSwitch.setChecked(UserPrefs.activityTrackingEnabled);
        trackingSwitch.setOnCheckedChangeListener(getTrackingSwitchCheckedChangeListener());

        if(UserPrefs.syncWithData)
            syncModeSpinner.setSelection(0);
        else
            syncModeSpinner.setSelection(1);

        syncModeSpinner.setOnItemSelectedListener(getSyncModeItemSelectedListener());

        notificationsSwitch.setChecked(UserPrefs.notificationsEnabled);
        notificationsSwitch.setOnCheckedChangeListener(getNotificationSwitchCheckedChangeListener());

        newsNotificationSwitch.setChecked(UserPrefs.newsNotificationsEnabled);
        newsNotificationSwitch.setOnCheckedChangeListener(((compoundButton, b) -> {
            UserPrefs.newsNotificationsEnabled = b;
            UserPrefs.getManager().setBool(UserPrefs.Keys.NEWS_NOTIFICATIONS, b);
        }));

        matchesNotificationSwitch.setChecked(UserPrefs.matchesNotificationsEnabled);
        matchesNotificationSwitch.setOnCheckedChangeListener(((compoundButton, b) -> {
            UserPrefs.matchesNotificationsEnabled = b;
            UserPrefs.getManager().setBool(UserPrefs.Keys.MATCHES_NOTIFICATIONS, b);
        }));

        messagesNotificationSwitch.setChecked(UserPrefs.messagesNotificationsEnabled);
        messagesNotificationSwitch.setOnCheckedChangeListener(((compoundButton, b) -> {
            UserPrefs.messagesNotificationsEnabled = b;
            UserPrefs.getManager().setBool(UserPrefs.Keys.MSG_NOTIFICATIONS, b);
        }));

    }

    private CompoundButton.OnCheckedChangeListener getTrackingSwitchCheckedChangeListener(){
        return ((compoundButton, b) -> {
            if(!b){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Disattivazione tracking");
                builder.setMessage("Disattivando il tracking la tua posizione non sarà più registrata, ma Home2Work non potrà più segnalarti match. Disattivare la funzione?");
                builder.setPositiveButton("Disattiva", ((dialogInterface, i) -> {
                    UserPrefs.activityTrackingEnabled = false;
                    UserPrefs.getManager().setBool(UserPrefs.Keys.ACTIVITY_TRACKING, false);
                    ((MainActivity)getActivity()).bottomNavigation.setNotification("!", 4);
                }));
                builder.setNegativeButton("Annulla", (((dialogInterface, i) -> {
                    trackingSwitch.setChecked(true);
                })));
                builder.show();

            } else {
                UserPrefs.activityTrackingEnabled = true;
                UserPrefs.getManager().setBool(UserPrefs.Keys.ACTIVITY_TRACKING, true);
                ((MainActivity)getActivity()).bottomNavigation.setNotification("", 4);
            }
        });
    }

    private AdapterView.OnItemSelectedListener getSyncModeItemSelectedListener(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1){
                    UserPrefs.getManager().setBool(UserPrefs.Keys.SYNC_WITH_DATA, false);
                    UserPrefs.syncWithData = false;
                } else {
                    UserPrefs.getManager().setBool(UserPrefs.Keys.SYNC_WITH_DATA, true);
                    UserPrefs.syncWithData = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    private CompoundButton.OnCheckedChangeListener getNotificationSwitchCheckedChangeListener(){
        return ((compoundButton, b) -> {
            UserPrefs.notificationsEnabled = b;
            UserPrefs.getManager().setBool(UserPrefs.Keys.NOTIFICATIONS, b);
            newsNotificationSwitch.setEnabled(b);
            matchesNotificationSwitch.setEnabled(b);
            messagesNotificationSwitch.setEnabled(b);
        });
    }

    @OnClick(R.id.home_address_container)
    public void onHomeAddressContainerClicked() {

        MaterialDialog editAddressDialog = new MaterialDialog.Builder(getContext())
                .title("Modifica indirizzo")
                .customView(R.layout.dialog_edit_address, false)
                .positiveText("Salva")
                .negativeText("Annulla")
                .onPositive(((dialog, which) -> {

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
                        LatLng latLng = Converters.addressToLatLng(getContext(), addr + ", " + city + " " + CAP);
                        if (latLng != null) {

                            User signedUser = Client.getSignedUser();

                            signedUser.setLocation(latLng);

                            Address newAddress = new Address();
                            newAddress.setCity(city);
                            newAddress.setAddress(addr);
                            newAddress.setPostalCode(CAP);

                            signedUser.setAddress(newAddress);

                            homeAddressTextView.setText(newAddress.toString());


                            Client.setSignedUser(signedUser);

                            commitChanges();


                        } else {
                            Toasty.warning(getContext(), getString(R.string.config_no_address_found), Toast.LENGTH_LONG).show();
                        }
                    }

                }))
                .build();

        View view = editAddressDialog.getCustomView();

        EditText cityInput = view.findViewById(R.id.city_input);
        EditText capInput = view.findViewById(R.id.cap_input);
        EditText addressInput = view.findViewById(R.id.address_input);

        User signedUserr = Client.getSignedUser();

        addressInput.setText(signedUserr.getAddress().getAddress());
        capInput.setText(signedUserr.getAddress().getPostalCode());
        cityInput.setText(signedUserr.getAddress().getCity());

        editAddressDialog.show();

    }

    @OnClick(R.id.job_start_time_text_view)
    public void onJobStartTimeTextViewClicked() {

        Long timestamp = Client.getSignedUser().getJob().getStart();

        int hour = Integer.parseInt(Converters.timestampToTime(timestamp, "HH"));
        int minute = Integer.parseInt(Converters.timestampToTime(timestamp, "mm"));

        final TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                String formattedMinute = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
                jobStartTimeTextView.setText(selectedHour + ":" + formattedMinute);
                Long timestamp = Converters.timeToTimestamp(selectedHour, selectedMinute, 0);

                User signedUser = Client.getSignedUser();
                signedUser.getJob().setStart(timestamp);
                Client.setSignedUser(signedUser);

                commitChanges();
            }
        }, hour, minute, true);

        mTimePicker.setTitle(R.string.config_job_start_selection);
        mTimePicker.show();

    }

    @OnClick(R.id.job_end_time_text_view)
    public void onJobEndTimeTextViewClicked() {

        Long timestamp = Client.getSignedUser().getJob().getEnd();

        int hour = Integer.parseInt(Converters.timestampToTime(timestamp, "HH"));
        int minute = Integer.parseInt(Converters.timestampToTime(timestamp, "mm"));

        final TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String formattedMinute = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
                jobEndTimeTextView.setText(selectedHour + ":" + formattedMinute);
                Long timestamp = Converters.timeToTimestamp(selectedHour, selectedMinute, 0);

                User signedUser = Client.getSignedUser();
                signedUser.getJob().setEnd(timestamp);
                Client.setSignedUser(signedUser);

                commitChanges();
            }
        }, hour, minute, true);

        mTimePicker.setTitle(R.string.config_job_end_selection);
        mTimePicker.show();
    }

    @OnClick(R.id.job_address_container)
    public void onJobAddressContainerClicked() {
        Toasty.info(getContext(), "Non puoi modificare l'indirizzo di lavoro. Contatta l'amministratore per maggiori informazioni", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.match_max_time_container)
    public void onMatchMaxTimeContainerClicked() {

        User signedUser = Client.getSignedUser();
        UserMatchPreferences matchPreferences = signedUser.getMatchPreferences();

        MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(getContext())
                .minValue(5)
                .maxValue(120)
                .defaultValue(matchPreferences.getMaxTime())
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.TRANSPARENT)
                .textColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();

        new AlertDialog.Builder(getContext())
                .setTitle("Differenza di tempo massima per i match (in minuti)")
                .setView(numberPicker)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        matchPreferences.setMaxTime(numberPicker.getValue());

                        signedUser.setMatchPreferences(matchPreferences);

                        Client.setSignedUser(signedUser);

                        commitChanges();

                        matchMaxTimeTextView.setText(matchPreferences.getMaxTime() + " minuti");

                    }
                })
                .show();

    }

    @OnClick(R.id.match_max_distance_container)
    public void onMatchMaxDistanceContainerClicked() {

        User signedUser = Client.getSignedUser();
        UserMatchPreferences matchPreferences = signedUser.getMatchPreferences();

        MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(getContext())
                .minValue(500)
                .maxValue(5000)
                .defaultValue(matchPreferences.getMaxDistance())
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.TRANSPARENT)
                .textColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                .textSize(20)
                .enableFocusability(true)
                .wrapSelectorWheel(true)
                .build();

        new AlertDialog.Builder(getContext())
                .setTitle("Distanza massima match (in metri)")
                .setView(numberPicker)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        matchPreferences.setMaxDistance(numberPicker.getValue());

                        signedUser.setMatchPreferences(matchPreferences);

                        Client.setSignedUser(signedUser);

                        commitChanges();

                        matchMaxDistanceTextView.setText(matchPreferences.getMaxDistance() + " metri");

                    }
                })
                .show();


    }

    @OnClick(R.id.match_min_score_container)
    public void onMatchMinScoreContainerClicked() {

        User signedUser = Client.getSignedUser();
        UserMatchPreferences matchPreferences = signedUser.getMatchPreferences();

        MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(getContext())
                .minValue(1)
                .maxValue(100)
                .defaultValue(matchPreferences.getMinScore())
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.TRANSPARENT)
                .textColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();

        new AlertDialog.Builder(getContext())
                .setTitle("Punteggio minimo per i match")
                .setView(numberPicker)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        matchPreferences.setMinScore(numberPicker.getValue());

                        signedUser.setMatchPreferences(matchPreferences);

                        Client.setSignedUser(signedUser);

                        commitChanges();

                        matchMinScoreTextView.setText(matchPreferences.getMinScore() + "%");

                    }
                })
                .show();
    }

    private void commitChanges() {
        Client.getAPI().updateUser(Client.getSignedUser()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toasty.success(getContext(), "Modifiche avvenute con successo").show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toasty.error(getContext(), "Impossibile apportare modifiche al momento, riprova più tardi").show();
            }
        });
    }

    private void logout() {
        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.signOutUser();

        // Avvio Activity di login
        Intent i = new Intent(getContext(), SignInActivity.class);
        i.putExtra(SessionManager.AUTH_CODE, SessionManager.AuthCode.SIGNED_OUT);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
