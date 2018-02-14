package it.gruppoinfor.home2work.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.EditText
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
        val id = item.itemId
        if (id == R.id.action_logout) {
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

    fun initUI() {
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
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

        private lateinit var mContext: Context

        override fun onAttach(context: Context) {
            mContext = context
            super.onAttach(context)
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val root = inflater.inflate(R.layout.fragment_conf_name, container, false)

            input_name.setText(HomeToWorkClient.getUser().name)
            input_surname.setText(HomeToWorkClient.getUser().surname)

            return root
        }

        override fun verifyStep(): VerificationError? {

            if (input_name.text.isEmpty()) {
                input_name.error = mContext.getString(R.string.activity_configuration_name_warning)
                return VerificationError(mContext.getString(R.string.activity_configuration_name_error))
            }

            if (input_surname.text.isEmpty()) {
                input_surname.error = mContext.getString(R.string.activity_configuration_surname_warning)
                return VerificationError(mContext.getString(R.string.activity_configuration_surname_error))
            }

            HomeToWorkClient.getUser().name = input_name.text.toString().trim { it <= ' ' }
            HomeToWorkClient.getUser().surname = input_surname.text.toString().trim { it <= ' ' }

            return null
        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {
            Toasty.warning(mContext, error.errorMessage).show()
        }

    }

    class ConfigurationFragmentHome : Fragment(), Step, OnMapReadyCallback {

        private val FINE_LOCATION_ACCESS = 0

        private lateinit var googleMap: GoogleMap
        private var mFusedLocationClient: FusedLocationProviderClient? = null
        private var lastLocation: Location? = null
        private var homeLocation: LatLng? = null
        private lateinit var mContext: Context

        override fun onAttach(context: Context) {
            super.onAttach(context)
            mContext = context
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val root = inflater.inflate(R.layout.fragment_conf_home, container, false)

            button_set_address.setOnClickListener {
                val editAddressDialog = MaterialDialog.Builder(mContext)
                        .customView(R.layout.dialog_edit_address, false)
                        .positiveText(R.string.activity_configuration_address_save)
                        .negativeText(R.string.activity_configuration_address_discard)
                        .onPositive { dialog, _ ->

                            val view = dialog.customView
                            if (view != null) {
                                checkAddressDialog()
                            }

                        }
                        .build()

                val view = editAddressDialog.customView

                if (view != null) {
                    setupAddressDialog(view)
                }

                editAddressDialog.show()
            }

            button_set_current_location.setOnClickListener {
                if (lastLocation != null) {
                    val homeLat = lastLocation!!.latitude
                    val homeLon = lastLocation!!.longitude
                    val currentLatLng = LatLng(homeLat, homeLon)
                    button_set_current_location.visibility = View.INVISIBLE
                    setHomeLocation(currentLatLng)
                }
            }

            map_view.onCreate(savedInstanceState)
            map_view.getMapAsync(this)

            return root
        }

        override fun onMapReady(googleMap: GoogleMap) {
            this.googleMap = googleMap
            this.googleMap.uiSettings.setAllGesturesEnabled(false)
            this.googleMap.uiSettings.isMyLocationButtonEnabled = false

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                setUpMap()
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_ACCESS)
            }

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
                mFusedLocationClient!!.lastLocation.addOnSuccessListener { location ->
                    lastLocation = location
                    button_set_current_location.visibility = View.VISIBLE
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

        override fun onResume() {
            map_view.onResume()
            super.onResume()
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
            if (homeLocation == null)
                return VerificationError(mContext.getString(R.string.activity_configuration_company_step_warning))

            HomeToWorkClient.getUser().location = homeLocation

            return null
        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {
            Toasty.warning(mContext, error.errorMessage).show()
        }

        private fun setUpMap() {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                MapsInitializer.initialize(mContext)
                googleMap.isMyLocationEnabled = true
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(com.google.android.gms.maps.model.LatLng(41.909986, 12.3959159), 5.0f))
            }
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
                AddressConverter.addressToLatLng(
                        mContext,
                        "$addr, $city $cap",
                        OnSuccessListener { latLng ->
                            val newAddress = Address()
                            newAddress.city = city
                            newAddress.address = addr
                            newAddress.postalCode = cap

                            HomeToWorkClient.getUser().location = latLng
                            HomeToWorkClient.getUser().address = newAddress

                            setHomeLocation(latLng)
                        }, OnFailureListener {
                    Toasty.warning(mContext, getString(R.string.activity_configuration_address_error), Toast.LENGTH_LONG).show()
                })
            }
        }

        private fun setHomeLocation(latLng: LatLng) {
            homeLocation = latLng
            googleMap.clear()
            googleMap.addMarker(MarkerOptions()
                    .position(com.google.android.gms.maps.model.LatLng(latLng.lat!!, latLng.lng!!))
                    .title(getString(R.string.home)))
                    .showInfoWindow()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(com.google.android.gms.maps.model.LatLng(latLng.lat!!, latLng.lng!!), 15.0f))
        }


    }

    class ConfigurationFragmentJob : Fragment(), Step {

        private lateinit var mCompanies: List<Company>
        private lateinit var mContext: Context

        override fun onAttach(context: Context) {
            super.onAttach(context)
            mContext = context
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val root = inflater.inflate(R.layout.fragment_conf_job, container, false)

            HomeToWorkClient.getInstance().getCompanies { companies ->
                mCompanies = companies
                initCompaniesSpinner()
            }

            companySpinner.requestFocus()

            return root
        }

        override fun verifyStep(): VerificationError? {

            if (companySpinner!!.selectedItem.toString() == getString(R.string.company))
                return VerificationError(mContext.getString(R.string.activity_configuration_company_step_warning))

            HomeToWorkClient.getUser().company = companySpinner!!.selectedItem as Company

            return null
        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {
            Toasty.warning(mContext, error.errorMessage).show()
        }

        private fun initCompaniesSpinner() {
            val companySpinnerAdapter = CompanySpinnerAdapter(activity as Activity, mCompanies)
            companySpinner.adapter = companySpinnerAdapter
            loadingView.visibility = View.GONE
        }

    }

    class ConfigurationFragmentAvatar : Fragment(), BlockingStep {

        private val PHOTO_INTENT = 0

        private var propic: Bitmap? = null
        private var uploaded = false
        private lateinit var mContext: Context

        override fun onAttach(context: Context) {
            super.onAttach(context)
            mContext = context
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val root = inflater.inflate(R.layout.fragment_conf_propic, container, false)

            selectPhotoButton.setOnClickListener {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
                } else {
                    selectImageIntent()
                }
            }

            return root
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
                    val bitmap = MediaStore.Images.Media.getBitmap(mContext.contentResolver, selectedImageUri)
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

                callback.stepperLayout.showProgress(mContext.getString(R.string.activity_configuration_avatar_upload))

                val file = ImageUtils.bitmapToFile(context!!, propic!!)
                val decodedAvatar = ImageUtils.decodeFile(file.path)
                val decodedFile = File(decodedAvatar)

                val mime = ImageUtils.getMimeType(decodedFile.path)
                val mediaType = MediaType.parse(mime!!)

                val requestFile = RequestBody.create(mediaType, decodedFile)

                val filename = HomeToWorkClient.getUser().id!!.toString() + ".jpg"

                val body = MultipartBody.Part.createFormData("avatar", filename, requestFile)

                HomeToWorkClient.getInstance().uploadAvatar(body, {
                    callback.stepperLayout.hideProgress()
                    callback.goToNextStep()
                }) {
                    callback.stepperLayout.hideProgress()
                    Toasty.error(mContext, mContext.getString(R.string.activity_configuration_avatar_upload_error)).show()
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

        private lateinit var mContext: Context

        override fun onAttach(context: Context) {
            super.onAttach(context)
            mContext = context
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_conf_social, container, false)
        }

        override fun verifyStep(): VerificationError? {

            if (!facebookInput.text.isEmpty())
                HomeToWorkClient.getUser().facebook = facebookInput.text.toString()

            if (!twitterInput.text.isEmpty())
                HomeToWorkClient.getUser().twitter = twitterInput.text.toString()

            if (!telegramInput.text.isEmpty())
                HomeToWorkClient.getUser().telegram = telegramInput.text.toString()

            return if (facebookInput.text.isEmpty() && twitterInput!!.text.isEmpty() && telegramInput!!.text.isEmpty()) {
                VerificationError("Inserisci almeno un metodo di contatto")
            } else null

        }

        override fun onSelected() {

        }

        override fun onError(error: VerificationError) {
            Toasty.warning(mContext, error.errorMessage).show()
        }

    }

    class ConfigurationFragmentComplete : Fragment(), BlockingStep {

        private lateinit var mContext: Context

        override fun onAttach(context: Context) {
            super.onAttach(context)
            mContext = context
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
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
            callback.stepperLayout.showProgress(mContext.getString(R.string.activity_configuration_wait))

            HomeToWorkClient.getUser().isConfigured = true

            HomeToWorkClient.getInstance().updateUser(
                    {
                        callback.complete()
                    }
            ) {
                callback.stepperLayout.hideProgress()
            }

        }

        override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {
            callback.goToPrevStep()
        }
    }

}
