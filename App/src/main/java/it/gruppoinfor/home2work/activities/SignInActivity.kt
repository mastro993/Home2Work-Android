package it.gruppoinfor.home2work.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import java.net.UnknownHostException

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.interfaces.LoginCallback
import it.gruppoinfor.home2workapi.model.User

class SignInActivity : AppCompatActivity(), LoginCallback {

    @BindView(R.id.sign_in_button)
    internal var signInButton: Button? = null
    @BindView(R.id.lost_password_button)
    internal var lostPasswordButton: TextView? = null
    @BindView(R.id.email_edit_text)
    internal var emailEditText: EditText? = null
    @BindView(R.id.password_edit_text)
    internal var passwordEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        ButterKnife.bind(this)
        loadEmail()
    }

    override fun onStart() {
        super.onStart()
        val bundle = intent.extras
        if (bundle != null) {

            when (bundle.getInt(SessionManager.AUTH_CODE)) {
                CODE_ERROR -> Toasty.error(this, getString(R.string.activity_signin_error), Toast.LENGTH_SHORT, true).show()
                CODE_EXPIRED_TOKEN -> Toasty.warning(this, getString(R.string.activity_signin_session_expired), Toast.LENGTH_SHORT, true).show()
                CODE_NO_INTERNET -> Toasty.error(this, getString(R.string.activity_signin_no_internet), Toast.LENGTH_SHORT, true).show()
            }

        }

    }

    override fun onLoginSuccess() {
        storeEmail()

        SessionManager.storeSession(this, HomeToWorkClient.getUser())
        val user = HomeToWorkClient.getUser()

        if (user.isConfigured!!) {

            val i = Intent(this@SignInActivity, MainActivity::class.java)
            startActivity(i)
            finish()

        } else {

            val i = Intent(this@SignInActivity, ConfigurationActivity::class.java)
            startActivity(i)
            finish()

        }
    }

    override fun onInvalidCredential() {
        enableLogin()
        Toasty.error(this@SignInActivity, getString(R.string.activity_signin_login_failed), Toast.LENGTH_SHORT, true).show()
    }

    override fun onLoginError() {
        enableLogin()
        Toasty.error(this@SignInActivity, getString(R.string.activity_signin_server_error), Toast.LENGTH_SHORT, true).show()
    }

    override fun onError(throwable: Throwable) {
        enableLogin()
        if (throwable is UnknownHostException) {
            Toasty.error(this@SignInActivity, getString(R.string.activity_signin_no_internet), Toast.LENGTH_SHORT, true).show()
        } else {
            Toasty.error(this@SignInActivity, getString(R.string.activity_signin_server_error), Toast.LENGTH_SHORT, true).show()
        }
    }

    @OnClick(R.id.sign_in_button)
    internal fun signInButtonClick() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        if (!validate())
            return

        emailEditText!!.isEnabled = false
        passwordEditText!!.isEnabled = false

        signInButton!!.isEnabled = false
        signInButton!!.text = "Accesso in corso"

        val email = emailEditText!!.text.toString()
        val password = passwordEditText!!.text.toString()

        HomeToWorkClient.getInstance().login(email, password, false, this)

    }

    private fun enableLogin() {
        signInButton!!.isEnabled = true
        signInButton!!.text = "Accedi"
        emailEditText!!.isEnabled = true
        passwordEditText!!.isEnabled = true
    }

    private fun validate(): Boolean {

        var valid = true

        val email = emailEditText!!.text.toString()
        val password = passwordEditText!!.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText!!.error = getString(R.string.activity_signin_email_error)
            valid = false
        } else {
            passwordEditText!!.error = null
        }

        if (password.isEmpty() || password.length < 6) {
            passwordEditText!!.error = getString(R.string.activity_signin_password_error)
            valid = false
        } else {
            passwordEditText!!.error = null
        }

        return valid
    }

    private fun storeEmail() {
        val prefs = getSharedPreferences(PREFS_SIGNIN, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(PREFS_EMAIL, emailEditText!!.text.toString())
        editor.apply()
    }

    private fun loadEmail() {
        val prefs = getSharedPreferences(PREFS_SIGNIN, Context.MODE_PRIVATE)
        val email = prefs.getString(PREFS_EMAIL, "")
        emailEditText!!.setText(email)

    }

    companion object {

        val CODE_EXPIRED_TOKEN = 0
        val CODE_ERROR = 1
        val CODE_NO_INTERNET = 2
        private val PREFS_EMAIL = "signin_email"
        private val PREFS_SIGNIN = "it.home2work.app.signin"
    }

}
