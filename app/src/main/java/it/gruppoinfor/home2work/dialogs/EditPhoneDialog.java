package it.gruppoinfor.home2work.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.api.APIClient;


public class EditPhoneDialog extends AlertDialog {

    @BindView(R.id.phoneInput)
    EditText phoneInput;

    private Callback callback;
    private Context context;

    public EditPhoneDialog(Context context, Callback callback) {
        super(context);
        View view = View.inflate(context, R.layout.dialog_edit_phone, null);
        ButterKnife.bind(this, view);
        setView(view);
        setTitle("Modifica numero di telefono");
        this.context = context;
        this.callback = callback;
        phoneInput.setText(APIClient.getAccount().getContacts().getPhone());
    }

    @OnClick(R.id.saveButton)
    void save() {
        String phone = phoneInput.getText().toString();
        if (phone.isEmpty() || phone.length() < 10) {
            phoneInput.setError("Numero di telefono non valido");
        } else {
            callback.onSave(this, phone);
        }
    }

    public interface Callback {
        void onSave(AlertDialog dialog, String phone);
    }

}
