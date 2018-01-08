package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.App;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.adapters.CompanySpinnerAdapter;
import it.gruppoinfor.home2work.utils.Converters;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2work.utils.Tools;
import it.gruppoinfor.home2workapi.model.Address;
import it.gruppoinfor.home2workapi.model.Company;
import it.gruppoinfor.home2workapi.model.LatLng;
import it.gruppoinfor.home2workapi.model.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ConfigurationActivity extends AppCompatActivity implements StepperLayout.StepperListener {

    @BindView(R.id.stepperLayout)
    StepperLayout stepperLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        stepperLayout.setAdapter(new ConfigurationStepsAdapter(getSupportFragmentManager(), this));
        stepperLayout.setOffscreenPageLimit(5);
        stepperLayout.setListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_configuration, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);
            builder.setTitle(R.string.config_logout);
            builder.setMessage(R.string.config_logout_message);
            builder.setPositiveButton(R.string.config_logout_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new SessionManager(ConfigurationActivity.this).signOutUser();

                    Intent intent = new Intent(ConfigurationActivity.this, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                    finish();
                }
            });
            builder.setNegativeButton(R.string.config_logout_cancel, null);
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompleted(View completeButton) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError(VerificationError verificationError) {
        //Toast.makeText(this, "onError! -> " + verificationError.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        //Toast.makeText(this, "onStepSelected! -> " + newStepPosition, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReturn() {
        finish();
    }

    public static class ConfigurationStepsAdapter extends AbstractFragmentStepAdapter {

        private static final String CURRENT_STEP_POSITION_KEY = "current_step";

        ConfigurationStepsAdapter(FragmentManager fm, Context context) {
            super(fm, context);
        }

        @Override
        public Step createStep(int position) {

            Fragment fragment;
            Bundle bundle = new Bundle();
            bundle.putInt(CURRENT_STEP_POSITION_KEY, position);

            switch (position) {
                case 0:
                    fragment = new ConfigurationFragmentStart();
                    break;
                case 1:
                    fragment = new ConfigurationFragmentName();
                    break;
                case 2:
                    fragment = new ConfigurationFragmentHome();
                    break;
                case 3:
                    fragment = new ConfigurationFragmentJob();
                    break;
                case 4:
                    fragment = new ConfigurationFragmentAvatar();
                    break;
                case 5:
                    fragment = new ConfigurationFragmentSocial();
                    break;
                case 6:
                    fragment = new ConfigurationFragmentComplete();
                    break;
                default:
                    fragment = new ConfigurationFragmentStart();
            }

            fragment.setArguments(bundle);
            return (Step) fragment;
        }

        @Override
        public int getCount() {
            return 7;
        }


    }

    public static class ConfigurationFragmentStart extends Fragment implements Step {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_conf_start, container, false);
        }

        @Override
        public VerificationError verifyStep() {
            return null;
        }

        @Override
        public void onSelected() {

        }

        @Override
        public void onError(@NonNull VerificationError error) {

        }
    }

    public static class ConfigurationFragmentName extends Fragment implements Step {

        @BindView(R.id.nameInput)
        EditText nameInput;
        @BindView(R.id.surnameInput)
        EditText surnameInput;

        private Unbinder mUnbinder;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_conf_name, container, false);
            mUnbinder = ButterKnife.bind(this, root);

            nameInput.setText(App.home2WorkClient.getUser().getName());
            surnameInput.setText(App.home2WorkClient.getUser().getSurname());

            return root;
        }

        @Override
        public VerificationError verifyStep() {

            if (nameInput.getText().toString().isEmpty()) {
                nameInput.setError("Inserisci un nome valido");
                return new VerificationError("Nome non valido");
            }

            if (surnameInput.getText().toString().isEmpty()) {
                surnameInput.setError("Inserisci un cognome valido");
                return new VerificationError("Cognome non valido");
            }

            App.home2WorkClient.getUser().setName(nameInput.getText().toString().trim());
            App.home2WorkClient.getUser().setSurname(surnameInput.getText().toString().trim());

            return null;
        }

        @Override
        public void onSelected() {

        }

        @Override
        public void onError(@NonNull VerificationError error) {
            Toasty.warning(getContext(), error.getErrorMessage()).show();
        }

        @Override
        public void onDestroyView() {
            mUnbinder.unbind();
            super.onDestroyView();
        }
    }

    public static class ConfigurationFragmentHome extends Fragment implements Step, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {

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

        private Unbinder mUnbinder;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_conf_home, container, false);
            mUnbinder = ButterKnife.bind(this, root);

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
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                MapsInitializer.initialize(this.getActivity());
                googleMap.setMyLocationEnabled(true);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.909986, 12.3959159).toLatLng(), 5.0f));
            }
        }

        @OnClick(R.id.setAddressButton)
        void setAddress() {
            MaterialDialog editAddressDialog = new MaterialDialog.Builder(getContext())
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

                                Address newAddress = new Address();
                                newAddress.setCity(city);
                                newAddress.setAddress(addr);
                                newAddress.setPostalCode(CAP);

                                App.home2WorkClient.getUser().setLocation(latLng);
                                App.home2WorkClient.getUser().setAddress(newAddress);

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

            User user = App.home2WorkClient.getUser();

            if (user.getAddress() != null) {
                addressInput.setText(user.getAddress().getAddress());
                capInput.setText(user.getAddress().getPostalCode());
                cityInput.setText(user.getAddress().getCity());
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
                    .position(latLng.toLatLng())
                    .title(getString(R.string.home)))
                    .showInfoWindow();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng.toLatLng(), 15.0f));
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
        public void onLowMemory() {
            super.onLowMemory();
            mapView.onLowMemory();
        }

        @Override
        public VerificationError verifyStep() {
            if (homeLocation == null)
                return new VerificationError("Devi impostare un indirizzo di casa prima di poter continuare");

            App.home2WorkClient.getUser().setLocation(homeLocation);

            return null;
        }

        @Override
        public void onSelected() {

        }

        @Override
        public void onError(@NonNull VerificationError error) {
            Toasty.warning(getContext(), error.getErrorMessage()).show();
        }

        @Override
        public void onDestroyView() {
            mUnbinder.unbind();
            super.onDestroyView();
        }
    }

    public static class ConfigurationFragmentJob extends Fragment implements Step {

        @BindView(R.id.loadingView)
        LinearLayout loadingView;
        @BindView(R.id.companySpinner)
        Spinner companySpinner;

        private Calendar calendar;
        private List<Company> mCompanies;

        private Unbinder mUnbinder;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_conf_job, container, false);
            mUnbinder = ButterKnife.bind(this, root);

            calendar = Calendar.getInstance();

            App.home2WorkClient.getCompanies(companies -> {
                mCompanies = companies;
                initCompaniesSpinner();
            });

            companySpinner.requestFocus();

            return root;
        }

        @Override
        public VerificationError verifyStep() {

            if (companySpinner.getSelectedItem().toString().equals(getString(R.string.company)))
                return new VerificationError("Devi selezionare un azienda prima di poter continuare");

            App.home2WorkClient.getUser().setCompany((Company) companySpinner.getSelectedItem());

            return null;
        }

        @Override
        public void onSelected() {

        }

        @Override
        public void onError(@NonNull VerificationError error) {
            Toasty.warning(getContext(), error.getErrorMessage()).show();
        }

        @Override
        public void onDestroyView() {
            mUnbinder.unbind();
            super.onDestroyView();
        }

        private void initCompaniesSpinner() {
            CompanySpinnerAdapter companySpinnerAdapter = new CompanySpinnerAdapter(getActivity(), mCompanies);
            companySpinner.setAdapter(companySpinnerAdapter);
            loadingView.setVisibility(View.GONE);
        }

    }

    public static class ConfigurationFragmentAvatar extends Fragment implements BlockingStep {

        private final int PHOTO_INTENT = 0;

        @BindView(R.id.propicView)
        ImageView propicView;

        private Bitmap propic;
        private boolean uploaded = false;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_conf_propic, container, false);
            ButterKnife.bind(this, root);

            return root;
        }

        @OnClick(R.id.selectPhotoButton)
        void selectPhoto() {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                selectImageIntent();
            }
        }

        private void selectImageIntent() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    getString(R.string.config_avatar_selection)), PHOTO_INTENT);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == PHOTO_INTENT && resultCode == RESULT_OK) {
                try {

                    Uri selectedImageUri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    propic = Tools.shrinkBitmap(bitmap, 300);
                    propicView.setImageBitmap(propic);
                    uploaded = false;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImageIntent();
            } else {
                //requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }

        @Override
        public VerificationError verifyStep() {

            return null;
        }

        @Override
        public void onSelected() {

        }

        @Override
        public void onError(@NonNull VerificationError error) {

        }

        @Override
        public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {

            if (propic == null || uploaded) {
                callback.goToNextStep();
            } else {

                callback.getStepperLayout().showProgress("Upload immagine in corso...");

                File file = Converters.bitmapToFile(getContext(), propic);
                String decodedAvatar = Tools.decodeFile(file.getPath(), 300, 300);
                File decodedFile = new File(decodedAvatar);

                String mime = Tools.getMimeType(decodedFile.getPath());
                MediaType mediaType = MediaType.parse(mime);

                RequestBody requestFile = RequestBody.create(mediaType, decodedFile);

                String filename = App.home2WorkClient.getUser().getId() + ".jpg";

                MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", filename, requestFile);

                App.home2WorkClient.uploadAvatar(body, responseBody -> {
                    callback.getStepperLayout().hideProgress();
                    callback.goToNextStep();
                }, e -> {
                    callback.getStepperLayout().hideProgress();
                    Toasty.error(getContext(), "Impossibile caricare l'immagine al momento").show();
                });

            }
        }

        @Override
        public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

        }

        @Override
        public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
            callback.goToPrevStep();
        }
    }

    public static class ConfigurationFragmentSocial extends Fragment implements Step {

        @BindView(R.id.facebookInput)
        EditText facebookInput;
        @BindView(R.id.twitterInput)
        EditText twitterInput;
        @BindView(R.id.telegramInput)
        EditText telegramInput;

        private Unbinder unbinder;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_conf_social, container, false);
            unbinder = ButterKnife.bind(this, root);
            return root;
        }

        @Override
        public VerificationError verifyStep() {

            if (!facebookInput.getText().toString().isEmpty())
                App.home2WorkClient.getUser().setFacebook(facebookInput.getText().toString());

            if (!twitterInput.getText().toString().isEmpty())
                App.home2WorkClient.getUser().setTwitter(twitterInput.getText().toString());

            if (!telegramInput.getText().toString().isEmpty())
                App.home2WorkClient.getUser().setTelegram(telegramInput.getText().toString());


            return null;
        }

        @Override
        public void onSelected() {

        }

        @Override
        public void onError(@NonNull VerificationError error) {

        }

        @Override
        public void onDestroyView() {
            unbinder.unbind();
            super.onDestroyView();
        }
    }

    public static class ConfigurationFragmentComplete extends Fragment implements BlockingStep {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_conf_completed, container, false);
            ButterKnife.bind(this, root);
            return root;
        }

        @Override
        public VerificationError verifyStep() {
            return null;
        }

        @Override
        public void onSelected() {

        }

        @Override
        public void onError(@NonNull VerificationError error) {

        }

        @Override
        public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {

        }

        @Override
        public void onCompleteClicked(final StepperLayout.OnCompleteClickedCallback callback) {
            callback.getStepperLayout().showProgress("Attendi..");

            App.home2WorkClient.getUser().setConfigured(true);

            App.home2WorkClient.updateUser(
                    user -> callback.complete(),
                    e -> callback.getStepperLayout().hideProgress());

        }

        @Override
        public void onBackClicked(final StepperLayout.OnBackClickedCallback callback) {
            callback.goToPrevStep();
        }
    }

}
