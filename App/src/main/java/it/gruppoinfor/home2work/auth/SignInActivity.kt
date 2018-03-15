package it.gruppoinfor.home2work.auth

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
import it.gruppoinfor.home2work.MainActivity
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.configuration.ConfigurationActivity
import it.gruppoinfor.home2work.firebase.FirebaseTokenService
import it.gruppoinfor.home2work.tracking.SyncJobService
import it.gruppoinfor.home2workapi.HomeToWorkClient
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity(), SignInView {

    private var mSignInPresenter: SignInPresenter = SignInPresenterImpl(this)

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

                mSignInPresenter.login(email, password)

            }
        }

        email_edit_text.setText(Prefs.getString(PREFS_EMAIL, ""))
    }

    override fun onResume() {
        super.onResume()
        mSignInPresenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        mSignInPresenter.onPause()
    }

    override fun onStart() {
        super.onStart()

        when (intent.extras?.getInt(CODE_AUTH)) {
            CODE_ERROR ->
                Toast.makeText(this, R.string.activity_signin_error, Toast.LENGTH_SHORT).show()
            CODE_EXPIRED_TOKEN ->
                Toast.makeText(this, R.string.activity_signin_session_expired, Toast.LENGTH_SHORT).show()
            CODE_NO_INTERNET ->
                Toast.makeText(this, R.string.activity_signin_no_internet, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onLoginSuccess() {
        SyncJobService.schedule(this, HomeToWorkClient.user!!.id)

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

        Prefs.putString(PREFS_EMAIL, HomeToWorkClient.user?.email)

        SessionManager.storeSession(this, HomeToWorkClient.user)

        val i = if (HomeToWorkClient.user!!.configured) {
            Intent(this@SignInActivity, MainActivity::class.java)
        } else {
            Intent(this@SignInActivity, ConfigurationActivity::class.java)
        }

        startActivity(i)
        finish()
    }

    override fun onError() {
        // Crashlytics log
        Answers.getInstance().logLogin(LoginEvent()
                .putMethod("Form")
                .putSuccess(false))

        enableLogin()
    }

    override fun showErrorMessage(errorMessage: String) {
        Toast.makeText(this@SignInActivity, errorMessage, Toast.LENGTH_SHORT).show()
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

    companion object {
        const val PREFS_EMAIL = "signin_email"
        const val CODE_AUTH = "CODE_AUTH"
        const val CODE_EXPIRED_TOKEN = 1
        const val CODE_ERROR = 2
        const val CODE_NO_INTERNET = 3
        const val CODE_INVALID_CREDENTIALS = 4
        const val CODE_LOGIN_ERROR = 5
        const val CODE_SERVER_ERROR = 6
        const val CODE_NO_ACCESS_TOKEN = 7
    }


}
