package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.arasthel.asyncjob.AsyncJob;

import java.net.UnknownHostException;

import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2work.utils.UserPrefs;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.model.User;

public class SplashActivity extends AppCompatActivity implements SessionManager.SessionManagerCallback {

    private final int FINE_LOCATION_ACCESS = 0;
    private final int SPLASH_TIME = 500;
    private SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        mSessionManager = new SessionManager(this);

        // Controllo dei permessi
        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, FINE_LOCATION_ACCESS);
        } else {
            initApp();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                initApp();
            else
                finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onNoSession() {
        Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
        startActivity(intent);
    }

    @Override
    public void onValidSession() {

        User user = HomeToWorkClient.getUser();
        if (user.isConfigured()) {
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(SplashActivity.this, ConfigurationActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onExpiredToken() {
        Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
        intent.putExtra(SessionManager.AUTH_CODE, SessionManager.EXPIRED_TOKEN);
        startActivity(intent);
    }

    @Override
    public void onError(Throwable throwable) {
        Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
        if (throwable instanceof UnknownHostException) {
            intent.putExtra(SessionManager.AUTH_CODE, SessionManager.NO_INTERNET);
        } else {
            intent.putExtra(SessionManager.AUTH_CODE, SessionManager.ERROR);
        }
        startActivity(intent);
        finish();
    }

    private void initApp() {
        AsyncJob.doInBackground(() -> {

            // Carico preferenze utente
            UserPrefs.init(SplashActivity.this);

            // Mostro il logo per qualche istante
            try {
                Thread.sleep(SPLASH_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mSessionManager.loadSession(this);


        });
    }

}
