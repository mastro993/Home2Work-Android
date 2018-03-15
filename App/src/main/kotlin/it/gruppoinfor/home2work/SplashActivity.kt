package it.gruppoinfor.home2work

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import it.gruppoinfor.home2work.api.HomeToWorkClient
import it.gruppoinfor.home2work.auth.SessionManager
import it.gruppoinfor.home2work.auth.SignInActivity
import it.gruppoinfor.home2work.configuration.ConfigurationActivity
import it.gruppoinfor.home2work.firebase.FirebaseTokenService
import it.gruppoinfor.home2work.main.MainActivity
import it.gruppoinfor.home2work.tracking.SyncJobService
import java.net.UnknownHostException


class SplashActivity : AppCompatActivity(), SessionManager.SessionCallback {

    val PERMISSION_FINE_LOCATION = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        // Controllo dei permessi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_FINE_LOCATION)
        } else {
            SessionManager.loadSession(this, this)
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == PERMISSION_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                SessionManager.loadSession(this, this)
            else
                finish()
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onValidSession() {


        SyncJobService.schedule(this@SplashActivity, HomeToWorkClient.user!!.id)
        FirebaseTokenService.sync(FirebaseInstanceId.getInstance().token)

        val i: Intent = if (HomeToWorkClient.user!!.configured) {
            Intent(this@SplashActivity, MainActivity::class.java)
        } else {
            Intent(this@SplashActivity, ConfigurationActivity::class.java)
        }
        startActivity(i)
        finish()

    }

    override fun onInvalidSession(code: Int, throwable: Throwable?) {

        val intent = Intent(this@SplashActivity, SignInActivity::class.java)
        when (code) {
            SignInActivity.CODE_INVALID_CREDENTIALS -> intent.putExtra(SignInActivity.CODE_AUTH, SignInActivity.CODE_EXPIRED_TOKEN)
            SignInActivity.CODE_NO_ACCESS_TOKEN -> {
            }
            else ->
                if (throwable is UnknownHostException)
                    intent.putExtra(SignInActivity.CODE_AUTH, SignInActivity.CODE_NO_INTERNET)
                else
                    intent.putExtra(SignInActivity.CODE_AUTH, SignInActivity.CODE_ERROR)
        }
        startActivity(intent)
        finish()

    }


}
