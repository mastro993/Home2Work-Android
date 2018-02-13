package it.gruppoinfor.home2work.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity

import com.arasthel.asyncjob.AsyncJob

import java.net.UnknownHostException

import butterknife.ButterKnife
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2work.user.UserPrefs
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.User

class SplashActivity : AppCompatActivity(), SessionManager.SessionCallback {

    private val FINE_LOCATION_ACCESS = 0
    private val SPLASH_TIME = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ButterKnife.bind(this)

        // Controllo dei permessi
        if (ActivityCompat.checkSelfPermission(this@SplashActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@SplashActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE), FINE_LOCATION_ACCESS)
        } else {
            initApp()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == FINE_LOCATION_ACCESS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                initApp()
            else
                finish()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onValidSession() {
        val user = HomeToWorkClient.getUser()
        if (user.isConfigured!!) {
            val i = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        } else {
            val i = Intent(this@SplashActivity, ConfigurationActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onInvalidSession(code: Int, throwable: Throwable?) {
        val intent: Intent
        when (code) {
            0 -> {
                intent = Intent(this@SplashActivity, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
            1 -> {
                intent = Intent(this@SplashActivity, SignInActivity::class.java)
                intent.putExtra(SessionManager.AUTH_CODE, SignInActivity.CODE_EXPIRED_TOKEN)
                startActivity(intent)
                finish()
            }
            2 -> {
                intent = Intent(this@SplashActivity, SignInActivity::class.java)
                if (throwable is UnknownHostException) {
                    intent.putExtra(SessionManager.AUTH_CODE, SignInActivity.CODE_NO_INTERNET)
                } else {
                    intent.putExtra(SessionManager.AUTH_CODE, SignInActivity.CODE_ERROR)
                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun initApp() {

        AsyncJob.doInBackground {

            // Carico preferenze utente
            UserPrefs.init(this@SplashActivity)

            // Mostro il logo per qualche istante
            try {
                Thread.sleep(SPLASH_TIME.toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            SessionManager.loadSession(this, this)


        }
    }

}
