package it.gruppoinfor.home2work.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.Step
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.adapters.CompanySpinnerAdapter
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2work.utils.AddressConverter
import it.gruppoinfor.home2work.utils.ImageUtils
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Address
import it.gruppoinfor.home2workapi.model.Company
import it.gruppoinfor.home2workapi.model.LatLng
import kotlinx.android.synthetic.main.activity_configuration.*
import kotlinx.android.synthetic.main.dialog_edit_address.*
import kotlinx.android.synthetic.main.fragment_conf_home.*
import kotlinx.android.synthetic.main.fragment_conf_job.*
import kotlinx.android.synthetic.main.fragment_conf_name.*
import kotlinx.android.synthetic.main.fragment_conf_propic.*
import kotlinx.android.synthetic.main.fragment_conf_social.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ConfigurationActivity : AppCompatActivity(), StepperLayout.StepperListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_configuration)
        initUI()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_configuration, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.dialog_logout_title)
                builder.setMessage(R.string.dialog_logout_content)
                builder.setPositiveButton(R.string.dialog_logout_confirm) { _, _ ->

                    SessionManager.clearSession(this@ConfigurationActivity)
                    val intent = Intent(this@ConfigurationActivity, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()

                }
                builder.setNegativeButton(R.string.dialog_logout_decline, null)
                builder.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCompleted(completeButton: View) {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onError(verificationError: VerificationError) {

    }

    override fun onStepSelected(newStepPosition: Int) {
        //Toast.makeText(this, "onStepSelected! -> " + newStepPosition, Toast.LENGTH_SHORT).show();
    }

    override fun onReturn() {
        finish()
    }

    private fun initUI() {

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        stepperLayout.adapter = ConfigurationStepsAdapter(supportFragmentManager, this)
        stepperLayout.setOffscreenPageLimit(5)
        stepperLayout.setListener(this)

    }

    class ConfigurationStepsAdapter internal constructor(fm: FragmentManager, context: Context) : AbstractFragmentStepAdapter(fm, context) {

        override fun createStep(position: Int): Step {

            val fragment: Fragment = when (position) {
                0 -> ConfigurationFragmentStart()
                1 -> ConfigurationFragmentName()
                2 -> ConfigurationFragmentHome()
                3 -> ConfigurationFragmentJob()
                4 -> ConfigurationFragmentAvatar()
                5 -> ConfigurationFragmentSocial()
                6 -> ConfigurationFragmentComplete()
                else -> ConfigurationFragmentStart()
            }

            val bundle = Bundle()
            bundle.putInt(CURRENT_STEP_POSITION_KEY, position)
            fragment.arguments = bundle

            return fragment as Step

        }

        override fun getCount(): Int {
            return 7
        }

        companion object {
            private const val CURRENT_STEP_POSITION_KEY = "current_step"
        }

    }

    class ConfigurationFragmentStart : Fragment(), Step {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

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

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            return inflater.inflate(R.layout.fragment_conf_name, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            initUI()
        }

        override fun verifyStep(): VerificationError? {

            if (input_name.text.isEmpty()) {
                input_name.error = getString(R.string.activity_configuration_name_warning)
                return VerificationError(getString(R.string.activity_configuration_name_error))
            }

            if (input_surname.text.isEmpty()) {
                input_surname.error = getString(R.string.activity_configuration_surname_warning)
                return VerificationError(getString(R.string.activity_configuration_surname_error))
            }

            HomeToWorkClient.user?.name = input_name.text.toString().trim { it <= ' ' }
            HomeToWorkClient.user?.surname = input_surname.text.toString().trim { it <= ' ' }

            return null
        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {

            Toasty.warning(context!!, error.errorMessage).show()

        }

        private fun initUI() {

            input_name.setText(HomeToWorkClient.user!!.name)
            input_surname.setText(HomeToWorkClient.user!!.surname)

        }

    }

    class ConfigurationFragmentHome : Fragment(), Step, OnMapReadyCallback {

        private lateinit var googleMap: GoogleMap
        private lateinit var mFusedLocationClient: FusedLocationProviderClient
        private var homeLocation: LatLng? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            return inflater.inflate(R.layout.fragment_conf_home, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            button_set_address.setOnClickListener {
                val editAddressDialog = MaterialDialog.Builder(context!!)
                        .customView(R.layout.dialog_edit_address, false)
                        .positiveText(R.string.activity_configuration_address_save)
                        .negativeText(R.string.activity_configuration_address_discard)
                        .onPositive { _, _ ->
                            checkAddressDialog()
                        }
                        .build()

                address_input.setText(HomeToWorkClient.user?.address?.address)
                cap_input.setText(HomeToWorkClient.user?.address?.postalCode)
                city_input.setText(HomeToWorkClient.user?.address?.city)

                editAddressDialog.show()
            }

            map_view.onCreate(savedInstanceState)
            map_view.getMapAsync(this)

        }

        override fun onMapReady(googleMap: GoogleMap) {

            this.googleMap = googleMap
            this.googleMap.uiSettings.setAllGesturesEnabled(false)
            this.googleMap.uiSettings.isMyLocationButtonEnabled = false

            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
                mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    button_set_current_location.visibility = View.VISIBLE
                    button_set_current_location.setOnClickListener {
                        val homeLat = location.latitude
                        val homeLon = location.longitude
                        val currentLatLng = LatLng(homeLat, homeLon)
                        button_set_current_location.visibility = View.INVISIBLE
                        setHomeLocation(currentLatLng)
                    }
                }

                setUpMap()

            } else {

                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Const.PERMISSION_FINE_LOCATION)

            }

        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

            if (requestCode == Const.PERMISSION_FINE_LOCATION) {
                if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpMap()
                }
            }

        }

        override fun onResume() {
            super.onResume()

            map_view.onResume()

        }

        override fun onPause() {
            super.onPause()

            map_view.onPause()

        }

        override fun onLowMemory() {
            super.onLowMemory()

            map_view.onLowMemory()

        }

        override fun verifyStep(): VerificationError? {

            return if (homeLocation != null) {
                HomeToWorkClient.user?.location = homeLocation!!
                null
            } else {
                VerificationError(getString(R.string.activity_configuration_company_step_warning))
            }
        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {

            Toasty.warning(context!!, error.errorMessage).show()

        }

        private fun setHomeLocation(latLng: LatLng) {

            homeLocation = latLng
            googleMap.clear()
            googleMap.addMarker(MarkerOptions()
                    .position(com.google.android.gms.maps.model.LatLng(latLng.lat, latLng.lng))
                    .title(getString(R.string.home)))
                    .showInfoWindow()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(com.google.android.gms.maps.model.LatLng(latLng.lat, latLng.lng), 15.0f))

        }

        private fun setUpMap() {

            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                MapsInitializer.initialize(context!!)
                googleMap.isMyLocationEnabled = true
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(com.google.android.gms.maps.model.LatLng(41.909986, 12.3959159), 5.0f))
            }

        }

        private fun checkAddressDialog() {

            var valid = true

            val addr = address_input.text.toString()
            val city = city_input.text.toString()
            val cap = cap_input.text.toString()

            if (addr.isEmpty()) {
                address_input.error = getString(R.string.activity_configuration_address_warning_address)
                valid = false
            }

            if (city.isEmpty()) {
                cap_input.error = getString(R.string.activity_configuration_address_warning_CAP)
                valid = false
            }

            if (cap.isEmpty()) {
                city_input.error = getString(R.string.activity_configuration_address_warning_city)
                valid = false
            }

            if (valid) {
                AddressConverter.addressToLatLng(context!!, "$addr, $city $cap",
                        OnSuccessListener { latLng ->
                            val newAddress = Address()
                            newAddress.city = city
                            newAddress.address = addr
                            newAddress.postalCode = cap

                            HomeToWorkClient.user!!.location = latLng
                            HomeToWorkClient.user!!.address = newAddress

                            setHomeLocation(latLng)
                        }, OnFailureListener {
                    Toasty.warning(context!!, getString(R.string.activity_configuration_address_error), Toast.LENGTH_LONG).show()
                })
            }

        }

    }

    class ConfigurationFragmentJob : Fragment(), Step {

        private lateinit var mCompanies: ArrayList<Company>

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            return inflater.inflate(R.layout.fragment_conf_job, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            HomeToWorkClient.getInstance().getCompanies(OnSuccessListener {
                mCompanies = it
                val companySpinnerAdapter = CompanySpinnerAdapter(activity as Activity, mCompanies)
                companySpinner.adapter = companySpinnerAdapter
                loadingView.visibility = View.GONE
            })

            companySpinner.requestFocus()

        }

        override fun verifyStep(): VerificationError? {

            if (companySpinner!!.selectedItem.toString() == getString(R.string.company))
                return VerificationError(getString(R.string.activity_configuration_company_step_warning))

            HomeToWorkClient.user?.company = companySpinner!!.selectedItem as Company

            return null
        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {

            Toasty.warning(context!!, error.errorMessage).show()

        }

    }

    class ConfigurationFragmentAvatar : Fragment(), BlockingStep {

        private var propic: Bitmap? = null
        private var uploaded = false

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            return inflater.inflate(R.layout.fragment_conf_propic, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            selectPhotoButton.setOnClickListener {
                if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
                } else {
                    selectImageIntent()
                }
            }

        }

        private fun selectImageIntent() {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent,
                    getString(R.string.activity_configuration_avatar_selection)), Const.REQ_CAMERA)

        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

            if (requestCode == Const.REQ_CAMERA && resultCode == Activity.RESULT_OK) {
                try {

                    val selectedImageUri = data.data
                    val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, selectedImageUri)
                    propic = ImageUtils.shrinkBitmap(bitmap, 300)
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
            }

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
                callback.stepperLayout.showProgress(getString(R.string.activity_configuration_avatar_upload))

                val file = ImageUtils.bitmapToFile(context!!, propic!!)
                val decodedAvatar = ImageUtils.decodeFile(file.path)
                val decodedFile = File(decodedAvatar)

                val mime = ImageUtils.getMimeType(decodedFile.path)
                val mediaType = MediaType.parse(mime!!)

                val requestFile = RequestBody.create(mediaType, decodedFile)

                val filename = "${HomeToWorkClient.user?.id}.jpg"

                val body = MultipartBody.Part.createFormData("avatar", filename, requestFile)

                HomeToWorkClient.getInstance().uploadAvatar(body, OnSuccessListener {
                    callback.stepperLayout.hideProgress()
                    callback.goToNextStep()
                }, OnFailureListener {
                    callback.stepperLayout.hideProgress()
                    Toasty.error(context!!, getString(R.string.activity_configuration_avatar_upload_error)).show()
                })

            }
        }

        override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback) {

        }

        override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {

            callback.goToPrevStep()

        }
    }

    class ConfigurationFragmentSocial : Fragment(), Step {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            return inflater.inflate(R.layout.fragment_conf_social, container, false)
        }

        override fun verifyStep(): VerificationError? {

            if (!facebookInput.text.isEmpty())
                HomeToWorkClient.user?.facebook = facebookInput.text.toString()

            if (!twitterInput.text.isEmpty())
                HomeToWorkClient.user?.twitter = twitterInput.text.toString()

            if (!telegramInput.text.isEmpty())
                HomeToWorkClient.user?.telegram = telegramInput.text.toString()

            return if (facebookInput.text.isEmpty() && twitterInput!!.text.isEmpty() && telegramInput!!.text.isEmpty()) {
                VerificationError("Inserisci almeno un metodo di contatto")
            } else null

        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {

            Toasty.warning(context!!, error.errorMessage).show()

        }

    }

    class ConfigurationFragmentComplete : Fragment(), BlockingStep {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            return inflater.inflate(R.layout.fragment_conf_completed, container, false)
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

            callback.stepperLayout.showProgress(getString(R.string.activity_configuration_wait))

            HomeToWorkClient.user?.isConfigured = true

            HomeToWorkClient.getInstance().updateUser(
                    OnSuccessListener {
                        callback.complete()
                    }, OnFailureListener {
                callback.stepperLayout.hideProgress()
            })

        }

        override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {

            callback.goToPrevStep()

        }
    }

}
