package it.gruppoinfor.home2work.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    public static final int CODE_EXPIRED_TOKEN = 0;
    public static final int CODE_ERROR = 1;
    public static final int CODE_NO_INTERNET = 2;
    private static final String PREFS_EMAIL = "signin_email";
    private static final String PREFS_SIGNIN = "it.home2work.app.signin";

    @BindView(R.id.sign_in_button)
    Button signInButton;
    @BindView(R.id.lost_password_button)
    TextView lostPasswordButton;
    @BindView(R.id.email_edit_text)
    EditText emailEditText;
    @BindView(R.id.password_edit_text)
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        loadEmail();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            switch (bundle.getInt(SessionManager.AUTH_CODE)) {
                case CODE_ERROR:
                    Toasty.error(this, getString(R.string.activity_signin_error), Toast.LENGTH_SHORT, true).show();
                    break;
                case CODE_EXPIRED_TOKEN:
                    Toasty.warning(this, getString(R.string.activity_signin_session_expired), Toast.LENGTH_SHORT, true).show();
                    break;
                case CODE_NO_INTERNET:
                    Toasty.error(this, getString(R.string.activity_signin_no_internet), Toast.LENGTH_SHORT, true).show();
                    break;
            }

        }

    }

    @Override
    public void onLoginSuccess() {
        storeEmail();

        SessionManager.storeSession(this, HomeToWorkClient.getUser());
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
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (!validate())
            return;

        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);

        signInButton.setEnabled(false);
        signInButton.setText("Accesso in corso");

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        HomeToWorkClient.getInstance().login(email, password, false, this);

    }

    private void enableLogin() {
        signInButton.setEnabled(true);
        signInButton.setText("Accedi");
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

    private void storeEmail() {
        SharedPreferences prefs = getSharedPreferences(PREFS_SIGNIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_EMAIL, emailEditText.getText().toString());
        editor.apply();
    }

    private void loadEmail() {
        SharedPreferences prefs = getSharedPreferences(PREFS_SIGNIN, Context.MODE_PRIVATE);
        String email = prefs.getString(PREFS_EMAIL, "");
        emailEditText.setText(email);

    }

}
