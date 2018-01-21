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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

    private void initUI() {
        trackingSwitch.setChecked(UserPrefs.TrackingEnabled);
        trackingSwitch.setOnCheckedChangeListener(getTrackingSwitchCheckedChangeListener());

        if (UserPrefs.SyncWithData)
            syncModeSpinner.setSelection(0);
        else
            syncModeSpinner.setSelection(1);

        syncModeSpinner.setOnItemSelectedListener(getSyncModeItemSelectedListener());

        notificationsSwitch.setChecked(UserPrefs.NotificationsEnabled);
        notificationsSwitch.setOnCheckedChangeListener(getNotificationSwitchCheckedChangeListener());

        newsNotificationSwitch.setChecked(UserPrefs.NewsNotificationsEnabled);
        newsNotificationSwitch.setOnCheckedChangeListener(((compoundButton, b) -> {
            UserPrefs.NewsNotificationsEnabled = b;
            UserPrefs.getManager().setBool(UserPrefs.NEWS_NOTIFICATIONS, b);
        }));

        matchesNotificationSwitch.setChecked(UserPrefs.MatchesNotificationsEnabled);
        matchesNotificationSwitch.setOnCheckedChangeListener(((compoundButton, b) -> {
            UserPrefs.MatchesNotificationsEnabled = b;
            UserPrefs.getManager().setBool(UserPrefs.MATCHES_NOTIFICATIONS, b);
        }));

    }

    private CompoundButton.OnCheckedChangeListener getTrackingSwitchCheckedChangeListener() {
        return ((compoundButton, b) -> {
            if (!b) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.activity_settings_dialog_tracking_title);
                builder.setMessage(R.string.activity_settings_dialog_tracking_content);
                builder.setPositiveButton(R.string.activity_settings_dialog_tracking_confirm, ((dialogInterface, i) -> {
                    UserPrefs.TrackingEnabled = false;
                    UserPrefs.getManager().setBool(UserPrefs.ACTIVITY_TRACKING, false);
                    // TODO bottomNavigation.setNotification("!", 4);
                }));
                builder.setNegativeButton(R.string.activity_settings_dialog_tracking_discard, (((dialogInterface, i) -> trackingSwitch.setChecked(true))));
                builder.show();

            } else {
                UserPrefs.TrackingEnabled = true;
                UserPrefs.getManager().setBool(UserPrefs.ACTIVITY_TRACKING, true);
                // TODO bottomNavigation.setNotification("", 4);
            }
        });
    }

    private AdapterView.OnItemSelectedListener getSyncModeItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    UserPrefs.getManager().setBool(UserPrefs.SYNC_WITH_DATA, false);
                    UserPrefs.SyncWithData = false;
                } else {
                    UserPrefs.getManager().setBool(UserPrefs.SYNC_WITH_DATA, true);
                    UserPrefs.SyncWithData = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    private CompoundButton.OnCheckedChangeListener getNotificationSwitchCheckedChangeListener() {
        return ((compoundButton, b) -> {
            UserPrefs.NotificationsEnabled = b;
            UserPrefs.getManager().setBool(UserPrefs.NOTIFICATIONS, b);
            newsNotificationSwitch.setEnabled(b);
            matchesNotificationSwitch.setEnabled(b);
        });
    }
}
