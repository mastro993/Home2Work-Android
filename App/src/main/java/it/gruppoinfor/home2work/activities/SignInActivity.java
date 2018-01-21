package it.gruppoinfor.home2work.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.interfaces.LoginCallback;
import it.gruppoinfor.home2workapi.model.User;

public class SignInActivity extends AppCompatActivity implements LoginCallback {

    @BindView(R.id.sign_in_button)
    Button signInButton;
    @BindView(R.id.lost_password_button)
    TextView lostPasswordButton;
    @BindView(R.id.email_edit_text)
    EditText emailEditText;
    @BindView(R.id.password_edit_text)
    EditText passwordEditText;
    @BindView(R.id.sign_in_loading)
    AVLoadingIndicatorView signInLoading;

    private SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        mSessionManager = new SessionManager(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            switch (bundle.getInt(SessionManager.AUTH_CODE)) {
                case SessionManager.ERROR:
                    Toasty.error(this, getString(R.string.activity_signin_error), Toast.LENGTH_SHORT, true).show();
                    break;
                case SessionManager.EXPIRED_TOKEN:
                    Toasty.warning(this, getString(R.string.activity_signin_session_expired), Toast.LENGTH_SHORT, true).show();
                    break;
                case SessionManager.NO_INTERNET:
                    Toasty.error(this, getString(R.string.activity_signin_no_internet), Toast.LENGTH_SHORT, true).show();
                    break;
            }

        }

    }

    @Override
    public void onLoginSuccess() {
        mSessionManager.storeSession();

        User user = HomeToWorkClient.getUser();

        if (user.isConfigured()) {

            Intent i = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(i);
            finish();

        } else {

            Intent i = new Intent(SignInActivity.this, ConfigurationActivity.class);
            startActivity(i);
            finish();

        }
    }

    @Override
    public void onInvalidCredential() {
        enableLogin();
        Toasty.error(SignInActivity.this, getString(R.string.activity_signin_login_failed), Toast.LENGTH_SHORT, true).show();
    }

    @Override
    public void onLoginError() {
        enableLogin();
        Toasty.error(SignInActivity.this, getString(R.string.activity_signin_server_error), Toast.LENGTH_SHORT, true).show();
    }

    @Override
    public void onError(Throwable throwable) {
        enableLogin();
        if (throwable instanceof UnknownHostException) {
            Toasty.error(SignInActivity.this, getString(R.string.activity_signin_no_internet), Toast.LENGTH_SHORT, true).show();
        } else {
            Toasty.error(SignInActivity.this, getString(R.string.activity_signin_server_error), Toast.LENGTH_SHORT, true).show();
        }
    }

    @OnClick(R.id.sign_in_button)
    void signInButtonClick() {

        if (!validate())
            return;

        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        signInButton.setVisibility(View.GONE);
        signInLoading.setVisibility(View.VISIBLE);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        HomeToWorkClient.getInstance().login(email, password, false, this);

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
            emailEditText.setError(getString(R.string.activity_signin_email_error));
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError(getString(R.string.activity_signin_password_error));
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }

}
