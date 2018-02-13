package it.gruppoinfor.home2work.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast

import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.Step
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter

import java.io.File

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.adapters.CompanySpinnerAdapter
import it.gruppoinfor.home2work.utils.Converters
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2work.utils.ImageTools
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Address
import it.gruppoinfor.home2workapi.model.Company
import it.gruppoinfor.home2workapi.model.LatLng
import it.gruppoinfor.home2workapi.model.User
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ConfigurationActivity : AppCompatActivity(), StepperLayout.StepperListener {

    @BindView(R.id.stepperLayout)
    internal var stepperLayout: StepperLayout? = null
    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        stepperLayout!!.adapter = ConfigurationStepsAdapter(supportFragmentManager, this)
        stepperLayout!!.setOffscreenPageLimit(5)
        stepperLayout!!.setListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_configuration, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_logout) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.dialog_logout_title)
            builder.setMessage(R.string.dialog_logout_content)
            builder.setPositiveButton(R.string.dialog_logout_confirm) { dialogInterface, i ->

                SessionManager.clearSession(this@ConfigurationActivity)
                val intent = Intent(this@ConfigurationActivity, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()

            }
            builder.setNegativeButton(R.string.dialog_logout_decline, null)
            builder.show()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCompleted(completeButton: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onError(verificationError: VerificationError) {
        //Toast.makeText(this, "onError! -> " + verificationError.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    override fun onStepSelected(newStepPosition: Int) {
        //Toast.makeText(this, "onStepSelected! -> " + newStepPosition, Toast.LENGTH_SHORT).show();
    }

    override fun onReturn() {
        finish()
    }

    class ConfigurationStepsAdapter internal constructor(fm: FragmentManager, context: Context) : AbstractFragmentStepAdapter(fm, context) {

        override fun createStep(position: Int): Step {

            val fragment: Fragment
            val bundle = Bundle()
            bundle.putInt(CURRENT_STEP_POSITION_KEY, position)

            when (position) {
                0 -> fragment = ConfigurationFragmentStart()
                1 -> fragment = ConfigurationFragmentName()
                2 -> fragment = ConfigurationFragmentHome()
                3 -> fragment = ConfigurationFragmentJob()
                4 -> fragment = ConfigurationFragmentAvatar()
                5 -> fragment = ConfigurationFragmentSocial()
                6 -> fragment = ConfigurationFragmentComplete()
                else -> fragment = ConfigurationFragmentStart()
            }

            fragment.arguments = bundle
            return fragment as Step
        }

        override fun getCount(): Int {
            return 7
        }

        companion object {

            private val CURRENT_STEP_POSITION_KEY = "current_step"
        }


    }

    class ConfigurationFragmentStart : Fragment(), Step {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_conf_start, container, false)
        }

        override fun verifyStep(): VerificationError? {
            return null
        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {

        }
    }

    class ConfigurationFragmentName : Fragment(), Step {

        @BindView(R.id.nameInput)
        internal var nameInput: EditText? = null
        @BindView(R.id.surnameInput)
        internal var surnameInput: EditText? = null

        private var mUnbinder: Unbinder? = null
        private var mContext: Context? = null

        override fun onAttach(context: Context) {
            super.onAttach(context)
            mContext = context
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val root = inflater.inflate(R.layout.fragment_conf_name, container, false)
            mUnbinder = ButterKnife.bind(this, root)

            nameInput!!.setText(HomeToWorkClient.getUser().name)
            surnameInput!!.setText(HomeToWorkClient.getUser().surname)

            return root
        }

        override fun verifyStep(): VerificationError? {

            if (nameInput!!.text.toString().isEmpty()) {
                nameInput!!.error = mContext!!.getString(R.string.activity_configuration_name_warning)
                return VerificationError(mContext!!.getString(R.string.activity_configuration_name_error))
            }

            if (surnameInput!!.text.toString().isEmpty()) {
                surnameInput!!.error = mContext!!.getString(R.string.activity_configuration_surname_warning)
                return VerificationError(mContext!!.getString(R.string.activity_configuration_surname_error))
            }

            HomeToWorkClient.getUser().name = nameInput!!.text.toString().trim { it <= ' ' }
            HomeToWorkClient.getUser().surname = surnameInput!!.text.toString().trim { it <= ' ' }

            return null
        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {
            Toasty.warning(mContext!!, error.errorMessage).show()
        }

        override fun onDestroyView() {
            mUnbinder!!.unbind()
            super.onDestroyView()
        }
    }

    class ConfigurationFragmentHome : Fragment(), Step, OnMapReadyCallback {

        private val FINE_LOCATION_ACCESS = 0


        @BindView(R.id.mapView)
        internal var mapView: MapView? = null
        @BindView(R.id.button_set_current_location)
        internal var buttonSetCurrentLocation: Button? = null

        private var googleMap: GoogleMap? = null
        private var mFusedLocationClient: FusedLocationProviderClient? = null
        private var lastLocation: Location? = null
        private var homeLocation: LatLng? = null
        private var mUnbinder: Unbinder? = null
        private var mContext: Context? = null

        override fun onAttach(context: Context) {
            super.onAttach(context)
            mContext = context
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val root = inflater.inflate(R.layout.fragment_conf_home, container, false)
            mUnbinder = ButterKnife.bind(this, root)

            mapView!!.onCreate(savedInstanceState)
            mapView!!.getMapAsync(this)

            return root
        }

        override fun onMapReady(googleMap: GoogleMap) {
            this.googleMap = googleMap
            this.googleMap!!.uiSettings.setAllGesturesEnabled(false)
            this.googleMap!!.uiSettings.isMyLocationButtonEnabled = false

            if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                setUpMap()
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_ACCESS)
            }

            if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext!!)
                mFusedLocationClient!!.lastLocation.addOnSuccessListener { location ->
                    lastLocation = location
                    buttonSetCurrentLocation!!.visibility = View.VISIBLE
                }
            }

        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            if (requestCode == FINE_LOCATION_ACCESS) {
                if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpMap()
                }
            }
        }

        private fun setUpMap() {
            if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                MapsInitializer.initialize(mContext!!)
                googleMap!!.isMyLocationEnabled = true
                googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(com.google.android.gms.maps.model.LatLng(41.909986, 12.3959159), 5.0f))
            }
        }

        @OnClick(R.id.button_set_address)
        internal fun setAddress() {
            val editAddressDialog = MaterialDialog.Builder(mContext!!)
                    .customView(R.layout.dialog_edit_address, false)
                    .positiveText(R.string.activity_configuration_address_save)
                    .negativeText(R.string.activity_configuration_address_discard)
                    .onPositive { dialog, which ->

                        val view = dialog.customView
                        if (view != null) {
                            checkAddressDialog(view)
                        }

                    }
                    .build()

            val view = editAddressDialog.customView

            if (view != null) {
                setupAddressDialog(view)
            }

            editAddressDialog.show()
        }

        private fun setupAddressDialog(view: View) {
            val cityInput = view.findViewById<EditText>(R.id.city_input)
            val capInput = view.findViewById<EditText>(R.id.cap_input)
            val addressInput = view.findViewById<EditText>(R.id.address_input)

            val user = HomeToWorkClient.getUser()

            if (user.address != null) {
                addressInput.setText(user.address.address)
                capInput.setText(user.address.postalCode)
                cityInput.setText(user.address.city)
            }
        }

        private fun checkAddressDialog(view: View) {

            var valid: Boolean? = true

            val cityInput = view.findViewById<EditText>(R.id.city_input)
            val capInput = view.findViewById<EditText>(R.id.cap_input)
            val addressInput = view.findViewById<EditText>(R.id.address_input)

            val addr = addressInput.text.toString()
            val city = cityInput.text.toString()
            val CAP = capInput.text.toString()

            if (addr.isEmpty()) {
                addressInput.error = getString(R.string.activity_configuration_address_warning_address)
                valid = false
            }

            if (city.isEmpty()) {
                capInput.error = getString(R.string.activity_configuration_address_warning_CAP)
                valid = false
            }

            if (CAP.isEmpty()) {
                cityInput.error = getString(R.string.activity_configuration_address_warning_city)
                valid = false
            }

            if (valid!!) {
                val latLng = Converters.addressToLatLng(context!!, "$addr, $city $CAP")
                if (latLng != null) {

                    val newAddress = Address()
                    newAddress.city = city
                    newAddress.address = addr
                    newAddress.postalCode = CAP

                    HomeToWorkClient.getUser().location = latLng
                    HomeToWorkClient.getUser().address = newAddress

                    setHomeLocation(latLng)

                } else {
                    Toasty.warning(mContext!!, getString(R.string.activity_configuration_address_error), Toast.LENGTH_LONG).show()
                }
            }
        }

        @OnClick(R.id.button_set_current_location)
        internal fun setCurrentPosition() {
            if (lastLocation != null) {
                val homeLat = lastLocation!!.latitude
                val homeLon = lastLocation!!.longitude
                val currentLatLng = LatLng(homeLat, homeLon)
                buttonSetCurrentLocation!!.visibility = View.INVISIBLE
                setHomeLocation(currentLatLng)
            }
        }

        private fun setHomeLocation(latLng: LatLng) {
            homeLocation = latLng
            googleMap!!.clear()
            googleMap!!.addMarker(MarkerOptions()
                    .position(com.google.android.gms.maps.model.LatLng(latLng.lat!!, latLng.lng!!))
                    .title(getString(R.string.home)))
                    .showInfoWindow()
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(com.google.android.gms.maps.model.LatLng(latLng.lat!!, latLng.lng!!), 15.0f))
        }

        override fun onResume() {
            mapView!!.onResume()
            super.onResume()
        }

        override fun onPause() {
            super.onPause()
            mapView!!.onPause()
        }

        override fun onLowMemory() {
            super.onLowMemory()
            mapView!!.onLowMemory()
        }

        override fun verifyStep(): VerificationError? {
            if (homeLocation == null)
                return VerificationError(mContext!!.getString(R.string.activity_configuration_company_step_warning))

            HomeToWorkClient.getUser().location = homeLocation

            return null
        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {
            Toasty.warning(mContext!!, error.errorMessage).show()
        }

        override fun onDestroyView() {
            mUnbinder!!.unbind()
            super.onDestroyView()
        }
    }

    class ConfigurationFragmentJob : Fragment(), Step {

        @BindView(R.id.loadingView)
        internal var loadingView: LinearLayout? = null
        @BindView(R.id.companySpinner)
        internal var companySpinner: Spinner? = null

        private var mCompanies: List<Company>? = null
        private var mUnbinder: Unbinder? = null
        private var mContext: Context? = null

        override fun onAttach(context: Context) {
            super.onAttach(context)
            mContext = context
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val root = inflater.inflate(R.layout.fragment_conf_job, container, false)
            mUnbinder = ButterKnife.bind(this, root)

            HomeToWorkClient.getInstance().getCompanies { companies ->
                mCompanies = companies
                initCompaniesSpinner()
            }

            companySpinner!!.requestFocus()

            return root
        }

        override fun verifyStep(): VerificationError? {

            if (companySpinner!!.selectedItem.toString() == getString(R.string.company))
                return VerificationError(mContext!!.getString(R.string.activity_configuration_company_step_warning))

            HomeToWorkClient.getUser().company = companySpinner!!.selectedItem as Company

            return null
        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {
            Toasty.warning(mContext!!, error.errorMessage).show()
        }

        override fun onDestroyView() {
            mUnbinder!!.unbind()
            super.onDestroyView()
        }

        private fun initCompaniesSpinner() {
            val companySpinnerAdapter = CompanySpinnerAdapter(activity, mCompanies)
            companySpinner!!.adapter = companySpinnerAdapter
            loadingView!!.visibility = View.GONE
        }

    }

    class ConfigurationFragmentAvatar : Fragment(), BlockingStep {

        private val PHOTO_INTENT = 0

        @BindView(R.id.propicView)
        internal var propicView: ImageView? = null

        private var propic: Bitmap? = null
        private var uploaded = false
        private var mContext: Context? = null

        override fun onAttach(context: Context) {
            super.onAttach(context)
            mContext = context
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val root = inflater.inflate(R.layout.fragment_conf_propic, container, false)
            ButterKnife.bind(this, root)

            return root
        }

        @OnClick(R.id.selectPhotoButton)
        internal fun selectPhoto() {
            if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                selectImageIntent()
            }
        }

        private fun selectImageIntent() {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent,
                    getString(R.string.activity_configuration_avatar_selection)), PHOTO_INTENT)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
            if (requestCode == PHOTO_INTENT && resultCode == Activity.RESULT_OK) {
                try {

                    val selectedImageUri = data.data
                    val bitmap = MediaStore.Images.Media.getBitmap(mContext!!.contentResolver, selectedImageUri)
                    propic = ImageTools.shrinkBitmap(bitmap, 300)
                    propicView!!.setImageBitmap(propic)
                    uploaded = false

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImageIntent()
            } /*else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }*/
        }

        override fun verifyStep(): VerificationError? {

            return null
        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {

        }

        override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback) {

            if (propic == null || uploaded) {
                callback.goToNextStep()
            } else {

                callback.stepperLayout.showProgress(mContext!!.getString(R.string.activity_configuration_avatar_upload))

                val file = Converters.bitmapToFile(context!!, propic!!)
                val decodedAvatar = ImageTools.decodeFile(file.path)
                val decodedFile = File(decodedAvatar)

                val mime = ImageTools.getMimeType(decodedFile.path)
                val mediaType = MediaType.parse(mime!!)

                val requestFile = RequestBody.create(mediaType, decodedFile)

                val filename = HomeToWorkClient.getUser().id!!.toString() + ".jpg"

                val body = MultipartBody.Part.createFormData("avatar", filename, requestFile)

                HomeToWorkClient.getInstance().uploadAvatar(body, { responseBody ->
                    callback.stepperLayout.hideProgress()
                    callback.goToNextStep()
                }) { e ->
                    callback.stepperLayout.hideProgress()
                    Toasty.error(mContext!!, mContext!!.getString(R.string.activity_configuration_avatar_upload_error)).show()
                }

            }
        }

        override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback) {

        }

        override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {
            callback.goToPrevStep()
        }
    }

    class ConfigurationFragmentSocial : Fragment(), Step {

        @BindView(R.id.facebookInput)
        internal var facebookInput: EditText? = null
        @BindView(R.id.twitterInput)
        internal var twitterInput: EditText? = null
        @BindView(R.id.telegramInput)
        internal var telegramInput: EditText? = null

        private var mUnbinder: Unbinder? = null
        private var mContext: Context? = null

        override fun onAttach(context: Context) {
            super.onAttach(context)
            mContext = context
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val root = inflater.inflate(R.layout.fragment_conf_social, container, false)
            mUnbinder = ButterKnife.bind(this, root)
            return root
        }

        override fun verifyStep(): VerificationError? {

            if (!facebookInput!!.text.toString().isEmpty())
                HomeToWorkClient.getUser().facebook = facebookInput!!.text.toString()

            if (!twitterInput!!.text.toString().isEmpty())
                HomeToWorkClient.getUser().twitter = twitterInput!!.text.toString()

            if (!telegramInput!!.text.toString().isEmpty())
                HomeToWorkClient.getUser().telegram = telegramInput!!.text.toString()

            return if (facebookInput!!.text.toString().isEmpty() && twitterInput!!.text.toString().isEmpty() && telegramInput!!.text.toString().isEmpty()) {
                VerificationError("Inserisci almeno un metodo di contatto")
            } else null

        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {
            Toasty.warning(mContext!!, error.errorMessage).show()
        }

        override fun onDestroyView() {
            mUnbinder!!.unbind()
            super.onDestroyView()
        }
    }

    class ConfigurationFragmentComplete : Fragment(), BlockingStep {

        private var mContext: Context? = null

        override fun onAttach(context: Context) {
            super.onAttach(context)
            mContext = context
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val root = inflater.inflate(R.layout.fragment_conf_completed, container, false)
            ButterKnife.bind(this, root)
            return root
        }

        override fun verifyStep(): VerificationError? {
            return null
        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {

        }

        override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback) {

        }

        override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback) {
            callback.stepperLayout.showProgress(mContext!!.getString(R.string.activity_configuration_wait))

            HomeToWorkClient.getUser().isConfigured = true

            HomeToWorkClient.getInstance().updateUser(
                    { user -> callback.complete() }
            ) { e -> callback.stepperLayout.hideProgress() }

        }

        override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {
            callback.goToPrevStep()
        }
    }

}
