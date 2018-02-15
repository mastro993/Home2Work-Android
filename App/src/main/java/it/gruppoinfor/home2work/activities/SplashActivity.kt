package it.gruppoinfor.home2work.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.arasthel.asyncjob.AsyncJob
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.user.Const
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2workapi.model.User
import java.net.UnknownHostException

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Controllo dei permessi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE), Const.PERMISSION_FINE_LOCATION)
        } else {
            initApp()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Const.PERMISSION_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                initApp()
            else
                finish()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun initApp() {

        AsyncJob.doInBackground {

            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            SessionManager.loadSession(this, object : SessionManager.SessionCallback {
                override fun onValidSession(user: User) {
                    val i = if (user.isConfigured) {
                        Intent(this@SplashActivity, MainActivity::class.java)
                    } else {
                        Intent(this@SplashActivity, ConfigurationActivity::class.java)
                    }
                    startActivity(i)
                    finish()
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
                            intent.putExtra(Const.CODE_AUTH, Const.CODE_EXPIRED_TOKEN)
                            startActivity(intent)
                            finish()
                        }
                        2 -> {
                            intent = Intent(this@SplashActivity, SignInActivity::class.java)
                            if (throwable is UnknownHostException) {
                                intent.putExtra(Const.CODE_AUTH, Const.CODE_NO_INTERNET)
                            } else {
                                intent.putExtra(Const.CODE_AUTH, Const.CODE_ERROR)
                            }
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            })

        }
    }

}
