package it.gruppoinfor.home2work.splash

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import com.google.firebase.iid.FirebaseInstanceId
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseActivity
import it.gruppoinfor.home2work.common.extensions.launchActivity
import it.gruppoinfor.home2work.common.extensions.showToast
import it.gruppoinfor.home2work.domain.usecases.DeleteUserLocations
import it.gruppoinfor.home2work.domain.usecases.GetUserLocations
import it.gruppoinfor.home2work.domain.usecases.StoreUserFCMToken
import it.gruppoinfor.home2work.domain.usecases.SyncUserLocations
import it.gruppoinfor.home2work.main.MainActivity
import it.gruppoinfor.home2work.services.LocationService
import it.gruppoinfor.home2work.signin.SignInActivity
import kotlinx.android.synthetic.main.activity_splash.*
import timber.log.Timber
import javax.inject.Inject


class SplashActivity : BaseActivity<SplashViewModel, SplashVMFactory>() {

    val PERMISSION_FINE_LOCATION = 0

    @Inject
    lateinit var getUserLocations: GetUserLocations
    @Inject
    lateinit var syncUserLocation: SyncUserLocations
    @Inject
    lateinit var deleteUserLocations: DeleteUserLocations
    @Inject
    lateinit var storeUserFCMToken: StoreUserFCMToken

    override fun getVMClass(): Class<SplashViewModel> {
        return SplashViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        b_signin.setOnClickListener {
            launchActivity<SignInActivity>()
        }

        observeViewState()

        // Controllo dei permessi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_FINE_LOCATION)
        } else {
            viewModel.tokenLogin()
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == PERMISSION_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish()
            } else {
                viewModel.tokenLogin()
                observeViewState()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun observeViewState() {

        viewModel.errorState.observe(this, Observer {
            it?.let {
                showToast(it)
            }
        })
        viewModel.loginState.observe(this, Observer {
            it?.let { if (it) onLoginSuccess() }
        })
        viewModel.viewState.observe(this, Observer { handleViewState(it) })


    }

    private fun onLoginSuccess() {
        localUserData.user?.let {

            LocationService.launch(this)
            updateFirebaseToken()

            launchActivity<MainActivity> {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            finish()

        } ?: showToast("Errore imprevisto")

    }

    private fun handleViewState(state: SplashViewState?) {

        state?.let {
            pb_loading.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            b_signin.visibility = if (it.showSignInButton) View.VISIBLE else View.GONE

        }

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
