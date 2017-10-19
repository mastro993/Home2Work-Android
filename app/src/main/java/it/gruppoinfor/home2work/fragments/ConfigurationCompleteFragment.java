package it.gruppoinfor.home2work.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.iid.FirebaseInstanceId;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.api.APIClient;
import it.gruppoinfor.home2work.api.Account;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfigurationCompleteFragment extends Fragment implements BlockingStep {

    private Account account;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_conf_completed, container, false);
        ButterKnife.bind(this, root);
        this.account = APIClient.getAccount();
        return root;
    }

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {

    }

    @Override
    public void onCompleteClicked(final StepperLayout.OnCompleteClickedCallback callback) {
        callback.getStepperLayout().showProgress("Attendi..");

        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        account.setFcmToken(fcmToken);
        account.setConfigured(true);

        APIClient.API().updateUser(account).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                callback.getStepperLayout().hideProgress();
                if(response.code()==200){
                    Account account = response.body();
                    APIClient.setUser(account);
                    callback.complete();
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                callback.getStepperLayout().hideProgress();
            }
        });

    }

    @Override
    public void onBackClicked(final StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }
}
