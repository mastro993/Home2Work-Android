package it.gruppoinfor.home2work.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.zxing.integration.android.IntentIntegrator
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.adapters.ShareGuestsAdapter
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2work.utils.QREncoder
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Guest
import it.gruppoinfor.home2workapi.model.LatLng
import it.gruppoinfor.home2workapi.model.Share
import kotlinx.android.synthetic.main.activity_ongoing_share.*

class OngoingShareActivity : AppCompatActivity(), ItemClickCallbacks {

    private lateinit var mShare: Share
    private var qrCodeDialog: MaterialDialog? = null
    private var loadingDialog: MaterialDialog? = null

    private val mMessageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            if (qrCodeDialog != null) qrCodeDialog!!.dismiss()
            refreshGuests()

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ongoing_share)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mShare = intent.getSerializableExtra(Const.EXTRA_SHARE) as Share
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
                IntentFilter(Const.SHARE_JOIN_REQUEST)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                IntentFilter(Const.SHARE_COMPLETE_REQUEST)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                IntentFilter(Const.SHARE_DETACH_REQUEST)
        )

    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onStop()
    }

    override fun onItemClick(view: View, position: Int) {

        val options: Array<String> = if (mShare.guests[position].status == Guest.Status.CANCELED) {
            resources.getStringArray(R.array.activity_ongoing_share_user_options_leaved)
        } else {
            resources.getStringArray(R.array.activity_ongoing_share_user_options)
        }

        MaterialDialog.Builder(this)
                .items(*options)
                .itemsCallback { _, _, p, _ ->
                    when (p) {
                        0 -> {
                            val userIntent = Intent(this, ShowUserActivity::class.java)
                            userIntent.putExtra("user", mShare.guests[position])
                            startActivity(userIntent)
                        }
                        1 -> HomeToWorkClient.getInstance().expelGuest(mShare, mShare.guests[position], OnSuccessListener { share ->
                            mShare = share
                            initUI()
                        }, OnFailureListener { it.printStackTrace() })
                        2 -> {
                        }
                    }
                }
                .show()

    }

    override fun onLongItemClick(view: View, position: Int): Boolean {

        return false
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null) {
            val stringData = scanResult.contents.split(",")
            val shareId = java.lang.Long.parseLong(stringData[0])
            val latLng = LatLng(java.lang.Double.parseDouble(stringData[1]), java.lang.Double.parseDouble(stringData[2]))
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
            qrCodeDialog = MaterialDialog.Builder(this)
                    .title(getString(R.string.activity_ongoing_dialog_share_code_title))
                    .customView(R.layout.dialog_share_qr_code, false)
                    .build()

            val qrCodeImage = qrCodeDialog!!.findViewById(R.id.qr_code_image_view) as ImageView
            val loadingView = qrCodeDialog!!.findViewById(R.id.loading_view) as FrameLayout

            loadingView.visibility = View.VISIBLE
            qrCodeDialog!!.show()

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                mFusedLocationClient.lastLocation.addOnSuccessListener { location ->

                    if (location == null) {
                        qrCodeDialog!!.hide()
                        Toast.makeText(this@OngoingShareActivity, R.string.activity_ongoing_share_code_unavailable, Toast.LENGTH_SHORT).show()
                    } else {
                        val latlngString = "${location.latitude},${location.longitude}"

                        HomeToWorkClient.getInstance().createShare(OnSuccessListener { share ->
                            try {
                                val bitmap = QREncoder.encodeText("${share.id},$latlngString")
                                qrCodeImage.setImageBitmap(bitmap)
                                loadingView.visibility = View.INVISIBLE
                            } catch (e: Exception) {
                                qrCodeDialog!!.hide()
                                Toast.makeText(this@OngoingShareActivity, R.string.activity_ongoing_share_code_unavailable, Toast.LENGTH_SHORT).show()
                            }
                        }, OnFailureListener {
                            qrCodeDialog!!.hide()
                            Toast.makeText(this@OngoingShareActivity, R.string.activity_ongoing_share_code_unavailable, Toast.LENGTH_SHORT).show()
                        })
                    }

                }

            }
        }

        button_complete_share.setOnClickListener {
            if (mShare.guests.size == 0) {
                MaterialDialog.Builder(this)
                        .title(R.string.activity_ongoing_share_dialog_completition_error_title)
                        .content(R.string.activity_ongoing_share_dialog_completition_error_content)
                        .show()

            } else {
                loadingDialog = MaterialDialog.Builder(this)
                        .content(R.string.activity_ongoing_share_dialog_completition)
                        .contentGravity(GravityEnum.CENTER)
                        .progress(true, 150, true)
                        .show()

                HomeToWorkClient.getInstance().finishShare(mShare, OnSuccessListener {
                    loadingDialog!!.dismiss()
                    Toast.makeText(this@OngoingShareActivity, R.string.activity_ongoing_share_dialog_completition_success, Toast.LENGTH_SHORT).show()
                    finish()
                }, OnFailureListener { e ->
                    loadingDialog!!.dismiss()
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
            mShareGuestsAdapter.setItemClickCallbacks(this)
            guests_recycler_view.adapter = mShareGuestsAdapter
        }

        button_complete_share.isEnabled = enableCompleteButton()

    }

    private fun initGuestUI() {

        host_layout.visibility = View.GONE
        guest_layout.visibility = View.VISIBLE

        layout_host.setOnClickListener {
            val userIntent = Intent(this, ShowUserActivity::class.java)
            userIntent.putExtra("user", mShare.host)
            startActivity(userIntent)
        }

        button_complete_share.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), Const.CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                val intentIntegrator = IntentIntegrator(this)
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                intentIntegrator.setPrompt(getString(R.string.activity_ongoing_share_dialog_qr_prompt))
                intentIntegrator.setOrientationLocked(false)

            }
        }

        name_text_view.text = mShare.host.toString()
        job_text_view.text = mShare.host.company.toString()

        avatar_view.setAvatarURL(mShare.host.avatarURL)
        // TODO mettere immagine normale senza avatar view
        //avatarView.setLevel(mShare.getHost().getExp());

        button_complete_share.isEnabled = true

    }

    private fun showHostCancelDialog() {

        MaterialDialog.Builder(this)
                .title("Annulla condivisione")
                .content("Sei sicuro di voler annullare la condivisione corrente? Tutti i progressi verranno persi")
                .onPositive { _, _ ->
                    val materialDialog = MaterialDialog.Builder(this@OngoingShareActivity)
                            .content(R.string.activity_ongoing_share_cancel_dialog_content)
                            .contentGravity(GravityEnum.CENTER)
                            .progress(true, 150, true)
                            .show()

                    HomeToWorkClient.getInstance().cancelShare(mShare, OnSuccessListener {
                        materialDialog.dismiss()
                        finish()
                    }, OnFailureListener { e ->
                        Toast.makeText(this@OngoingShareActivity, R.string.activity_signin_server_error, Toast.LENGTH_SHORT).show()
                        materialDialog.dismiss()
                        e.printStackTrace()
                    })
                }
                .positiveText("Conferma")
                .negativeText("Indietro")
                .show()

    }

    private fun showGuestCancelDialog() {

        MaterialDialog.Builder(this)
                .title("Annulla condivisione")
                .content("Sei sicuro di voler annullare la condivisione corrente? Tutti i progressi verranno persi")
                .onPositive { _, _ ->
                    val materialDialog = MaterialDialog.Builder(this@OngoingShareActivity)
                            .content(R.string.activity_ongoing_share_leave_dialog_content)
                            .contentGravity(GravityEnum.CENTER)
                            .progress(true, 150, true)
                            .show()


                    HomeToWorkClient.getInstance().leaveShare(mShare, OnSuccessListener {
                        materialDialog.dismiss()
                        finish()
                    }, OnFailureListener { e ->
                        Toast.makeText(this@OngoingShareActivity, R.string.activity_signin_server_error, Toast.LENGTH_SHORT).show()
                        materialDialog.dismiss()
                        e.printStackTrace()
                    })
                }
                .positiveText("Conferma")
                .negativeText("Indietro")
                .show()


    }

    private fun enableCompleteButton(): Boolean {

        if (mShare.guests.size == 0) return false
        val uncompletedGuests = mShare.guests.find { guest -> guest.status == Guest.Status.JOINED }
        return uncompletedGuests == null

    }

    private fun checkShareCode(shareID: Long?, hostLocation: LatLng) {

        loadingDialog = MaterialDialog.Builder(this)
                .content(R.string.activity_ongoing_share_check)
                .contentGravity(GravityEnum.CENTER)
                .progress(true, 150, true)
                .show()

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
                        loadingDialog!!.dismiss()

                    }
                    hostLocation.distanceTo(endLocation) > 500 -> {
                        Toast.makeText(this, R.string.activity_ongoing_share_check_wrong_code, Toast.LENGTH_SHORT).show()
                        loadingDialog!!.dismiss()

                    }
                    else -> HomeToWorkClient.getInstance().completeShare(mShare, endLocation, OnSuccessListener {
                        loadingDialog!!.dismiss()
                        finish()
                        Toast.makeText(this, R.string.activity_ongoing_share_check_success, Toast.LENGTH_SHORT).show()
                    }, OnFailureListener { e ->
                        loadingDialog!!.dismiss()
                        Toast.makeText(this@OngoingShareActivity, R.string.activity_ongoing_share_check_error, Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    })
                }

            }

        }
    }

    private fun refreshGuests() {

        HomeToWorkClient.getInstance().getShare(mShare.id, OnSuccessListener { share ->
            mShare = share
            initUI()
        }, OnFailureListener { it.printStackTrace() })

    }


}
