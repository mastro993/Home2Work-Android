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
import it.gruppoinfor.home2work.fragments.MatchFragment;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2workapi.Client;

public class SplashActivity extends AppCompatActivity {

    private final int FINE_LOCATION_ACCESS = 0;
    private final int SPLASH_TIME = 500;

    @BindView(R.id.logo_imageview)
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        new Handler().postDelayed(() -> {

            if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, FINE_LOCATION_ACCESS);
            } else {
                startApp();
            }

        }, SPLASH_TIME);

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void startApp() {
        SessionManager.with(this).checkSession(new SessionManager.SessionManagerCallback() {

            @Override
            public void onNoSession() {
                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                startActivity(intent);
            }

            @Override
            public void onValidSession() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onExpiredToken() {
                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                intent.putExtra(SessionManager.AUTH_CODE, SessionManager.EXPIRED_TOKEN);
                startActivity(intent);
            }

            @Override
            public void onError() {
                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                intent.putExtra(SessionManager.AUTH_CODE, SessionManager.ERROR);
                startActivity(intent);
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
