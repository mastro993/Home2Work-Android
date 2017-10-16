package it.gruppoinfor.home2work.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;

public class SplashActivity extends Activity {

    private final int FINE_LOCATION_ACCESS = 0;
    private final int SPLASH_TIME = 2000;

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

                //PreferenceManager.loadPrefs();
                //checkPermissions();

                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(SplashActivity.this, logo, "logo");
                startActivity(intent, options.toBundle());

            }
        }, SPLASH_TIME);

    }

    /*private void startApp() {
        SessionManager sessionManager = new SessionManager(SplashActivity.this);
        sessionManager.checkSession(new SessionManager.Callback() {
            @Override
            public void onValidSession(User user) {
                FleetUpClient.setUser(user);
                if (!user.isConfigured()) {
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

                Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
                intent.putExtra(SessionManager.AUTH_CODE, code);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(SplashActivity.this, logo, "logo");
                startActivity(intent, options.toBundle());

            }
        });
    }*/

    /*private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this, R.style.LightAlertDialog);
            adb.setTitle("Permessi applicazione");
            adb.setMessage("FleetUp ha bisogno di monitorare la tua posizione per poter funzionare correttamente.");
            adb.setPositiveButton("Consenti", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS);
                }
            });
            adb.setNegativeButton("Esci", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            adb.setCancelable(false);
            adb.show();
        } else {
            startApp();
        }
    }*/

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS) {
            checkPermissions();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/
}
