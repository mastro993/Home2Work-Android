package it.gruppoinfor.home2work.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.adapters.ConfigurationStepsAdapter;
import it.gruppoinfor.home2work.utils.SessionManager;

public class ConfigurationActivity extends AppCompatActivity implements StepperLayout.StepperListener {

    @BindView(R.id.stepperLayout)
    StepperLayout stepperLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        stepperLayout.setAdapter(new ConfigurationStepsAdapter(getSupportFragmentManager(), this));
        stepperLayout.setOffscreenPageLimit(5);
        stepperLayout.setListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_configuration, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_qr_code) {
            // TODO configurazione con QR code
        } else if (id == R.id.action_nfc) {
            // TODO configurazione con NFC
        } else if (id == R.id.action_logout) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);
            builder.setTitle(R.string.config_logout);
            builder.setMessage(R.string.config_logout_message);
            builder.setPositiveButton(R.string.config_logout_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SessionManager.with(ConfigurationActivity.this).signOutUser();

                    Intent intent = new Intent(ConfigurationActivity.this, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                    finish();
                }
            });
            builder.setNegativeButton(R.string.config_logout_cancel, null);
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompleted(View completeButton) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError(VerificationError verificationError) {
        //Toast.makeText(this, "onError! -> " + verificationError.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        //Toast.makeText(this, "onStepSelected! -> " + newStepPosition, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReturn() {
        finish();
    }
}
