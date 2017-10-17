package it.gruppoinfor.home2work.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
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
import it.gruppoinfor.home2work.SessionManager;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.models.User;
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

    AnimationDrawable animationDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

    }


    @OnClick(R.id.sign_in_button)
    void signInButtonClick() {

        // Controlla la validità dei dati immessi
        if (!validate()) {
            return;
        }

        signInLoading.setVisibility(View.VISIBLE);

        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);

        signInButton.setEnabled(false);


        Call<User> call = Client.getAPI().login(email, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                switch (response.code()) {
                    case 404:
                        onLoginFailed();
                        break;
                    case 200:
                        onLoginSuccess(response.body());
                        break;
                    default:
                        onLoginError();
                        break;
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                onLoginError();
            }
        });
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

    private void onLoginSuccess(final User user) {

        Client.setUser(user);

        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.storeSession(user);

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

    private void onLoginError() {
        enableLogin();
        Toasty.error(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT, true).show();
    }

    private void enableLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                signInButton.setEnabled(true);
                emailEditText.setEnabled(true);
                passwordEditText.setEnabled(true);
                signInLoading.setVisibility(View.GONE);
            }
        }, 2000);
    }

    private void onLoginFailed() {
        enableLogin();
        Toasty.error(getContext(), getString(R.string.login_failed), Toast.LENGTH_SHORT, true).show();
    }
}
