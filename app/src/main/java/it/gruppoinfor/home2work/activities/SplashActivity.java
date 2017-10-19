package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.SessionManager;
import it.gruppoinfor.home2work.UserPrefs;
import it.gruppoinfor.home2work.api.APIClient;
import it.gruppoinfor.home2work.api.Account;

public class SplashActivity extends AppCompatActivity {

    private final int FINE_LOCATION_ACCESS = 0;
    private final int SPLASH_TIME = 1000;

    @BindView(R.id.logo_imageview)
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                UserPrefs.init(getApplicationContext());

                if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS);
                } else {
                    startApp();
                }

            }
        }, SPLASH_TIME);

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void startApp() {
        SessionManager sessionManager = new SessionManager(SplashActivity.this);
        sessionManager.checkSession(new SessionManager.Callback() {
            @Override
            public void onValidSession(Account account) {

                // Imposto l'utente autenticato nel APIClient
                APIClient.setUser(account);

                if (!account.isConfigured()) {
                    Intent i = new Intent(SplashActivity.this, ConfigurationActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }

            }

            @Override
            public void onInvalidSession(SessionManager.AuthCode code) {
                // Se l'utente non ha una sessione valida viene portato alla schermata di login

                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                intent.putExtra(SessionManager.AUTH_CODE, code);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(SplashActivity.this, logo, "logo");
                startActivity(intent, options.toBundle());

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startApp();
            else
                finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
