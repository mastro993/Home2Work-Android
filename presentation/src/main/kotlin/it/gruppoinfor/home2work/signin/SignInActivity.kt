package it.gruppoinfor.home2work.signin

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import it.gruppoinfor.home2work.MainActivity
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.JobScheduler
import it.gruppoinfor.home2work.common.extensions.launchActivity
import it.gruppoinfor.home2work.common.extensions.showToast
import it.gruppoinfor.home2work.common.services.LocationService
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.di.DipendencyInjector
import kotlinx.android.synthetic.main.activity_sign_in.*
import javax.inject.Inject


class SignInActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: SignInVMFactory
    @Inject
    lateinit var jobScheduler: JobScheduler
    @Inject
    lateinit var localUserData: LocalUserData

    private lateinit var viewModel: SignInViewModel

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signInButton: Button
    private lateinit var lostPasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        DipendencyInjector.createSignInComponent().inject(this)
        viewModel = ViewModelProvider(this, factory).get(SignInViewModel::class.java)

        emailInput = email_input
        passwordInput = password_input
        signInButton = sign_in_button
        lostPasswordButton = lost_password_button

        signInButton.setOnClickListener {
            val view = this.currentFocus
            if (view != null) {

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)

            }

            if (validate()) {

                val email = emailInput.text.toString()
                val password = passwordInput.text.toString()

                viewModel.login(email, password)

            }
        }

        viewModel.loadSavedEmail()
        observeViewState()

    }

    override fun onDestroy() {
        super.onDestroy()
        DipendencyInjector.releaseSignInComponent()
    }

    private fun observeViewState() {

        viewModel.errorState.observe(this, Observer {
            it?.let {
                showToast(it)
            }
        })
        viewModel.loginSuccessState.observe(this, Observer {
            it?.let { if (it) onLoginSuccess() }
        })
        viewModel.viewState.observe(this, Observer {
            handleViewState(it)
        })


    }

    private fun handleViewState(state: SignInViewState?) {

        state?.let {

            it.savedEmail?.let { emailInput.setText(it) }

            emailInput.isEnabled = !it.isLoading
            emailInput.clearFocus()

            passwordInput.isEnabled = !it.isLoading
            passwordInput.clearFocus()

            signInButton.isEnabled = !it.isLoading
            signInButton.text = if (it.isLoading) "Accesso in corso" else "Accedi"


        }

    }

    private fun onLoginSuccess() {

        localUserData.user?.let {
            jobScheduler.scheduleSyncJob(it.id)

            LocationService.launch(this, it.id)

            launchActivity<MainActivity> {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            finish()

        } ?: showToast("Errore imprevisto")

    }


    private fun validate(): Boolean {

        var valid = true

        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = getString(R.string.activity_signin_email_error)
            valid = false
        } else {
            emailInput.error = null
        }

        if (password.isEmpty() || password.length < 6) {
            passwordInput.error = getString(R.string.activity_signin_password_error)
            valid = false
        } else {
            passwordInput.error = null
        }

        return valid
    }


}
