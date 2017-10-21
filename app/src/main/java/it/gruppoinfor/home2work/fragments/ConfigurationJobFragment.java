package it.gruppoinfor.home2work.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.Converters;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.ConfigurationActivity;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.models.Job;
import it.gruppoinfor.home2work.models.Company;
import retrofit2.Callback;
import retrofit2.Response;


public class ConfigurationJobFragment extends Fragment implements Step {

    @BindView(R.id.loadingView)
    LinearLayout loadingView;
    @BindView(R.id.companySpinner)
    Spinner companySpinner;
    @BindView(R.id.startInput)
    EditText startInput;
    @BindView(R.id.endInput)
    EditText endInput;

    private Calendar calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_conf_job, container, false);
        ButterKnife.bind(this, root);

        calendar = Calendar.getInstance();

        Client.getAPI().getCompanies().enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Company>> call, Response<List<Company>> response) {
                ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getContext(),
                        android.R.layout.simple_spinner_item,
                        response.body());
                companySpinner.setAdapter(spinnerArrayAdapter);
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(retrofit2.Call<List<Company>> call, Throwable t) {

            }
        });

        companySpinner.requestFocus();

        return root;
    }

    @OnFocusChange(R.id.startInput)
    void startInputFocusChanged(View view, boolean hasFocus) {
        if (hasFocus) editStart();
    }

    @OnFocusChange(R.id.endInput)
    void endInputFocusChanged(View view, boolean hasFocus) {
        if (hasFocus) editEnd();
    }

    @OnClick(R.id.startInput)
    void editStart() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        final TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String formattedMinute = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
                startInput.setText(selectedHour + ":" + formattedMinute);
                //job_start = selectedHour * 3600L + selectedMinute * 60L;
            }
        }, hour, minute, true);

        mTimePicker.setTitle(R.string.config_job_start_selection);
        mTimePicker.show();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(startInput.getWindowToken(), 0);
    }

    @OnClick(R.id.endInput)
    void editEnd() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        final TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String formattedMinute = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
                endInput.setText(selectedHour + ":" + formattedMinute);
                //job_end = selectedHour * 3600L + selectedMinute * 60L;
            }
        }, hour, minute, true);

        mTimePicker.setTitle(R.string.config_job_end_selection);
        mTimePicker.show();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(endInput.getWindowToken(), 0);
    }


    @Override
    public VerificationError verifyStep() {

        if (companySpinner.getSelectedItem().toString().equals(getString(R.string.company)))
            return new VerificationError("Devi selezionare un azienda prima di poter continuare");

        if (startInput.getText().toString().isEmpty()) {
            startInput.setError("Inserisci un orario di inizio");
            return new VerificationError("Devi selezionare un orario di inizio");
        }

        if (endInput.getText().toString().isEmpty()) {
            endInput.setError("Inserisci un orario di fine");
            return new VerificationError("Devi selezionare un orario di fine");
        }

        Job job = Client.getSignedUser().getJob();

        job.setStart(Converters.timeToTimestamp(startInput.getText().toString()));
        job.setEnd(Converters.timeToTimestamp(endInput.getText().toString()));
        job.setCompany((Company) companySpinner.getSelectedItem());

        Client.getSignedUser().setJob(job);

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
