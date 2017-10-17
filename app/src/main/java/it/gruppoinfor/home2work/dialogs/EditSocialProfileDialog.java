package it.gruppoinfor.home2work.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.gruppoinfor.home2work.R;


public class EditSocialProfileDialog extends AlertDialog {

    @BindView(R.id.usernameInput)
    EditText usernameInput;

    private Callback callback;
    private Context context;

    public EditSocialProfileDialog(Context context, Callback callback, int title, String value) {
        super(context);
        View view = View.inflate(context, R.layout.dialog_edit_social_profile, null);
        ButterKnife.bind(this, view);
        setView(view);
        setTitle(title);
        this.context = context;
        this.callback = callback;

        usernameInput.setText(value);
    }

    @OnClick(R.id.saveButton)
    void save() {
        String username = usernameInput.getText().toString();

        if (username.isEmpty()) {
            usernameInput.setError(context.getString(R.string.edit_username_not_valid));
        } else {
            callback.onSave(this, username);
        }
    }

    public interface Callback {
        void onSave(AlertDialog dialog, String username);
    }
}
