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
import it.gruppoinfor.home2work.signin.SignInActivity
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor


class SettingsActivity : AppCompatActivity() {


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

        button_logout.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.dialog_logout_title)
            builder.setMessage(R.string.dialog_logout_content)
            builder.setPositiveButton(R.string.dialog_logout_confirm) { _, _ ->

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
