package it.gruppoinfor.home2work.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.SignInActivity;
import it.gruppoinfor.home2work.utils.Converters;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2work.utils.UserPrefs;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Address;
import it.gruppoinfor.home2workapi.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    @BindView(R.id.tracking_switch)
    Switch trackingSwitch;
    @BindView(R.id.sync_mode_spinner)
    Spinner syncModeSpinner;
    @BindView(R.id.settings_sync_mode_container)
    RelativeLayout settingsSyncModeContainer;
    @BindView(R.id.notifications_switch)
    Switch notificationsSwitch;
    @BindView(R.id.news_notification_switch)
    CheckBox newsNotificationSwitch;
    @BindView(R.id.matches_notification_switch)
    CheckBox matchesNotificationSwitch;
    @BindView(R.id.messages_notification_switch)
    CheckBox messagesNotificationSwitch;

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

        trackingSwitch.setChecked(UserPrefs.activityTrackingEnabled);
        trackingSwitch.setOnCheckedChangeListener(getTrackingSwitchCheckedChangeListener());

        if (UserPrefs.syncWithData)
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

    private CompoundButton.OnCheckedChangeListener getTrackingSwitchCheckedChangeListener() {
        return ((compoundButton, b) -> {
            if (!b) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Disattivazione tracking");
                builder.setMessage("Disattivando il tracking la tua posizione non sarà più registrata, ma Home2Work non potrà più segnalarti match. Disattivare la funzione?");
                builder.setPositiveButton("Disattiva", ((dialogInterface, i) -> {
                    UserPrefs.activityTrackingEnabled = false;
                    UserPrefs.getManager().setBool(UserPrefs.Keys.ACTIVITY_TRACKING, false);
                    ((MainActivity) getActivity()).bottomNavigation.setNotification("!", 4);
                }));
                builder.setNegativeButton("Annulla", (((dialogInterface, i) -> {
                    trackingSwitch.setChecked(true);
                })));
                builder.show();

            } else {
                UserPrefs.activityTrackingEnabled = true;
                UserPrefs.getManager().setBool(UserPrefs.Keys.ACTIVITY_TRACKING, true);
                ((MainActivity) getActivity()).bottomNavigation.setNotification("", 4);
            }
        });
    }

    private AdapterView.OnItemSelectedListener getSyncModeItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
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

    private CompoundButton.OnCheckedChangeListener getNotificationSwitchCheckedChangeListener() {
        return ((compoundButton, b) -> {
            UserPrefs.notificationsEnabled = b;
            UserPrefs.getManager().setBool(UserPrefs.Keys.NOTIFICATIONS, b);
            newsNotificationSwitch.setEnabled(b);
            matchesNotificationSwitch.setEnabled(b);
            messagesNotificationSwitch.setEnabled(b);
        });
    }

    //@OnClick(R.id.home_address_container)
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

                            User signedUser = Client.User;

                            signedUser.setLocation(latLng);

                            Address newAddress = new Address();
                            newAddress.setCity(city);
                            newAddress.setAddress(addr);
                            newAddress.setPostalCode(CAP);

                            signedUser.setAddress(newAddress);

                            //homeAddressTextView.setText(newAddress.toString());


                            Client.User = signedUser;

                            //commitChanges();


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

        User signedUserr = Client.User;

        addressInput.setText(signedUserr.getAddress().getAddress());
        capInput.setText(signedUserr.getAddress().getPostalCode());
        cityInput.setText(signedUserr.getAddress().getCity());

        editAddressDialog.show();

    }

    //@OnClick(R.id.job_address_container)
    public void onJobAddressContainerClicked() {
        Toasty.info(getContext(), "Non puoi modificare l'indirizzo di lavoro. Contatta l'amministratore per maggiori informazioni", Toast.LENGTH_LONG).show();
    }

