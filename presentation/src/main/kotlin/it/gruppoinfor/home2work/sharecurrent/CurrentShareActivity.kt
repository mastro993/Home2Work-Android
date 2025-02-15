package it.gruppoinfor.home2work.sharecurrent

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseActivity
import it.gruppoinfor.home2work.common.events.ActiveShareEvent
import it.gruppoinfor.home2work.common.extensions.hide
import it.gruppoinfor.home2work.common.extensions.remove
import it.gruppoinfor.home2work.common.extensions.show
import it.gruppoinfor.home2work.common.extensions.showToast
import it.gruppoinfor.home2work.common.utilities.QREncoder
import it.gruppoinfor.home2work.entities.GuestStatus
import it.gruppoinfor.home2work.entities.Share
import it.gruppoinfor.home2work.entities.ShareStatus
import it.gruppoinfor.home2work.entities.ShareType
import it.gruppoinfor.home2work.user.UserActivityLauncher
import kotlinx.android.synthetic.main.activity_current_share.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.find

class CurrentShareActivity : BaseActivity<CurrentShareViewModel, CurrentShareVMFactory>() {

    private var mGuestsAdapter: GuestAdapter? = null
    private var share: Share? = null
    private var qrCodeDialog: BottomSheetDialog? = null


    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1
        const val REQ_CODE = 3434
        const val EXTRA_SHARE = "share"
    }

    override fun getVMClass(): Class<CurrentShareViewModel> {
        return CurrentShareViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_share)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        share = localUserData.currentShare
        share?.let {
            when (it.type) {
                ShareType.HOST -> initHostUI()
                ShareType.GUEST -> initGuestUI()
            }
        } ?: finish()

        observeViewState()

    }

    override fun onResume() {
        super.onResume()

        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_ongoing_share, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_cancel_share -> {
                share?.let {
                    when (it.type) {
                        ShareType.HOST -> showHostCancelDialog()
                        ShareType.GUEST -> showGuestCancelDialog()
                    }
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null) {
            val stringData = scanResult.contents.split(",")
            val shareId = java.lang.Long.parseLong(stringData[0])
            val location = Location("").apply {
                latitude = stringData[1].toDouble()
                longitude = stringData[2].toDouble()
            }
            checkShareCode(shareId, location)
        } else {
            showToast(R.string.activity_ongoing_share_invalid_code)
        }


    }

    private fun initHostUI() {

        host_layout.show()
        guest_layout.remove()

        button_show_share_code.setOnClickListener {

            qrCodeDialog = BottomSheetDialog(this)
            val sheetView = layoutInflater.inflate(R.layout.sheet_share_qr_code, null)
            qrCodeDialog?.setContentView(sheetView)
            qrCodeDialog?.show()

            val qrCodeImage = sheetView.find<ImageView>(R.id.qr_code_image_view)

            qrCodeDialog?.show()

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                val locationRequest = LocationRequest.create()
                locationRequest.numUpdates = 1
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                qrCodeImage.hide()

                mFusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val joinLocation = locationResult.lastLocation

                        val locationString = joinLocation.latitude.toString() + "," + joinLocation.longitude
                        val qrCodeBitmap = try {
                            QREncoder.encodeText("${share!!.id},$locationString")
                        } catch (e: Exception) {
                            showToast("Impossibile generare codice condivisione")
                            null
                        }

                        qrCodeBitmap?.let {
                            qrCodeImage.setImageBitmap(qrCodeBitmap)
                        } ?: qrCodeDialog?.dismiss()

                        mFusedLocationClient.removeLocationUpdates(this)

                        qrCodeImage.show()

                    }
                }, Looper.myLooper())


            }


        }


        button_complete_share_host.setOnClickListener {
            share?.let {

                when {
                    it.guests.size == 0 -> {
                        AlertDialog.Builder(this)
                                .setTitle(R.string.activity_ongoing_share_dialog_completition_error_title)
                                .setMessage(R.string.activity_ongoing_share_dialog_completition_error_content)
                                .show()
                    }
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                        val locationRequest = LocationRequest.create()
                        locationRequest.numUpdates = 1
                        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                        val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                        mFusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                val hostLocation = locationResult.lastLocation
                                viewModel.finishShare(hostLocation)
                                mFusedLocationClient.removeLocationUpdates(this)
                            }
                        }, Looper.myLooper())
                    }
                    else -> {
                    }
                }

            }
        }

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)

        guests_recycler_view.layoutManager = layoutManager
        guests_recycler_view.layoutAnimation = animation

        mGuestsAdapter = GuestAdapter(imageLoader, { guest, _ ->

            val user = guest.user
            UserActivityLauncher(
                    userId = user.id,
                    userName = user.fullName,
                    userAvatarUrl = user.avatarUrl,
                    userCompanyId = user.company!!.id,
                    userCompanyName = user.company!!.formattedName
            ).launch(this)

        }, { guest, _ ->


            val dialog = BottomSheetDialog(this)
            val sheetView = layoutInflater.inflate(R.layout.dialog_guest_options, null, false)

            dialog.setContentView(sheetView)
            dialog.show()

            sheetView.find<TextView>(R.id.guest_dialog_show_profile).setOnClickListener {
                dialog.dismiss()
                val user = guest.user
                UserActivityLauncher(
                        userId = user.id,
                        userName = user.toString(),
                        userAvatarUrl = user.avatarUrl,
                        userCompanyId = user.company!!.id,
                        userCompanyName = user.company!!.name
                ).launch(this)

            }
            sheetView.find<TextView>(R.id.guest_dialog_ban).setOnClickListener {
                dialog.dismiss()
                viewModel.banUser(guest.user.id)
            }

            with(sheetView.find<TextView>(R.id.guest_dialog_ban)) {
                if (guest.status == GuestStatus.LEAVED || guest.status == GuestStatus.COMPLETED) {
                    remove()
                } else {
                    show()
                }
            }

            true
        })

        guests_recycler_view.adapter = mGuestsAdapter

        button_complete_share_host.isEnabled = enableCompleteButton()

        with(share!!) {
            if (guests.size > 0) {
                text_empty_guest_list.remove()
                guests_recycler_view.show()
            } else {
                text_empty_guest_list.show()
                guests_recycler_view.remove()
            }

            mGuestsAdapter?.setItems(guests)
        }

    }

    private fun initGuestUI() {

        share?.let {
            host_layout.remove()
            guest_layout.show()

            button_complete_share_guest.setOnClickListener {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
                } else {
                    val intentIntegrator = IntentIntegrator(this)
                    intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                    intentIntegrator.setPrompt(getString(R.string.activity_ongoing_share_dialog_qr_prompt))
                    intentIntegrator.setOrientationLocked(false)
                    intentIntegrator.initiateScan()
                }
            }

            with(it.host) {

                avatar_view.setOnClickListener {
                    UserActivityLauncher(
                            userId = id,
                            userName = fullName,
                            userAvatarUrl = avatarUrl,
                            userCompanyId = company!!.id,
                            userCompanyName = company!!.formattedName
                    ).launch(this@CurrentShareActivity)
                }

                name_text_view.text = fullName
                job_text_view.text = company?.formattedName

                avatar_view.setAvatarURL(avatarUrl)
                avatar_view.setLevel(null)
            }

            button_complete_share_guest.isEnabled = true
        }

    }

    private fun observeViewState() {
        viewModel.errorEvent.observe(this, Observer {
            it?.let { showToast(it) }
        })

        viewModel.infoEvent.observe(this, Observer {
            it?.let { showToast(it) }
        })

        viewModel.shareCanceledEvent.observe(this, Observer {
            it?.let {
                if (it) {
                    localUserData.currentShare = null
                    finish()
                }
            }
        })

        viewModel.shareCompletedEvent.observe(this, Observer {
            it?.let {

                localUserData.currentShare = null
                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_SHARE, it)
                setResult(REQ_CODE, resultIntent)
                finish()

            }
        })

        viewModel.viewState.observe(this, Observer {
            it?.let { handleViewState(it) }
        })
    }

    private fun handleViewState(viewState: CurrentShareViewState) {
        viewState.share?.let {
            share = it

            if (it.guests.size > 0) {
                text_empty_guest_list.remove()
                header_view.show()
                guests_recycler_view.show()
            } else {
                text_empty_guest_list.show()
                header_view.remove()
                guests_recycler_view.remove()
            }

            mGuestsAdapter?.setItems(it.guests)

        }
    }

    private fun showHostCancelDialog() {

        AlertDialog.Builder(this)
                .setTitle("Interrompi condivisione")
                .setMessage("Sei sicuro di voler interrompere la condivisione corrente? Tutti i progressi verranno persi")
                .setPositiveButton(android.R.string.yes, { _, _ ->

                    viewModel.cancelShare()

                })
                .setNegativeButton(android.R.string.no, null)
                .show()

    }

    private fun showGuestCancelDialog() {


        AlertDialog.Builder(this)
                .setTitle("Annulla condivisione")
                .setMessage("Sei sicuro di voler uscire dala condivisione corrente? Tutti i progressi verranno persi")
                .setPositiveButton(android.R.string.yes, { _, _ ->

                    viewModel.leaveShare()

                })
                .setNegativeButton(android.R.string.no, null)
                .show()

    }

    private fun enableCompleteButton(): Boolean {

        share?.let {
            if (it.guests.size == 0) return false
            val uncompletedGuests = it.guests.find { guest -> guest.status == ShareStatus.CREATED }
            return uncompletedGuests == null
        } ?: return false


    }

    private fun checkShareCode(shareID: Long?, hostLocation: Location) {

        share?.let {
            if (it.id != shareID) {
                Toast.makeText(this, R.string.activity_ongoing_share_check_wrong_code, Toast.LENGTH_SHORT).show()
                return
            }
        } ?: return


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.create()
            locationRequest.numUpdates = 1
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


            mFusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val endLocation = locationResult.lastLocation
                    viewModel.completeShare(hostLocation, endLocation)
                    mFusedLocationClient.removeLocationUpdates(this)
                }
            }, Looper.myLooper())

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActiveShareEvent) {

        if (event.finished) {
            localUserData.currentShare = null
            finish()
        } else {
            qrCodeDialog?.dismiss()
            viewModel.getActiveShare()
        }

        button_complete_share_host.isEnabled = enableCompleteButton()

    }


}
