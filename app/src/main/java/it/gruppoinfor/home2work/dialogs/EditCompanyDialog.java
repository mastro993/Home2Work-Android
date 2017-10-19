package it.gruppoinfor.home2work.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.api.APIClient;
import it.gruppoinfor.home2work.models.Company;
import retrofit2.Call;
import retrofit2.Response;

public class EditCompanyDialog extends AlertDialog {

    @BindView(R.id.loadingView)
    ProgressBar loadingView;
    @BindView(R.id.companySpinner)
    Spinner companySpinner;

    private Callback callback;

    public EditCompanyDialog(final Context context, Callback callback) {
        super(context);
        View view = View.inflate(context, R.layout.dialog_edit_company, null);
        ButterKnife.bind(this, view);
        setView(view);
        setTitle("Modifica azienda");
        this.callback = callback;

        APIClient.API().getCompanies().enqueue(new retrofit2.Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {
                Company[] companies = response.body().toArray(new Company[response.body().size()]);
                ArrayAdapter<Company> companyNames = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, companies);
                companyNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                companySpinner.setAdapter(companyNames);

                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Company>> call, Throwable t) {

            }
        });

    }

    @OnClick(R.id.saveButton)
    void save() {
        callback.onSave(this, companySpinner.getSelectedItem().toString());
    }

    public interface Callback {
        void onSave(AlertDialog dialog, final String company);
    }
}
