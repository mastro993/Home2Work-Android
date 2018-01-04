package it.gruppoinfor.home2work.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;

public class ConfigurationFragmentSocial extends Fragment implements BlockingStep {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_conf_social, container, false);
        ButterKnife.bind(this, root);
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


    }

    @Override
    public void onBackClicked(final StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }
}
