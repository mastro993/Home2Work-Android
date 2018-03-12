package it.gruppoinfor.home2work.settings

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import com.crashlytics.android.Crashlytics
import com.pixplicity.easyprefs.library.Prefs
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.auth.SessionManager
import it.gruppoinfor.home2work.auth.SignInActivity
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor


class SettingsActivity : AppCompatActivity() {

    private val trackingSwitchCheckedChangeListener: CompoundButton.OnCheckedChangeListener
        get() = CompoundButton.OnCheckedChangeListener { _, b ->

            Crashlytics.setBool("Tracking attivo", b)

            if (!b) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.activity_settings_dialog_tracking_title)
                builder.setMessage(R.string.activity_settings_dialog_tracking_content)
                builder.setPositiveButton(R.string.activity_settings_dialog_tracking_confirm) { _, _ ->
                    Prefs.putBoolean(PREFS_ACTIVITY_TRACKING, b)
                    text_tracking.setText(R.string.activity_settings_tracking_disabled)
                    text_tracking.setTextColor(ContextCompat.getColor(this, R.color.red_500))
                }
                builder.setNegativeButton(R.string.activity_settings_dialog_tracking_discard) { _, _ -> switch_tracking.isChecked = true }
                builder.show()

            } else {
                Prefs.putBoolean(PREFS_ACTIVITY_TRACKING, b)
                text_tracking.setText(R.string.activity_settings_tracking_enabled)
                text_tracking.setTextColor(ContextCompat.getColor(this, R.color.light_bg_dark_secondary_text))
            }

        }

    private val syncModeItemSelectedListener: AdapterView.OnItemSelectedListener
        get() = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

                Crashlytics.setBool("Data sync", i != 1)

                Prefs.putBoolean(PREFS_SYNC_WITH_DATA, i != 1)

            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }

    private val notificationSwitchCheckedChangeListener: CompoundButton.OnCheckedChangeListener
        get() = CompoundButton.OnCheckedChangeListener { _, b ->

            Crashlytics.setBool("Notifiche abilitate", b)

            Prefs.putBoolean(PREFS_NOTIFICATIONS, b)

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
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initUI() {

        switch_tracking.setOnCheckedChangeListener(trackingSwitchCheckedChangeListener)
        if (Prefs.getBoolean(PREFS_ACTIVITY_TRACKING, true)) {
            switch_tracking.isChecked = true
            text_tracking.setText(R.string.activity_settings_tracking_enabled)
            text_tracking.setTextColor(ContextCompat.getColor(this, R.color.light_bg_dark_secondary_text))
        } else {
            switch_tracking.isChecked = false
            text_tracking.setText(R.string.activity_settings_tracking_disabled)
            text_tracking.setTextColor(ContextCompat.getColor(this, R.color.red_500))
        }

        switch_notifications.isChecked = Prefs.getBoolean(PREFS_NOTIFICATIONS, true)
        switch_notifications.setOnCheckedChangeListener(notificationSwitchCheckedChangeListener)

        button_logout.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.dialog_logout_title)
            builder.setMessage(R.string.dialog_logout_content)
            builder.setPositiveButton(R.string.dialog_logout_confirm) { _, _ ->

                SessionManager.clearSession(this)
                startActivity(intentFor<SignInActivity>().clearTask())

            }
            builder.setNegativeButton(R.string.dialog_logout_decline, null)
            builder.show()

        }


    }

    companion object {
        const val PREFS_NOTIFICATIONS = "NOTIFICATIONS"
        const val PREFS_NOTIFICATIONS_NEWS = "NOTIFICATIONS_NEWS"
        const val PREFS_NOTIFICATIONS_MSG = "NOTIFICATIONS_MSG"
        const val PREFS_NOTIFICATIONS_MATCHES = "NOTIFICATIONS_MATCHES"
        const val PREFS_ACTIVITY_TRACKING = "ACTIVITY_TRACKING"
        const val PREFS_SYNC_WITH_DATA = "SYNC_WITH_DATA"
        const val PREFS_LAST_SYNC = "LAST_SYNC"
    }
}
