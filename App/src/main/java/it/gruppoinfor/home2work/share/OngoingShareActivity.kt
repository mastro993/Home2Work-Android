package it.gruppoinfor.home2work.share

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.zxing.integration.android.IntentIntegrator
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.user.UserActivity
import it.gruppoinfor.home2work.firebase.MessagingService
import it.gruppoinfor.home2work.Constants
import it.gruppoinfor.home2work.utils.QREncoder
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.common.LatLng
import it.gruppoinfor.home2workapi.share.Guest
import it.gruppoinfor.home2workapi.share.Share
import kotlinx.android.synthetic.main.activity_ongoing_share.*
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor

class OngoingShareActivity : AppCompatActivity() {

    private lateinit var mShare: Share
    private var qrCodeDialog: BottomSheetDialog? = null

    private val mMessageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            if (qrCodeDialog != null) qrCodeDialog?.dismiss()
            refreshGuests()

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ongoing_share)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mShare = intent.getSerializableExtra(Constants.EXTRA_SHARE) as Share
        initUI()

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

    override fun onStart() {
        super.onStart()

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                IntentFilter(MessagingService.SHARE_JOIN_REQUEST)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                IntentFilter(MessagingService.SHARE_COMPLETE_REQUEST)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                IntentFilter(MessagingService.SHARE_LEAVE_REQUEST)
        )

    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onStop()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null) {
            val stringData = scanResult.contents.split(",")
            val shareId = java.lang.Long.parseLong(stringData[0])
            val latLng = LatLng(stringData[1].toDouble(), stringData[2].toDouble())
            checkShareCode(shareId, latLng)
        } else
            Toast.makeText(this, R.string.activity_ongoing_share_invalid_code, Toast.LENGTH_SHORT).show()

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

                val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                mFusedLocationClient.lastLocation.addOnSuccessListener { location ->

                    if (location == null) {
                        qrCodeDialog?.dismiss()
                        Toast.makeText(this@OngoingShareActivity, R.string.activity_ongoing_share_code_unavailable, Toast.LENGTH_SHORT).show()
                    } else {
                        val latlngString = "${location.latitude},${location.longitude}"

                        HomeToWorkClient.createNewShare(OnSuccessListener { share ->
                            try {
                                val bitmap = QREncoder.encodeText("${share.id},$latlngString")
                                qrCodeImage.setImageBitmap(bitmap)
                                loadingView.visibility = View.INVISIBLE
                            } catch (e: Exception) {
                                qrCodeDialog?.dismiss()
                                Toast.makeText(this@OngoingShareActivity, R.string.activity_ongoing_share_code_unavailable, Toast.LENGTH_SHORT).show()
                            }
                        }, OnFailureListener {
                            qrCodeDialog?.dismiss()
                            Toast.makeText(this@OngoingShareActivity, R.string.activity_ongoing_share_code_unavailable, Toast.LENGTH_SHORT).show()
                        })
                    }

                }

            }

        }


        button_complete_share.setOnClickListener {
            if (mShare.guests.size == 0) {

                AlertDialog.Builder(this)
                        .setTitle(R.string.activity_ongoing_share_dialog_completition_error_title)
                        .setMessage(R.string.activity_ongoing_share_dialog_completition_error_content)
                        .show()

            } else {

                HomeToWorkClient.finishShare(mShare, OnSuccessListener {
                    Toast.makeText(this@OngoingShareActivity, R.string.activity_ongoing_share_dialog_completition_success, Toast.LENGTH_SHORT).show()
                    finish()
                }, OnFailureListener { e ->
                    Toast.makeText(this@OngoingShareActivity, R.string.activity_ongoing_share_dialog_completition_error, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                })

            }
        }

        if (mShare.guests.size == 0) {

            empty_view.visibility = View.VISIBLE
            header_view.visibility = View.GONE
            guests_recycler_view.visibility = View.GONE

        } else {

            empty_view.visibility = View.GONE
            header_view.visibility = View.VISIBLE
            guests_recycler_view.visibility = View.VISIBLE

            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)

            guests_recycler_view.layoutManager = layoutManager
            guests_recycler_view.layoutAnimation = animation

            val mShareGuestsAdapter = ShareGuestsAdapter(this, mShare.guests)

            mShareGuestsAdapter.getClickPosition().subscribe {position ->

                startActivity(intentFor<UserActivity>(Constants.EXTRA_USER to mShare.guests[position]))

            }
            mShareGuestsAdapter.getLongClickPosition().subscribe {position ->

                val dialog = BottomSheetDialog(this)
                val sheetView = layoutInflater.inflate(R.layout.dialog_guest_options, null)

                dialog.setContentView(sheetView)
                dialog.show()

                sheetView.find<TextView>(R.id.guest_dialog_show_profile).setOnClickListener {
                    dialog.dismiss()
                    startActivity(intentFor<UserActivity>(Constants.EXTRA_USER to mShare.guests[position].user))
                }
                sheetView.find<TextView>(R.id.guest_dialog_ban).setOnClickListener {
                    dialog.dismiss()
                    HomeToWorkClient.banGuestFromShare(mShare.id, mShare.guests[position].user?.id, OnSuccessListener { share ->
                        mShare = share
                        initUI()
                    }, OnFailureListener { it.printStackTrace() })
                }

                sheetView.find<TextView>(R.id.guest_dialog_ban).visibility = if (mShare.guests[position].status == Guest.Status.CANCELED) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

            }

            guests_recycler_view.adapter = mShareGuestsAdapter

        }

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

                    HomeToWorkClient.cancelShare(mShare.id, OnSuccessListener {

                        Answers.getInstance().logCustom(CustomEvent("Condivisione annullata"))
                        finish()

                    }, OnFailureListener { e ->

                        Toast.makeText(this@OngoingShareActivity, R.string.activity_signin_server_error, Toast.LENGTH_SHORT).show()
                        e.printStackTrace()

                    })

                })
                .setNegativeButton(android.R.string.no, null)
                .show()

    }

    private fun showGuestCancelDialog() {


        AlertDialog.Builder(this)
                .setTitle("Annulla condivisione")
                .setMessage("Sei sicuro di voler uscire dala condivisione corrente? Tutti i progressi verranno persi")
                .setPositiveButton(android.R.string.yes, { _, _ ->

                    HomeToWorkClient.leaveShare(mShare, OnSuccessListener {

                        Answers.getInstance().logCustom(CustomEvent("Unione a ondivisione annullata"))
                        finish()

                    }, OnFailureListener { e ->

                        Toast.makeText(this@OngoingShareActivity, R.string.activity_signin_server_error, Toast.LENGTH_SHORT).show()
                        e.printStackTrace()

                    })

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

            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mFusedLocationClient.lastLocation.addOnSuccessListener { endLocation ->

                when {
                    endLocation == null -> {
                        Toast.makeText(this, R.string.activity_ongoing_share_check_error, Toast.LENGTH_SHORT).show()
                    }
                    hostLocation.distanceTo(endLocation) > 500 -> {
                        Toast.makeText(this, R.string.activity_ongoing_share_check_wrong_code, Toast.LENGTH_SHORT).show()
                    }
                    else -> HomeToWorkClient.completeShare(mShare, endLocation, OnSuccessListener {

                        val userId = HomeToWorkClient.user?.id
                        if (userId == mShare.host?.id) {
                            val sharedDistance = it.guests.sumBy { it.distance } / 1000f
                            Answers.getInstance().logCustom(CustomEvent("Condivisione conclusa")
                                    .putCustomAttribute("Distanza totale condivisa", sharedDistance))
                        } else {
                            val sharedDistance = it.guests.last { it.user?.id == userId }.distance / 1000f
                            Answers.getInstance().logCustom(CustomEvent("Condivisione completata")
                                    .putCustomAttribute("Distanza condivisa", sharedDistance))
                        }

                        finish()
                        Toast.makeText(this, R.string.activity_ongoing_share_check_success, Toast.LENGTH_SHORT).show()

                    }, OnFailureListener { e ->

                        Toast.makeText(this@OngoingShareActivity, R.string.activity_ongoing_share_check_error, Toast.LENGTH_SHORT).show()
                        e.printStackTrace()

                    })
                }

            }

        }
    }

    private fun refreshGuests() {

        HomeToWorkClient.getShare(mShare.id, OnSuccessListener { share ->
            mShare = share
            initUI()
        }, OnFailureListener { it.printStackTrace() })

    }


}
