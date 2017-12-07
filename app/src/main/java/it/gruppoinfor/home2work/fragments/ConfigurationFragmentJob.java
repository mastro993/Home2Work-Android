package it.gruppoinfor.home2work.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Company;
import retrofit2.Callback;
import retrofit2.Response;


public class ConfigurationFragmentJob extends Fragment implements Step {

    @BindView(R.id.loadingView)
    LinearLayout loadingView;
    @BindView(R.id.companySpinner)
    Spinner companySpinner;

    private Calendar calendar;
    private List<Company> companies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_conf_job, container, false);
        ButterKnife.bind(this, root);

        calendar = Calendar.getInstance();

        Client.getAPI().getCompanies().enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Company>> call, Response<List<Company>> response) {
                companies = response.body();
                initCompaniesSpinner();
            }

            @Override
            public void onFailure(retrofit2.Call<List<Company>> call, Throwable t) {

            }
        });

        companySpinner.requestFocus();

        return root;
    }

    private void initCompaniesSpinner() {
        // TODO custom spinner
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                companies);
        companySpinner.setAdapter(spinnerArrayAdapter);
        loadingView.setVisibility(View.GONE);
    }


    @Override
    public VerificationError verifyStep() {

        if (companySpinner.getSelectedItem().toString().equals(getString(R.string.company)))
            return new VerificationError("Devi selezionare un azienda prima di poter continuare");


        Client.getSignedUser().getCompany().setId(
                ((Company) companySpinner.getSelectedItem()).getId()
        );

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
