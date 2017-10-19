package it.gruppoinfor.home2work.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.api.APIClient;

public class ConfigurationNameFragment extends Fragment implements Step {

    @BindView(R.id.nameInput)
    EditText nameInput;
    @BindView(R.id.surnameInput)
    EditText surnameInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_conf_name, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public VerificationError verifyStep() {

        if (nameInput.getText().toString().isEmpty()) {
            nameInput.setError("Inserisci un nome valido");
            return new VerificationError("Nome non valido");
        }

        if (surnameInput.getText().toString().isEmpty()) {
            surnameInput.setError("Inserisci un cognome valido");
            return new VerificationError("Cognome non valido");
        }

        APIClient.getAccount().setName(nameInput.getText().toString().trim());
        APIClient.getAccount().setSurname(surnameInput.getText().toString().trim());

        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {
        Toasty.warning(getContext(), error.getErrorMessage()).show();
    }
}
