package it.gruppoinfor.home2work.activities

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.user.UserPrefs


class SettingsActivity : AppCompatActivity() {


    @BindView(R.id.text_tracking)
    internal var textTracking: TextView? = null
    @BindView(R.id.switch_tracking)
    internal var switchTracking: Switch? = null
    @BindView(R.id.spinner_sync_mode)
    internal var spinnerSyncMode: Spinner? = null
    @BindView(R.id.switch_notifications)
    internal var switchNotifications: Switch? = null
    @BindView(R.id.check_notifications_news)
    internal var checkNotificationsNews: CheckBox? = null
    @BindView(R.id.check_notifications_match)
    internal var checkNotificationsMatch: CheckBox? = null

    private// TODO bottomNavigation.setNotification("!", 4);
            // TODO bottomNavigation.setNotification("", 4);
    val trackingSwitchCheckedChangeListener: CompoundButton.OnCheckedChangeListener
        get() = { compoundButton, b ->
            if (!b) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.activity_settings_dialog_tracking_title)
                builder.setMessage(R.string.activity_settings_dialog_tracking_content)
                builder.setPositiveButton(R.string.activity_settings_dialog_tracking_confirm) { dialogInterface, i ->
                    UserPrefs.TrackingEnabled = false
                    UserPrefs.manager!!.setBool(UserPrefs.ACTIVITY_TRACKING, false)
                    textTracking!!.setText(R.string.activity_settings_tracking_disabled)
                    textTracking!!.setTextColor(resources.getColor(R.color.red_500))
                }
                builder.setNegativeButton(R.string.activity_settings_dialog_tracking_discard) { dialogInterface, i -> switchTracking!!.isChecked = true }
                builder.show()

            } else {
                UserPrefs.TrackingEnabled = true
                UserPrefs.manager!!.setBool(UserPrefs.ACTIVITY_TRACKING, true)
                textTracking!!.setText(R.string.activity_settings_tracking_enabled)
                textTracking!!.setTextColor(resources.getColor(R.color.light_bg_dark_secondary_text))
            }
        }

    private val syncModeItemSelectedListener: AdapterView.OnItemSelectedListener
        get() = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                if (i == 1) {
                    UserPrefs.manager!!.setBool(UserPrefs.SYNC_WITH_DATA, false)
                    UserPrefs.SyncWithData = false
                } else {
                    UserPrefs.manager!!.setBool(UserPrefs.SYNC_WITH_DATA, true)
                    UserPrefs.SyncWithData = true
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }

    private val notificationSwitchCheckedChangeListener: CompoundButton.OnCheckedChangeListener
        get() = { compoundButton, b ->
            UserPrefs.NotificationsEnabled = b
            UserPrefs.manager!!.setBool(UserPrefs.NOTIFICATIONS, b)
            checkNotificationsNews!!.isEnabled = b
            checkNotificationsMatch!!.isEnabled = b
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ButterKnife.bind(this)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        initUI()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initUI() {
        switchTracking!!.isChecked = UserPrefs.TrackingEnabled
        switchTracking!!.setOnCheckedChangeListener(trackingSwitchCheckedChangeListener)
        if (UserPrefs.TrackingEnabled) {
            textTracking!!.setText(R.string.activity_settings_tracking_enabled)
            textTracking!!.setTextColor(resources.getColor(R.color.light_bg_dark_secondary_text))
        } else {
            textTracking!!.setText(R.string.activity_settings_tracking_disabled)
            textTracking!!.setTextColor(resources.getColor(R.color.red_500))
        }

        if (UserPrefs.SyncWithData)
            spinnerSyncMode!!.setSelection(0)
        else
            spinnerSyncMode!!.setSelection(1)

        spinnerSyncMode!!.onItemSelectedListener = syncModeItemSelectedListener

        switchNotifications!!.isChecked = UserPrefs.NotificationsEnabled
        switchNotifications!!.setOnCheckedChangeListener(notificationSwitchCheckedChangeListener)

        checkNotificationsNews!!.isChecked = UserPrefs.NewsNotificationsEnabled
        checkNotificationsNews!!.setOnCheckedChangeListener { compoundButton, b ->
            UserPrefs.NewsNotificationsEnabled = b
            UserPrefs.manager!!.setBool(UserPrefs.NEWS_NOTIFICATIONS, b)
        }

        checkNotificationsMatch!!.isChecked = UserPrefs.MatchesNotificationsEnabled
        checkNotificationsMatch!!.setOnCheckedChangeListener { compoundButton, b ->
            UserPrefs.MatchesNotificationsEnabled = b
            UserPrefs.manager!!.setBool(UserPrefs.MATCHES_NOTIFICATIONS, b)
        }

    }
}
