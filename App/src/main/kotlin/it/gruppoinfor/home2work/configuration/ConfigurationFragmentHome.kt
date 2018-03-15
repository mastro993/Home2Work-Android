package it.gruppoinfor.home2work.configuration

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.api.HomeToWorkClient
import it.gruppoinfor.home2work.location.FullAddress
import it.gruppoinfor.home2work.location.LatLng
import it.gruppoinfor.home2work.utils.AddressConverter
import kotlinx.android.synthetic.main.dialog_edit_address.*
import kotlinx.android.synthetic.main.fragment_conf_home.*

class ConfigurationFragmentHome : Fragment(), Step, OnMapReadyCallback {

    val PERMISSION_FINE_LOCATION = 0

    private lateinit var googleMap: GoogleMap
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var homeLocation: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_conf_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeLocation = HomeToWorkClient.user?.homeLatLng


        button_set_address.setOnClickListener {

            var addr: String? = HomeToWorkClient.user?.address?.address
            var city: String? = HomeToWorkClient.user?.address?.city
            var cap: String? = HomeToWorkClient.user?.address?.postalCode


            val dialog = AlertDialog.Builder(context!!)
                    .setTitle("Inserisci un indirizzo")
                    .setView(R.layout.dialog_edit_address)
                    //.setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok, { d, _ ->

                        var valid = true

                        if (addr.isNullOrEmpty()) {
                            address_input.error = getString(R.string.activity_configuration_address_warning_address)
                            valid = false
                        }

                        if (city.isNullOrEmpty()) {
                            cap_input.error = getString(R.string.activity_configuration_address_warning_CAP)
                            valid = false
                        }

                        if (cap.isNullOrEmpty()) {
                            city_input.error = getString(R.string.activity_configuration_address_warning_city)
                            valid = false
                        }

                        if (valid) {

                            d.dismiss()

                            AddressConverter.addressToLatLng(context!!, "$addr, $city $cap",
                                    OnSuccessListener { latLng ->
                                        val newAddress = FullAddress()
                                        newAddress.city = city!!
                                        newAddress.address = addr!!
                                        newAddress.postalCode = cap!!

                                        HomeToWorkClient.user?.homeLatLng = latLng
                                        HomeToWorkClient.user?.address = newAddress

                                        setHomeLocation(latLng)
                                    }, OnFailureListener {
                                Toast.makeText(context!!, R.string.activity_configuration_address_error, Toast.LENGTH_SHORT).show()
                            })
                        }

                    }).show()

            dialog.address_input.setText(addr)
            dialog.cap_input.setText(cap)
            dialog.city_input.setText(city)

            dialog.address_input.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    addr = p0.toString()
                }
            })
            dialog.city_input.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    city = p0.toString()
                }
            })

            dialog.cap_input.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    cap = p0.toString()
                }
            })


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

            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_FINE_LOCATION)

        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == PERMISSION_FINE_LOCATION) {
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
            HomeToWorkClient.user?.homeLatLng = homeLocation!!
            null
        } else {
            VerificationError("Devi impostare il tuo indirizzo di casa prima di poter continuare")
        }
    }

    override fun onSelected() {

    }

    override fun onError(error: VerificationError) {

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

}