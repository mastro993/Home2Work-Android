package it.gruppoinfor.home2work.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.utils.Converters;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Address;
import it.gruppoinfor.home2workapi.model.User;


public class ConfigurationFragmentHome extends Fragment implements Step, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {

    private final int FINE_LOCATION_ACCESS = 0;

    @BindView(R.id.setAddressButton)
    Button setAddressButton;
    @BindView(R.id.currentPositionButton)
    Button currentPositionButton;
    @BindView(R.id.mapView)
    MapView mapView;

    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap;

    private Location lastLocation;
    private LatLng homeLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_conf_home, container, false);
        ButterKnife.bind(this, root);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return root;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        currentPositionButton.setVisibility(View.VISIBLE);

        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setAllGesturesEnabled(false);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setUpMap();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            MapsInitializer.initialize(this.getActivity());
            googleMap.setMyLocationEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.909986, 12.3959159), 5.0f));
        }
    }

    @OnClick(R.id.setAddressButton)
    void setAddress() {
        MaterialDialog editAddressDialog = new MaterialDialog.Builder(getContext())
                .title("Modifica indirizzo")
                .customView(R.layout.dialog_edit_address, false)
                .positiveText("Salva")
                .negativeText("Annulla")
                .onPositive(((dialog, which) -> {

                    Boolean valid = true;

                    View view = dialog.getCustomView();

                    EditText cityInput = view.findViewById(R.id.city_input);
                    EditText capInput = view.findViewById(R.id.cap_input);
                    EditText addressInput = view.findViewById(R.id.address_input);

                    String addr = addressInput.getText().toString();
                    String city = cityInput.getText().toString();
                    String CAP = capInput.getText().toString();

                    if (addr.isEmpty()) {
                        addressInput.setError(getString(R.string.config_address_insert));
                        valid = false;
                    }

                    if (city.isEmpty()) {
                        capInput.setError(getString(R.string.config_cap_insert));
                        valid = false;
                    }

                    if (CAP.isEmpty()) {
                        cityInput.setError(getString(R.string.config_city_insert));
                        valid = false;
                    }

                    if (valid) {
                        LatLng latLng = Converters.addressToLatLng(getContext(), addr + ", " + city + " " + CAP);
                        if (latLng != null) {

                            User signedUser = Client.User;

                            signedUser.setLocation(latLng);

                            Address newAddress = new Address();
                            newAddress.setCity(city);
                            newAddress.setAddress(addr);
                            newAddress.setPostalCode(CAP);

                            signedUser.setAddress(newAddress);

                            Client.User = signedUser;

                            setHomeLocation(latLng);

                        } else {
                            Toasty.warning(getContext(), getString(R.string.config_no_address_found), Toast.LENGTH_LONG).show();
                        }
                    }

                }))
                .build();

        View view = editAddressDialog.getCustomView();

        EditText cityInput = view.findViewById(R.id.city_input);
        EditText capInput = view.findViewById(R.id.cap_input);
        EditText addressInput = view.findViewById(R.id.address_input);

        User signedUserr = Client.User;

        if(signedUserr.getAddress() != null){
            addressInput.setText(signedUserr.getAddress().getAddress());
            capInput.setText(signedUserr.getAddress().getPostalCode());
            cityInput.setText(signedUserr.getAddress().getCity());
        }



        editAddressDialog.show();


    }

    @OnClick(R.id.currentPositionButton)
    void setCurrentPosition() {
        if (lastLocation != null) {
            double homeLat = lastLocation.getLatitude();
            double homeLon = lastLocation.getLongitude();
            LatLng currentLatLng = new LatLng(homeLat, homeLon);
            currentPositionButton.setVisibility(View.INVISIBLE);
            setHomeLocation(currentLatLng);
        }
    }

    private void setHomeLocation(LatLng latLng) {
        homeLocation = latLng;
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.home)))
                .showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public VerificationError verifyStep() {
        if (homeLocation == null)
            return new VerificationError("Devi impostare un indirizzo di casa prima di poter continuare");

        Client.User.setLocation(homeLocation);

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
