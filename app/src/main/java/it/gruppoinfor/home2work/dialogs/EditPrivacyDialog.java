package it.gruppoinfor.home2work.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.api.Client;


public class EditPrivacyDialog extends AlertDialog {

    @BindView(R.id.privacySpinner)
    Spinner privacySpinner;

    private Callback callback;

    public EditPrivacyDialog(Context context, Callback callback) {
        super(context);
        View view = View.inflate(context, R.layout.dialog_edit_privacy, null);
        ButterKnife.bind(this, view);
        setView(view);
        setTitle("Modifica privacy profilo");
        this.callback = callback;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.profile_privacy_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        privacySpinner.setAdapter(adapter);
        privacySpinner.setSelection(Client.getUser().getPrivacy());
    }

    @OnClick(R.id.saveButton)
    void save() {
        int position = privacySpinner.getSelectedItemPosition();
        callback.onSave(this, position);
    }


    public interface Callback {
        void onSave(AlertDialog dialog, int newPrivacy);
    }
}
