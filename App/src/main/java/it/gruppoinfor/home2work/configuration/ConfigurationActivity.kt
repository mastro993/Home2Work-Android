package it.gruppoinfor.home2work.configuration

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Toast
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.MainActivity
import it.gruppoinfor.home2work.auth.SessionManager
import it.gruppoinfor.home2work.auth.SignInActivity
import kotlinx.android.synthetic.main.activity_configuration.*

class ConfigurationActivity : AppCompatActivity(), StepperLayout.StepperListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_configuration)
        initUI()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_configuration, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.dialog_logout_title)
                builder.setMessage(R.string.dialog_logout_content)
                builder.setPositiveButton(R.string.dialog_logout_confirm) { _, _ ->

                    Answers.getInstance().logCustom(CustomEvent("Configurazione annullata"))

                    SessionManager.clearSession(this@ConfigurationActivity)
                    val intent = Intent(this@ConfigurationActivity, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()

                }
                builder.setNegativeButton(R.string.dialog_logout_decline, null)
                builder.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCompleted(completeButton: View) {

        Answers.getInstance().logCustom(CustomEvent("Configurazione completata"))

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onError(verificationError: VerificationError) {

        Toast.makeText(this, verificationError.errorMessage, Toast.LENGTH_SHORT).show()

    }

    override fun onStepSelected(newStepPosition: Int) {
        //Toast.makeText(this, "onStepSelected! -> " + newStepPosition, Toast.LENGTH_SHORT).show();
    }

    override fun onReturn() {
        finish()
    }

    private fun initUI() {

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        stepperLayout.adapter = ConfigurationStepsAdapter(supportFragmentManager, this)
        stepperLayout.setOffscreenPageLimit(5)
        stepperLayout.setListener(this)

    }

}
