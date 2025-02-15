package it.gruppoinfor.home2work.signin

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.google.firebase.iid.FirebaseInstanceId
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseActivity
import it.gruppoinfor.home2work.common.extensions.*
import it.gruppoinfor.home2work.domain.usecases.StoreUserFCMToken
import it.gruppoinfor.home2work.main.MainActivity
import it.gruppoinfor.home2work.services.LocationService
import it.gruppoinfor.home2work.services.SyncWorker
import kotlinx.android.synthetic.main.activity_sign_in.*
import timber.log.Timber
import javax.inject.Inject


class SignInActivity : BaseActivity<SignInViewModel, SignInVMFactory>() {


    @Inject
    lateinit var storeUserFCMToken: StoreUserFCMToken

    override fun getVMClass(): Class<SignInViewModel> {
        return SignInViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sign_in_button.setOnClickListener {
            val view = this.currentFocus
            if (view != null) {

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)

            }

            if (validate()) {

                val email = email_input.text.toString()
                val password = password_input.text.toString()

                viewModel.login(email, password)

            }
        }

        viewModel.loadLastUsedEmail()
        observeViewState()

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

    private fun observeViewState() {

        viewModel.errorState.observe(this, Observer { error ->
            error?.let {
                showToast(it)
            }
        })
        viewModel.loginSuccessState.observe(this, Observer { success ->
            success?.let {
                if (it)
                    onLoginSuccess()
            }
        })
        viewModel.viewState.observe(this, Observer {
            handleViewState(it)
        })


    }

    private fun handleViewState(state: SignInViewState?) {

        state?.let { it ->

            it.savedEmail?.let { email_input.setText(it) }

            email_input.isEnabled = !it.isLoading
            email_input.clearFocus()

            password_input.isEnabled = !it.isLoading
            password_input.clearFocus()

            if (it.isLoading) {
                progressbar_loading.show()
                sign_in_button.hide()
            } else {
                progressbar_loading.hide()
                sign_in_button.show()
            }

            //sign_in_button.text = if (it.isLoading) "Accesso in corso" else "Accedi"


        }

    }

    private fun onLoginSuccess() {

        localUserData.user?.let {

            //jobScheduler.scheduleSyncJob(it.id)
            SyncWorker.schedule(it.id)
            LocationService.launch(this)
            updateFirebaseToken()

            viewModel.storeUsedEmail(email_input.text.toString())

            launchActivity<MainActivity> {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            finish()

        } ?: showToast("Errore imprevisto")

    }


    private fun validate(): Boolean {

        var valid = true

        val email = email_input.text.toString()
        val password = password_input.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_input.error = getString(R.string.activity_signin_email_error)
            valid = false
        } else {
            email_input.error = null
        }

        if (password.isEmpty() || password.length < 6) {
            password_input.error = getString(R.string.activity_signin_password_error)
            valid = false
        } else {
            password_input.error = null
        }

        return valid
    }

    private fun updateFirebaseToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            val savedToken = localUserData.firebaseToken
            val newToken = it.token
            if (newToken != savedToken) {
                storeUserFCMToken.store(newToken)
                        .subscribe({
                            Timber.d("Token Firebase aggiornato")
                            localUserData.firebaseToken = newToken
                        }, {
                            Timber.e("Impossibile aggiornare il token Firebase")
                        })
            }
        }
    }


}
