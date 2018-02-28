package it.gruppoinfor.home2work.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.pixplicity.easyprefs.library.Prefs
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.services.FirebaseTokenService
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.callback.LoginCallback
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.net.UnknownHostException


class SignInActivity : AppCompatActivity(), LoginCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sign_in)

        sign_in_button.setOnClickListener {
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

            if (validate()) {

                email_edit_text.isEnabled = false
                password_edit_text.isEnabled = false

                sign_in_button.visibility = View.INVISIBLE

                val email = email_edit_text.text.toString()
                val password = password_edit_text.text.toString()

                HomeToWorkClient.login(email, password, this)

            }
        }

        email_edit_text.setText(Prefs.getString(Const.PREFS_EMAIL, ""))
    }

    override fun onStart() {
        super.onStart()

        when (intent.extras?.getInt(Const.CODE_AUTH)) {
            Const.CODE_ERROR ->
                Toast.makeText(this, R.string.activity_signin_error, Toast.LENGTH_SHORT).show()
            Const.CODE_EXPIRED_TOKEN ->
                Toast.makeText(this, R.string.activity_signin_session_expired, Toast.LENGTH_SHORT).show()
            Const.CODE_NO_INTERNET ->
                Toast.makeText(this, R.string.activity_signin_no_internet, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onLoginSuccess() {

        // Crashlytics log
        Answers.getInstance().logLogin(LoginEvent()
                .putMethod("Form")
                .putSuccess(true))

        // Salvo i dati dell'utente per crashalytics
        Crashlytics.setUserIdentifier(HomeToWorkClient.user?.id.toString())
        Crashlytics.setUserEmail(HomeToWorkClient.user?.email)
        Crashlytics.setUserName(HomeToWorkClient.user.toString())

        // Aggiorno il token Firebase Cloud Messaging sul server
        FirebaseTokenService().onTokenRefresh()

        Prefs.putString(Const.PREFS_EMAIL, HomeToWorkClient.user?.email)

        SessionManager.storeSession(this, HomeToWorkClient.user)

        val i = if (HomeToWorkClient.user!!.configured) {
            Intent(this@SignInActivity, MainActivity::class.java)
        } else {
            Intent(this@SignInActivity, ConfigurationActivity::class.java)
        }

        startActivity(i)
        finish()

    }

    override fun onInvalidCredential() {

        enableLogin()
        Toast.makeText(this@SignInActivity, R.string.activity_signin_login_failed, Toast.LENGTH_SHORT).show()

    }

    override fun onError(throwable: Throwable?) {

        // Crashlytics log
        Answers.getInstance().logLogin(LoginEvent()
                .putMethod("Form")
                .putSuccess(false))

        enableLogin()
        if (throwable is UnknownHostException) {
            Toast.makeText(this@SignInActivity, R.string.activity_signin_no_internet, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@SignInActivity, R.string.activity_signin_server_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableLogin() {

        sign_in_button.visibility = View.VISIBLE
        email_edit_text.isEnabled = true
        password_edit_text.isEnabled = true

    }

    private fun validate(): Boolean {

        var valid = true

        val email = email_edit_text.text.toString()
        val password = password_edit_text.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_edit_text.error = getString(R.string.activity_signin_email_error)
            valid = false
        } else {
            password_edit_text.error = null
        }

        if (password.isEmpty() || password.length < 6) {
            password_edit_text.error = getString(R.string.activity_signin_password_error)
            valid = false
        } else {
            password_edit_text.error = null
        }

        return valid
    }



}
