package it.gruppoinfor.home2work.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.UserPrefs;


public class SettingsActivity extends AppCompatActivity {


    @BindView(R.id.text_tracking)
    TextView textTracking;
    @BindView(R.id.switch_tracking)
    Switch switchTracking;
    @BindView(R.id.spinner_sync_mode)
    Spinner spinnerSyncMode;
    @BindView(R.id.switch_notifications)
    Switch switchNotifications;
    @BindView(R.id.check_notifications_news)
    CheckBox checkNotificationsNews;
    @BindView(R.id.check_notifications_match)
    CheckBox checkNotificationsMatch;

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
        switchTracking.setChecked(UserPrefs.TrackingEnabled);
        switchTracking.setOnCheckedChangeListener(getTrackingSwitchCheckedChangeListener());
        if (UserPrefs.TrackingEnabled) {
            textTracking.setText(R.string.activity_settings_tracking_enabled);
            textTracking.setTextColor(getResources().getColor(R.color.light_bg_dark_secondary_text));
        } else {
            textTracking.setText(R.string.activity_settings_tracking_disabled);
            textTracking.setTextColor(getResources().getColor(R.color.red_500));
        }

        if (UserPrefs.SyncWithData)
            spinnerSyncMode.setSelection(0);
        else
            spinnerSyncMode.setSelection(1);

        spinnerSyncMode.setOnItemSelectedListener(getSyncModeItemSelectedListener());

        switchNotifications.setChecked(UserPrefs.NotificationsEnabled);
        switchNotifications.setOnCheckedChangeListener(getNotificationSwitchCheckedChangeListener());

        checkNotificationsNews.setChecked(UserPrefs.NewsNotificationsEnabled);
        checkNotificationsNews.setOnCheckedChangeListener(((compoundButton, b) -> {
            UserPrefs.NewsNotificationsEnabled = b;
            UserPrefs.getManager().setBool(UserPrefs.NEWS_NOTIFICATIONS, b);
        }));

        checkNotificationsMatch.setChecked(UserPrefs.MatchesNotificationsEnabled);
        checkNotificationsMatch.setOnCheckedChangeListener(((compoundButton, b) -> {
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
                    textTracking.setText(R.string.activity_settings_tracking_disabled);
                    textTracking.setTextColor(getResources().getColor(R.color.red_500));
                    // TODO bottomNavigation.setNotification("!", 4);
                }));
                builder.setNegativeButton(R.string.activity_settings_dialog_tracking_discard, (((dialogInterface, i) -> switchTracking.setChecked(true))));
                builder.show();

            } else {
                UserPrefs.TrackingEnabled = true;
                UserPrefs.getManager().setBool(UserPrefs.ACTIVITY_TRACKING, true);
                textTracking.setText(R.string.activity_settings_tracking_enabled);
                textTracking.setTextColor(getResources().getColor(R.color.light_bg_dark_secondary_text));
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
            checkNotificationsNews.setEnabled(b);
            checkNotificationsMatch.setEnabled(b);
        });
    }
}
