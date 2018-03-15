package it.gruppoinfor.home2work.shares

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.zxing.integration.android.IntentIntegrator
import it.gruppoinfor.home2work.Constants
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.extensions.showToast
import it.gruppoinfor.home2work.firebase.NewMessageEvent
import it.gruppoinfor.home2work.firebase.OngoingShareEvent
import it.gruppoinfor.home2work.user.UserActivity
import it.gruppoinfor.home2work.user.UserActivityArgs
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.LatLng
import it.gruppoinfor.home2workapi.share.Guest
import it.gruppoinfor.home2workapi.share.Share
import kotlinx.android.synthetic.main.activity_ongoing_share.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor

class OngoingShareActivity : AppCompatActivity(), OngoingShareView {

    private val mOngoingSharePresenter: OngoingSharePresenter = OngoingSharePresenterImpl(this)
    private var mGuestsAdapter: GuestAdapter? = null
    private lateinit var mShare: Share
    private var qrCodeDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ongoing_share)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mShare = HomeToWorkClient.ongoingShare!!
        initUI()

    }

    override fun onResume() {
        super.onResume()

        mOngoingSharePresenter.onResume()


        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)

        mOngoingSharePresenter.onPause()
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
                when (mShare.type) {
                    Share.Type.DRIVER -> showHostCancelDialog()
                    Share.Type.GUEST -> showGuestCancelDialog()
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onMessageEvent(event: OngoingShareEvent) {

        qrCodeDialog?.dismiss()
        mOngoingSharePresenter.refreshGuests()

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null) {
            val stringData = scanResult.contents.split(",")
            val shareId = java.lang.Long.parseLong(stringData[0])
            val latLng = LatLng(stringData[1].toDouble(), stringData[2].toDouble())
            checkShareCode(shareId, latLng)
        } else{
            showToast(R.string.activity_ongoing_share_invalid_code)
        }


    }

    private fun initUI() {

        when (mShare.type) {
            Share.Type.DRIVER -> initHostUI()
            Share.Type.GUEST -> initGuestUI()
        }

    }

    private fun initHostUI() {

        host_layout.visibility = View.VISIBLE
        guest_layout.visibility = View.GONE

        layout_show_code.setOnClickListener {

            qrCodeDialog = BottomSheetDialog(this)
            val sheetView = layoutInflater.inflate(R.layout.dialog_share_qr_code, null)
            qrCodeDialog?.setContentView(sheetView)
            qrCodeDialog?.show()

            val qrCodeImage = sheetView.find<ImageView>(R.id.qr_code_image_view)
            val loadingView = sheetView.find<FrameLayout>(R.id.loading_view)

            loadingView.visibility = View.VISIBLE

            qrCodeDialog?.show()

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                val locationRequest = LocationRequest.create()
                locationRequest.numUpdates = 1
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                loadingView.visibility = View.VISIBLE

                mFusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val joinLocation = locationResult.lastLocation
                        val qrCodeBitmap = mOngoingSharePresenter.getShareQRCode(joinLocation)
                        if (qrCodeBitmap != null) {
                            qrCodeImage.setImageBitmap(qrCodeBitmap)
                            loadingView.visibility = View.INVISIBLE
                        } else {
                            qrCodeDialog?.dismiss()
                        }
                        mFusedLocationClient.removeLocationUpdates(this)
                    }
                }, Looper.myLooper())


            }

        }

        button_complete_share.setOnClickListener {
            if (mShare.guests.size == 0) {

                AlertDialog.Builder(this)
                        .setTitle(R.string.activity_ongoing_share_dialog_completition_error_title)
                        .setMessage(R.string.activity_ongoing_share_dialog_completition_error_content)
                        .show()

            } else {

                mOngoingSharePresenter.finishShare()

            }
        }

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)

        guests_recycler_view.layoutManager = layoutManager
        guests_recycler_view.layoutAnimation = animation

        mGuestsAdapter = GuestAdapter(this, this)

        guests_recycler_view.adapter = mGuestsAdapter

        button_complete_share.isEnabled = enableCompleteButton()

    }

    private fun initGuestUI() {

        host_layout.visibility = View.GONE
        guest_layout.visibility = View.VISIBLE

        layout_host.setOnClickListener {
            startActivity(intentFor<UserActivity>(Constants.EXTRA_USER to mShare.host))
        }

        button_complete_share.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), Constants.CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                val intentIntegrator = IntentIntegrator(this)
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                intentIntegrator.setPrompt(getString(R.string.activity_ongoing_share_dialog_qr_prompt))
                intentIntegrator.setOrientationLocked(false)
                intentIntegrator.initiateScan()
            }
        }

        name_text_view.text = mShare.host.toString()
        job_text_view.text = mShare.host?.company.toString()

        avatar_view.setAvatarURL(mShare.host?.avatarURL)
        avatar_view.setLevel(null)

        button_complete_share.isEnabled = true

    }

    private fun showHostCancelDialog() {

        AlertDialog.Builder(this)
                .setTitle("Interrompi condivisione")
                .setMessage("Sei sicuro di voler interrompere la condivisione corrente? Tutti i progressi verranno persi")
                .setPositiveButton(android.R.string.yes, { _, _ ->

                    mOngoingSharePresenter.cancelOngoingShare()

                })
                .setNegativeButton(android.R.string.no, null)
                .show()

    }

    private fun showGuestCancelDialog() {


        AlertDialog.Builder(this)
                .setTitle("Annulla condivisione")
                .setMessage("Sei sicuro di voler uscire dala condivisione corrente? Tutti i progressi verranno persi")
                .setPositiveButton(android.R.string.yes, { _, _ ->

                    mOngoingSharePresenter.leaveOngoingShare()

                })
                .setNegativeButton(android.R.string.no, null)
                .show()

    }

    private fun enableCompleteButton(): Boolean {

        if (mShare.guests.size == 0) return false
        val uncompletedGuests = mShare.guests.find { guest -> guest.status == Guest.Status.JOINED }
        return uncompletedGuests == null

    }

    private fun checkShareCode(shareID: Long?, hostLocation: LatLng) {

        if (mShare.id != shareID) {
            Toast.makeText(this, R.string.activity_ongoing_share_check_wrong_code, Toast.LENGTH_SHORT).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.create()
            locationRequest.numUpdates = 1
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


            mFusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val endLocation = locationResult.lastLocation
                    mOngoingSharePresenter.completeShare(hostLocation,endLocation)
                    mFusedLocationClient.removeLocationUpdates(this)
                }
            }, Looper.myLooper())

        }
    }

    override fun setGuests(share: Share) {

        if(share.guests.size > 0){
            empty_view.visibility = View.GONE
            header_view.visibility = View.VISIBLE
            guests_recycler_view.visibility = View.VISIBLE
        } else {
            empty_view.visibility = View.VISIBLE
            header_view.visibility = View.GONE
            guests_recycler_view.visibility = View.GONE
        }

        mShare = share
        mGuestsAdapter?.setItems(share.guests)
        mGuestsAdapter?.notifyDataSetChanged()
    }

    override fun onGuestBanned() {
        mOngoingSharePresenter.refreshGuests()
    }

    override fun onGuestClick(position: Int, guest: Guest) {
        val user = guest.user!!
        UserActivityArgs(
                userId = user.id,
                userName = user.toString(),
                userAvatarUrl = user.avatarURL,
                userCompanyId = user.company.id,
                userCompanyName = user.company.name
        ).launch(this)

    }

    override fun onGuestLongClick(position: Int, guest: Guest) {
        val dialog = BottomSheetDialog(this)
        val sheetView = layoutInflater.inflate(R.layout.dialog_guest_options, null)

        dialog.setContentView(sheetView)
        dialog.show()

        sheetView.find<TextView>(R.id.guest_dialog_show_profile).setOnClickListener {
            dialog.dismiss()
            startActivity(intentFor<UserActivity>(Constants.EXTRA_USER to guest.user))
        }
        sheetView.find<TextView>(R.id.guest_dialog_ban).setOnClickListener {
            dialog.dismiss()
            mOngoingSharePresenter.banGuest(guest.user?.id!!)
        }

        sheetView.find<TextView>(R.id.guest_dialog_ban).visibility = if (guest.status == Guest.Status.CANCELED || guest.status == Guest.Status.COMPLETED) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun showErrorMessage(errorMessage: String) {
        showToast(errorMessage)
    }

    override fun onShareCanceled() {
        finish()
    }

    override fun onShareCompleted() {
        showToast("Condivisione completata con successo!")
        finish()
    }

}
