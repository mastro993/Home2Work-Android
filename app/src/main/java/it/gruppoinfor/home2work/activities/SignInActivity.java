package it.gruppoinfor.home2work.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2workapi.Home2WorkClient;

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.sign_in_button)
    Button signInButton;
    @BindView(R.id.lost_password_button)
    Button lostPasswordButton;
    @BindView(R.id.email_edit_text)
    EditText emailEditText;
    @BindView(R.id.password_edit_text)
    EditText passwordEditText;
    @BindView(R.id.sign_in_loading)
    AVLoadingIndicatorView signInLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            switch (bundle.getInt(SessionManager.AUTH_CODE)) {
                case SessionManager.ERROR:
                    Toasty.error(this, "Errore durante il login", Toast.LENGTH_SHORT, true).show();
                    break;
                case SessionManager.EXPIRED_TOKEN:
                    Toasty.warning(this, getString(R.string.session_expired), Toast.LENGTH_SHORT, true).show();
                    break;
            }

        }

    }

    @OnClick(R.id.sign_in_button)
    void signInButtonClick() {

        if (!validate())
            return;

        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        signInButton.setVisibility(View.GONE);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Home2WorkClient home2WorkClient = new Home2WorkClient();

        home2WorkClient.API.login(email, password, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    switch (userResponse.code()) {
                        case 404:
                            enableLogin();
                            Toasty.error(SignInActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT, true).show();
                            break;
                        case 200:
                            Home2WorkClient.User = userResponse.body();
                            onLoginSuccess();
                            break;
                        default:
                            enableLogin();
                            Toasty.error(SignInActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT, true).show();
                            break;
                    }
                }, throwable -> {
                    enableLogin();
                    Toasty.error(SignInActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT, true).show();
                });

    }

    private void onLoginSuccess() {

        new SessionManager(this).storeSession();

        if (Home2WorkClient.User.isConfigured()) {

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();

        } else {

            Intent i = new Intent(this, ConfigurationActivity.class);
            startActivity(i);
            finish();

        }

    }

    private void enableLogin() {
        signInButton.setVisibility(View.VISIBLE);
        emailEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
    }

    private boolean validate() {

        boolean valid = true;

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.invalid_email_error));
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError(getString(R.string.invalid_password_error));
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }


}