/*    @OnClick(R.id.match_max_time_container)
    public void onMatchMaxTimeContainerClicked() {

        User signedUser = Client.getUser();
        UserMatchPreferences matchPreferences = signedUser.getMatchPreferences();

        *//*MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(getContext())
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

                        Client.setUser(signedUser);

                        commitChanges();

                        matchMaxTimeTextView.setText(matchPreferences.getMaxTime() + " minuti");

                    }
                })
                .show();*//*


        MaterialDialog editMaxTimeDialog = new MaterialDialog.Builder(getContext())
                .title("Modifica tempo massimo match")
                .customView(R.layout.dialog_number_picker, false)
                .positiveText("Salva")
                .negativeText("Annulla")
                .onPositive(((dialog, which) -> {

                    NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.number_picker);

                    matchPreferences.setMaxTime(numberPicker.getValue());

                    signedUser.setMatchPreferences(matchPreferences);

                    Client.setUser(signedUser);

                    commitChanges();

                    matchMaxTimeTextView.setText(matchPreferences.getMaxTime() + " minuti");

                }))
                .build();

        View view = editMaxTimeDialog.getCustomView();

        NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        numberPicker.setMax(120);
        numberPicker.setMin(15);
        numberPicker.setUnit(5);
        numberPicker.setValue(matchPreferences.getMaxTime());

        editMaxTimeDialog.show();

    }

    @OnClick(R.id.match_max_distance_container)
    public void onMatchMaxDistanceContainerClicked() {

        User signedUser = Client.getUser();
        UserMatchPreferences matchPreferences = signedUser.getMatchPreferences();

       *//* MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(getContext())
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

                        Client.setUser(signedUser);

                        commitChanges();

                        matchMaxDistanceTextView.setText(matchPreferences.getMaxDistance() + " metri");

                    }
                })
                .show();*//*

        MaterialDialog editMaxTimeDialog = new MaterialDialog.Builder(getContext())
                .title("Distanza massima match")
                .customView(R.layout.dialog_number_picker, false)
                .positiveText("Salva")
                .negativeText("Annulla")
                .onPositive(((dialog, which) -> {

                    NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.number_picker);

                    matchPreferences.setMaxDistance(numberPicker.getValue());

                    signedUser.setMatchPreferences(matchPreferences);

                    Client.setUser(signedUser);

                    commitChanges();

                    matchMaxDistanceTextView.setText(matchPreferences.getMaxDistance() + " metri");

                }))
                .build();

        View view = editMaxTimeDialog.getCustomView();

        NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        numberPicker.setMax(5000);
        numberPicker.setMin(500);
        numberPicker.setUnit(100);
        numberPicker.setValue(matchPreferences.getMaxDistance());

        editMaxTimeDialog.show();


    }

    @OnClick(R.id.match_min_score_container)
    public void onMatchMinScoreContainerClicked() {

        User signedUser = Client.getUser();
        UserMatchPreferences matchPreferences = signedUser.getMatchPreferences();

*//*        MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(getContext())
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

                        Client.setUser(signedUser);

                        commitChanges();

                        matchMinScoreTextView.setText(matchPreferences.getMinScore() + "%");

                    }
                })
                .show();*//*


        MaterialDialog editMaxTimeDialog = new MaterialDialog.Builder(getContext())
                .title("Punteggio minimo per i match")
                .customView(R.layout.dialog_number_picker, false)
                .positiveText("Salva")
                .negativeText("Annulla")
                .onPositive(((dialog, which) -> {

                    NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.number_picker);

                    matchPreferences.setMinScore(numberPicker.getValue());

                    signedUser.setMatchPreferences(matchPreferences);

                    Client.setUser(signedUser);

                    commitChanges();

                    matchMinScoreTextView.setText(matchPreferences.getMinScore() + "%");

                }))
                .build();

        View view = editMaxTimeDialog.getCustomView();

        NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        numberPicker.setMax(100);
        numberPicker.setMin(10);
        numberPicker.setUnit(10);
        numberPicker.setValue(matchPreferences.getMinScore());

        editMaxTimeDialog.show();
    }*/

    private void logout() {
        SessionManager.with(getContext()).signOutUser();

        // Avvio Activity di login
        Intent i = new Intent(getContext(), SignInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
