package it.gruppoinfor.home2work.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseActivity
import it.gruppoinfor.home2work.common.user.SettingsPreferences
import it.gruppoinfor.home2work.splash.SplashActivity
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import javax.inject.Inject


class SettingsActivity : BaseActivity<SettingsViewModel, SettingsVMFactory>() {

    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    override fun getVMClass(): Class<SettingsViewModel> {
        return SettingsViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

        vacancy_mode_switch.isChecked = settingsPreferences.vacancyModeEnabled

        vacancy_mode_switch.setOnCheckedChangeListener { _, isChecked ->
            settingsPreferences.vacancyModeEnabled = isChecked
        }

        vacancy_mode_title.setOnClickListener {

            if(!settingsPreferences.vacancyModeEnabled){
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Modalità vacanza")
                builder.setMessage("Abilitando la modalità vacanza la tua attività non verrà monitorata e le notifiche saranno disabilitate")
                builder.setPositiveButton("Attiva") { _, _ ->

                    vacancy_mode_switch.isChecked = true

                }
                builder.setNegativeButton(R.string.dialog_logout_decline, null)
                builder.show()
            } else {
                vacancy_mode_switch.isChecked = false
            }

        }


        about_button.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.home2work.it/")))
        }

        button_logout.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.dialog_logout_title)
            builder.setMessage(R.string.dialog_logout_content)
            builder.setPositiveButton(R.string.dialog_logout_confirm) { _, _ ->

                localUserData.clear()
                startActivity(intentFor<SplashActivity>().clearTask())

            }
            builder.setNegativeButton(R.string.dialog_logout_decline, null)
            builder.show()

        }


    }


}
