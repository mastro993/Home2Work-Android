package it.gruppoinfor.home2work.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import com.pixplicity.easyprefs.library.Prefs
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.user.Const
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {

    private val trackingSwitchCheckedChangeListener: CompoundButton.OnCheckedChangeListener
        get() = CompoundButton.OnCheckedChangeListener { _, b ->
            if (!b) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.activity_settings_dialog_tracking_title)
                builder.setMessage(R.string.activity_settings_dialog_tracking_content)
                builder.setPositiveButton(R.string.activity_settings_dialog_tracking_confirm) { _, _ ->
                    Prefs.putBoolean(Const.PREF_ACTIVITY_TRACKING, b)
                    text_tracking.setText(R.string.activity_settings_tracking_disabled)
                    text_tracking.setTextColor(ContextCompat.getColor(this, R.color.red_500))
                }
                builder.setNegativeButton(R.string.activity_settings_dialog_tracking_discard) { _, _ -> switch_tracking.isChecked = true }
                builder.show()

            } else {
                Prefs.putBoolean(Const.PREF_ACTIVITY_TRACKING, b)
                text_tracking.setText(R.string.activity_settings_tracking_enabled)
                text_tracking.setTextColor(ContextCompat.getColor(this, R.color.light_bg_dark_secondary_text))
            }
        }

    private val syncModeItemSelectedListener: AdapterView.OnItemSelectedListener
        get() = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                Prefs.putBoolean(Const.PREF_SYNC_WITH_DATA, i != 1)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }

    private val notificationSwitchCheckedChangeListener: CompoundButton.OnCheckedChangeListener
        get() = CompoundButton.OnCheckedChangeListener { _, b ->
            Prefs.putBoolean(Const.PREF_NOTIFICATIONS, b)
            check_notifications_match.isEnabled = b
            check_notifications_news.isEnabled = b
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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
        switch_tracking.setOnCheckedChangeListener(trackingSwitchCheckedChangeListener)
        if (Prefs.getBoolean(Const.PREF_ACTIVITY_TRACKING, true)) {
            switch_tracking.isChecked = true
            text_tracking.setText(R.string.activity_settings_tracking_enabled)
            text_tracking.setTextColor(ContextCompat.getColor(this, R.color.light_bg_dark_secondary_text))
        } else {
            switch_tracking.isChecked = false
            text_tracking.setText(R.string.activity_settings_tracking_disabled)
            text_tracking.setTextColor(ContextCompat.getColor(this, R.color.red_500))
        }

        if (Prefs.getBoolean(Const.PREF_SYNC_WITH_DATA, true))
            spinner_sync_mode.setSelection(0)
        else
            spinner_sync_mode.setSelection(1)
        spinner_sync_mode.onItemSelectedListener = syncModeItemSelectedListener

        switch_notifications.isChecked = Prefs.getBoolean(Const.PREF_NOTIFICATIONS, true)
        switch_notifications.setOnCheckedChangeListener(notificationSwitchCheckedChangeListener)

        check_notifications_news.isChecked = Prefs.getBoolean(Const.PREF_NOTIFICATIONS_NEWS, true)
        check_notifications_news.setOnCheckedChangeListener { _, b ->
            Prefs.putBoolean(Const.PREF_NOTIFICATIONS_NEWS, b)
        }

        check_notifications_match.isChecked = Prefs.getBoolean(Const.PREF_NOTIFICATIONS_MATCHES, true)
        check_notifications_match.setOnCheckedChangeListener { _, b ->
            Prefs.putBoolean(Const.PREF_NOTIFICATIONS_MATCHES, b)
        }

    }
}
