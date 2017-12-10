package it.gruppoinfor.home2work.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static it.gruppoinfor.home2work.App.getContext;

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

    @OnClick(R.id.sign_in_button)
    void signInButtonClick() {

        if (!validate())
            return;

        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        signInButton.setEnabled(false);

        signInLoading.setVisibility(View.VISIBLE);
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Client.getAPI().login(email, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                switch (response.code()) {
                    case 404:
                        enableLogin();
                        Toasty.error(getContext(), getString(R.string.login_failed), Toast.LENGTH_SHORT, true).show();
                        break;
                    case 200:
                        onLoginSuccess(response.body());
                        break;
                    default:
                        enableLogin();
                        Toasty.error(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT, true).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                enableLogin();
                Toasty.error(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void onLoginSuccess(final User user) {

        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.storeSession(user);
        Client.setSignedUser(user);

        if (user.isConfigured()) {

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
        new Handler().postDelayed(() -> {
            signInButton.setEnabled(true);
            emailEditText.setEnabled(true);
            passwordEditText.setEnabled(true);
            signInLoading.setVisibility(View.GONE);
        }, 2000);
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

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            SessionManager.AuthCode code = (SessionManager.AuthCode) bundle.get(SessionManager.AUTH_CODE);

            if (code == SessionManager.AuthCode.EXPIRED_TOKEN) {
                Toasty.warning(this, getString(R.string.session_expired), Toast.LENGTH_SHORT, true).show();
            } else if (code == SessionManager.AuthCode.SIGNED_OUT) {
                Toasty.info(this, getString(R.string.user_logout), Toast.LENGTH_SHORT, true).show();
            } else if (code == SessionManager.AuthCode.ERROR) {
                Toasty.error(this, "Errore durante il login", Toast.LENGTH_SHORT, true).show();
            }
        }

    }
}
