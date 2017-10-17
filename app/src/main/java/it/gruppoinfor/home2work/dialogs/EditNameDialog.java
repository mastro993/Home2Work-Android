package it.gruppoinfor.home2work.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.models.User;


public class EditNameDialog extends AlertDialog {

    @BindView(R.id.nameEditText)
    EditText nameEditText;
    @BindView(R.id.surnameEditText)
    EditText surnameEditText;

    private Callback callback;
    private User user;

    public EditNameDialog(Context context, Callback callback) {
        super(context);
        View view = View.inflate(context, R.layout.dialog_edit_name, null);
        ButterKnife.bind(this, view);
        setView(view);
        setTitle("Modifica nome e cognome");
        this.callback = callback;
        this.user = Client.getUser();

        nameEditText.setText(user.getName());
        surnameEditText.setText(user.getSurname());

    }

    @OnClick(R.id.saveButton)
    void save() {
        String name = nameEditText.getText().toString();
        String surname = surnameEditText.getText().toString();
        callback.onSave(this, name, surname);
    }

    public interface Callback {
        void onSave(AlertDialog dialog, String name, String surname);
    }


}
