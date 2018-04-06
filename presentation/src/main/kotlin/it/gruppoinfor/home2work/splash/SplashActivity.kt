package it.gruppoinfor.home2work.splash

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import it.gruppoinfor.home2work.main.MainActivity
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseActivity
import it.gruppoinfor.home2work.common.JobScheduler
import it.gruppoinfor.home2work.common.extensions.launchActivity
import it.gruppoinfor.home2work.common.extensions.showToast
import it.gruppoinfor.home2work.common.services.LocationService
import it.gruppoinfor.home2work.signin.SignInActivity
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject


class SplashActivity : BaseActivity<SplashViewModel, SplashVMFactory>() {

    val PERMISSION_FINE_LOCATION = 0

    @Inject
    lateinit var jobScheduler: JobScheduler

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
            jobScheduler.scheduleSyncJob(it.id)

            LocationService.launch(this, it.id)

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

}
