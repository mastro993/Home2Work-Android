package it.gruppoinfor.home2work.splash

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.ActivityCompat
import it.gruppoinfor.home2work.MainActivity
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseActivity
import it.gruppoinfor.home2work.common.JobScheduler
import it.gruppoinfor.home2work.common.extensions.launchActivity
import it.gruppoinfor.home2work.common.extensions.remove
import it.gruppoinfor.home2work.common.extensions.show
import it.gruppoinfor.home2work.common.extensions.showToast
import it.gruppoinfor.home2work.common.services.LocationService
import it.gruppoinfor.home2work.signin.SignInActivity
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject


class SplashActivity : BaseActivity<SplashViewModel, SplashVMFactory, SplashViewState>() {

    private val PERMISSION_FINE_LOCATION = 0

    @Inject
    lateinit var jobScheduler: JobScheduler

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(R.layout.activity_splash, savedInstanceState, persistentState)

        viewModel.errorState.observe(this, Observer {
            it?.let {
                showToast(it)
            }
        })
        viewModel.loginState.observe(this, Observer {
            it?.let { if (it) onLoginSuccess() }
        })
    }

    override fun initUI() {
        b_signin.setOnClickListener {
            launchActivity<SignInActivity>()
        }

        // Controllo dei permessi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_FINE_LOCATION)
        } else {
            viewModel.tokenLogin()
        }

    }

    override fun observeViewState(viewState: SplashViewState?) {
        viewState?.let {

            pb_loading.apply {
                if (it.isLoading) show() else remove()
            }

            b_signin.apply {
                if (it.showSignInButton) show() else remove()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == PERMISSION_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish()
            } else {
                viewModel.tokenLogin()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun onLoginSuccess() {

        localUserData.user?.let {
            jobScheduler.scheduleSyncJob(it.id)

            LocationService.launch(
                    context = this,
                    userId = it.id)

            launchActivity<MainActivity> {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            finish()

        } ?: showToast("Errore imprevisto")

    }

}
