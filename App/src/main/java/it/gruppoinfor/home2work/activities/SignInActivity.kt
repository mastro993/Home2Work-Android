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
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.interfaces.LoginCallback
import it.gruppoinfor.home2workapi.model.User
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

                HomeToWorkClient.getInstance().login(email, password, false, this)

            }
        }

        loadEmail()
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

    override fun onLoginSuccess(user: User) {

        // Crashlytics log
        Answers.getInstance().logLogin(LoginEvent()
                .putMethod("Form")
                .putSuccess(true))

        Crashlytics.setUserIdentifier(user.id.toString())
        Crashlytics.setUserEmail(user.email)
        Crashlytics.setUserName(user.toString())

        Prefs.putString(PREFS_EMAIL, user.email)

        SessionManager.storeSession(this, HomeToWorkClient.user)

        val i = if (user.isConfigured) {
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

    private fun storeEmail() {

        val prefs = getSharedPreferences(PREFS_SIGNIN, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(PREFS_EMAIL, email_edit_text.text.toString())
        editor.apply()

    }

    private fun loadEmail() {

        val prefs = getSharedPreferences(PREFS_SIGNIN, Context.MODE_PRIVATE)
        val email = prefs.getString(PREFS_EMAIL, "")
        email_edit_text.setText(email)

    }

    companion object {
        private const val PREFS_EMAIL = "signin_email"
        private const val PREFS_SIGNIN = "it.home2work.app.signin"
    }

}
