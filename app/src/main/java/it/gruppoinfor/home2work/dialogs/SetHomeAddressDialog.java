package it.gruppoinfor.home2work.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;


public class SetHomeAddressDialog extends AlertDialog {

    private Context context;
    private Callback callback;

    public SetHomeAddressDialog(Context context, Callback callback) {
        super(context);
        this.context = context;
        this.callback = callback;
        View view = getLayoutInflater().inflate(R.layout.dialog_home_address, null);
        ButterKnife.bind(this, view);
        setView(view);
    }

    public interface Callback {
        void onSave(final AlertDialog dialog, LatLng latLng);
    }

}
