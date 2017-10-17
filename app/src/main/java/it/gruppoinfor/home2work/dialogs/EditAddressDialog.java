package it.gruppoinfor.home2work.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.Converters;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.models.User;

public class EditAddressDialog extends AlertDialog {

    @BindView(R.id.addressInput)
    EditText addressInput;
    @BindView(R.id.cityInput)
    EditText cityInput;
    @BindView(R.id.capInput)
    EditText capInput;

    private Callback callback;
    private Context context;
    private User user;

    public EditAddressDialog(Context context, Callback callback) {
        super(context);
        View view = View.inflate(context, R.layout.dialog_edit_address, null);
        ButterKnife.bind(this, view);
        setView(view);
        setTitle("Modifica indirizzo");
        this.user = Client.getUser();

        this.context = context;
        this.callback = callback;

        Converters.latLngToAddress(context, user.getHomeLoc(), new Converters.GeocoderCallback() {
            @Override
            public void onFinish(Address address) {
                if (address != null) {
                    addressInput.setText(address.getAddressLine(0));
                    capInput.setText(address.getPostalCode());
                    cityInput.setText(address.getLocality());
                }
            }
        });

    }

    @OnClick(R.id.saveButton)
    void save() {
        boolean valid = true;

        String address = addressInput.getText().toString();
        String city = cityInput.getText().toString();
        String CAP = capInput.getText().toString();

        if (address.isEmpty()) {
            addressInput.setError(context.getString(R.string.config_address_insert));
            valid = false;
        }

        if (city.isEmpty()) {
            capInput.setError(context.getString(R.string.config_cap_insert));
            valid = false;
        }

        if (CAP.isEmpty()) {
            cityInput.setError(context.getString(R.string.config_city_insert));
            valid = false;
        }

        if (valid) {
            LatLng latLng = Converters.addressToLatLng(context, address + ", " + city + " " + CAP);
            if (latLng != null) {
                callback.onSave(this, latLng);
            } else {
                Toasty.warning(context, context.getString(R.string.config_no_address_found), Toast.LENGTH_LONG).show();
            }
        }
    }

    public interface Callback {
        void onSave(AlertDialog dialog, LatLng latLng);
    }

}
