package it.gruppoinfor.home2work.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.UserPrefs;

import static it.gruppoinfor.home2work.App.getContext;

public class SettingsActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initUI();
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

    private void initUI(){
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

    }

    private CompoundButton.OnCheckedChangeListener getTrackingSwitchCheckedChangeListener() {
        return ((compoundButton, b) -> {
            if (!b) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Disattivazione tracking");
                builder.setMessage("Disattivando il tracking la tua posizione non sarà più registrata, ma Home2Work non potrà più segnalarti match. Disattivare la funzione?");
                builder.setPositiveButton("Disattiva", ((dialogInterface, i) -> {
                    UserPrefs.activityTrackingEnabled = false;
                    UserPrefs.getManager().setBool(UserPrefs.Keys.ACTIVITY_TRACKING, false);
                    // TODO bottomNavigation.setNotification("!", 4);
                }));
                builder.setNegativeButton("Annulla", (((dialogInterface, i) -> {
                    trackingSwitch.setChecked(true);
                })));
                builder.show();

            } else {
                UserPrefs.activityTrackingEnabled = true;
                UserPrefs.getManager().setBool(UserPrefs.Keys.ACTIVITY_TRACKING, true);
                // TODO bottomNavigation.setNotification("", 4);
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
        });
    }
}
