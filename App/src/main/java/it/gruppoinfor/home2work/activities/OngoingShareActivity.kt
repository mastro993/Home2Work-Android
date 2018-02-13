package it.gruppoinfor.home2work.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.annimon.stream.Optional
import com.annimon.stream.Stream
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.adapters.ShareGuestsAdapter
import it.gruppoinfor.home2work.custom.AvatarView
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2work.services.MessagingService
import it.gruppoinfor.home2work.utils.QREncoder
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Guest
import it.gruppoinfor.home2workapi.model.LatLng
import it.gruppoinfor.home2workapi.model.Share

class OngoingShareActivity : AppCompatActivity(), ItemClickCallbacks {
    @BindView(R.id.layout_show_code)
    internal var layoutShowCode: LinearLayout? = null
    @BindView(R.id.guests_recycler_view)
    internal var guestsRecyclerView: RecyclerView? = null
    @BindView(R.id.empty_view)
    internal var emptyView: TextView? = null
    @BindView(R.id.button_complete_share)
    internal var buttonCompleteShare: Button? = null
    @BindView(R.id.host_layout)
    internal var hostLayout: View? = null
    @BindView(R.id.guest_layout)
    internal var guestLayout: View? = null
    @BindView(R.id.ongoing_share_layout)
    internal var ongoingShareLayout: RelativeLayout? = null
    @BindView(R.id.avatar_view)
    internal var avatarView: AvatarView? = null
    @BindView(R.id.name_text_view)
    internal var nameTextView: TextView? = null
    @BindView(R.id.job_text_view)
    internal var jobTextView: TextView? = null
    @BindView(R.id.text_share_distance)
    internal var textShareDistance: TextView? = null
    @BindView(R.id.text_share_xp)
    internal var textShareXp: TextView? = null
    @BindView(R.id.buttons_layout)
    internal var buttonsLayout: LinearLayout? = null
    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null
    @BindView(R.id.layout_host)
    internal var layoutHost: LinearLayout? = null
    @BindView(R.id.header_view)
    internal var headerView: TextView? = null

    private var mShare: Share? = null
    // private Guest mUserShareGuest;
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
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        val intent = intent
        mShare = intent.getSerializableExtra(EXTRA_SHARE) as Share

        if (mShare!!.type == Share.Type.DRIVER)
            initHostUI()
        else {
            //Stream<Guest> shareGuestStream = Stream.of(mShare.getGuests());
            //mUserShareGuest = shareGuestStream.filter(value -> value.getGuest().equals(HomeToWorkClient.getUser())).findFirst().get();
            initGuestUI()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_ongoing_share, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_cancel_share -> {
                when (mShare!!.type) {
                    Share.Type.DRIVER -> MaterialDialog.Builder(this)
                            .title("Annulla condivisione")
                            .content("Sei sicuro di voler annullare la condivisione corrente? Tutti i progressi verranno persi")
                            .onPositive { dialog, which ->
                                val materialDialog = MaterialDialog.Builder(this@OngoingShareActivity)
                                        .content(R.string.activity_ongoing_share_cancel_dialog_content)
                                        .contentGravity(GravityEnum.CENTER)
                                        .progress(true, 150, true)
                                        .show()

                                HomeToWorkClient.getInstance().cancelShare(mShare, { responseBody ->
                                    materialDialog.dismiss()
                                    finish()
                                }) { e ->
                                    Toasty.error(this@OngoingShareActivity, getString(R.string.activity_signin_server_error)).show()
                                    materialDialog.dismiss()
                                    e.printStackTrace()
                                }
                            }
                            .positiveText("Conferma")
                            .negativeText("Indietro")
                            .show()
                    Share.Type.GUEST -> MaterialDialog.Builder(this)
                            .title("Annulla condivisione")
                            .content("Sei sicuro di voler annullare la condivisione corrente? Tutti i progressi verranno persi")
                            .onPositive { dialog, which ->
                                val materialDialog = MaterialDialog.Builder(this@OngoingShareActivity)
                                        .content(R.string.activity_ongoing_share_leave_dialog_content)
                                        .contentGravity(GravityEnum.CENTER)
                                        .progress(true, 150, true)
                                        .show()


                                HomeToWorkClient.getInstance().leaveShare(mShare, { responseBody ->
                                    materialDialog.dismiss()
                                    finish()
                                }) { e ->
                                    Toasty.error(this@OngoingShareActivity, getString(R.string.activity_signin_server_error)).show()
                                    materialDialog.dismiss()
                                    e.printStackTrace()
                                }
                            }
                            .positiveText("Conferma")
                            .negativeText("Indietro")
                            .show()
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
                IntentFilter(MessagingService.SHARE_DETACH_REQUEST)
        )
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onStop()
    }

    override fun onItemClick(view: View, position: Int) {
        val options: Array<String>

        if (mShare!!.guests[position].status == Guest.Status.CANCELED) {
            options = resources.getStringArray(R.array.activity_ongoing_share_user_options_leaved)
        } else {
            options = resources.getStringArray(R.array.activity_ongoing_share_user_options)
        }

        MaterialDialog.Builder(this)
                .items(*options)
                .itemsCallback { dialog, itemView, p, text ->
                    when (p) {
                        0 -> {
                            val userIntent = Intent(this, ShowUserActivity::class.java)
                            userIntent.putExtra("user", mShare!!.guests[position])
                            startActivity(userIntent)
                        }
                        1 -> HomeToWorkClient.getInstance().expelGuest(mShare, mShare!!.guests[position], { share ->
                            mShare = share
                            initHostUI()
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
            val stringData = scanResult.contents.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val shareId = java.lang.Long.parseLong(stringData[0])
            val latLng = LatLng(java.lang.Double.parseDouble(stringData[1]), java.lang.Double.parseDouble(stringData[2]))
            checkShareCode(shareId, latLng)
        } else
            Toasty.error(this, getString(R.string.activity_ongoing_share_invalid_code))
    }

    @OnClick(R.id.layout_show_code)
    fun onShareCodeButtonClicked() {
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
                    Toasty.error(this@OngoingShareActivity, getString(R.string.activity_ongoing_share_code_unavailable)).show()
                    return@mFusedLocationClient.getLastLocation().addOnSuccessListener
                }

                val latlngString = location!!.latitude.toString() + "," + location.longitude

                HomeToWorkClient.getInstance().createShare({ share ->
                    try {
                        val bitmap = QREncoder.EncodeText(share.id.toString() + "," + latlngString)
                        qrCodeImage.setImageBitmap(bitmap)
                        loadingView.visibility = View.INVISIBLE
                    } catch (e: Exception) {
                        qrCodeDialog!!.hide()
                        Toasty.error(this@OngoingShareActivity, getString(R.string.activity_ongoing_share_code_unavailable)).show()
                    }
                }) { e ->
                    qrCodeDialog!!.hide()
                    Toasty.error(this@OngoingShareActivity, getString(R.string.activity_ongoing_share_code_unavailable)).show()
                }
            }

        }

    }

    @OnClick(R.id.button_complete_share)
    fun onCompleteButtonClick() {

        when (mShare!!.type) {
            Share.Type.DRIVER -> {
                if (mShare!!.guests.size == 0) {
                    MaterialDialog.Builder(this)
                            .title(R.string.activity_ongoing_share_dialog_completition_error_title)
                            .content(R.string.activity_ongoing_share_dialog_completition_error_content)
                            .show()
                    break
                }

                loadingDialog = MaterialDialog.Builder(this)
                        .content(R.string.activity_ongoing_share_dialog_completition)
                        .contentGravity(GravityEnum.CENTER)
                        .progress(true, 150, true)
                        .show()

                HomeToWorkClient.getInstance().finishShare(mShare, { share ->
                    loadingDialog!!.dismiss()
                    Toasty.success(this@OngoingShareActivity, getString(R.string.activity_ongoing_share_dialog_completition_success)).show()
                    finish()
                }) { e ->
                    loadingDialog!!.dismiss()
                    Toasty.error(this@OngoingShareActivity, getString(R.string.activity_ongoing_share_dialog_completition_error)).show()
                    e.printStackTrace()
                }
            }
            Share.Type.GUEST ->

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


    }

    @OnClick(R.id.layout_host)
    fun onHostLayoutClick() {
        val userIntent = Intent(this, ShowUserActivity::class.java)
        userIntent.putExtra("user", mShare!!.host)
        startActivity(userIntent)
    }

    private fun enableCompleteButton(): Boolean {
        if (mShare!!.guests.size == 0) return false

        val shareGuestStream = Stream.of(mShare!!.guests)

        val shareGuestOptional = shareGuestStream.filter { value -> value.status == Guest.Status.JOINED }.findFirst()

        return !shareGuestOptional.isPresent

    }

    private fun checkShareCode(shareID: Long?, hostLocation: LatLng) {


        loadingDialog = MaterialDialog.Builder(this)
                .content(R.string.activity_ongoing_share_check)
                .contentGravity(GravityEnum.CENTER)
                .progress(true, 150, true)
                .show()

        if (mShare!!.id != shareID) {
            Toasty.error(this, getString(R.string.activity_ongoing_share_check_wrong_code)).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mFusedLocationClient.lastLocation.addOnSuccessListener { endLocation ->

                if (endLocation == null) {
                    Toasty.error(this, getString(R.string.activity_ongoing_share_check_error)).show()
                    loadingDialog!!.dismiss()
                    return@mFusedLocationClient.getLastLocation().addOnSuccessListener
                }

                if (hostLocation.distanceTo(endLocation) > 500) {
                    Toasty.error(this, getString(R.string.activity_ongoing_share_check_wrong_code)).show()
                    loadingDialog!!.dismiss()
                    return@mFusedLocationClient.getLastLocation().addOnSuccessListener
                }

                HomeToWorkClient.getInstance().completeShare(mShare, endLocation, { share ->
                    loadingDialog!!.dismiss()
                    finish()
                    Toasty.success(this, getString(R.string.activity_ongoing_share_check_success)).show()
                }) { e ->
                    loadingDialog!!.dismiss()
                    Toasty.error(this@OngoingShareActivity, getString(R.string.activity_ongoing_share_check_error)).show()
                    e.printStackTrace()
                }

            }

        }
    }

    private fun initHostUI() {

        hostLayout!!.visibility = View.VISIBLE
        guestLayout!!.visibility = View.GONE

        if (mShare!!.guests != null && mShare!!.guests.size == 0) {
            emptyView!!.visibility = View.VISIBLE
            headerView!!.visibility = View.GONE
            guestsRecyclerView!!.visibility = View.GONE
        } else {
            emptyView!!.visibility = View.GONE
            headerView!!.visibility = View.VISIBLE
            guestsRecyclerView!!.visibility = View.VISIBLE

            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)

            guestsRecyclerView!!.layoutManager = layoutManager
            guestsRecyclerView!!.layoutAnimation = animation

            val mShareGuestsAdapter = ShareGuestsAdapter(this, mShare!!.guests)
            mShareGuestsAdapter.setItemClickCallbacks(this)
            guestsRecyclerView!!.adapter = mShareGuestsAdapter
        }

        buttonCompleteShare!!.isEnabled = enableCompleteButton()

    }

    private fun initGuestUI() {

        hostLayout!!.visibility = View.GONE
        guestLayout!!.visibility = View.VISIBLE

        nameTextView!!.text = mShare!!.host.toString()
        jobTextView!!.text = mShare!!.host.company.toString()

        avatarView!!.setAvatarURL(mShare!!.host.avatarURL)
        // TODO mettere immagine normale senza avatar view
        //avatarView.setLevel(mShare.getHost().getExp());

        buttonCompleteShare!!.isEnabled = true


    }

    private fun refreshGuests() {

        HomeToWorkClient.getInstance().getShare(mShare!!.id, { share ->
            mShare = share
            initHostUI()
        }, OnFailureListener { it.printStackTrace() })

    }

    companion object {

        val EXTRA_SHARE = "share"
        private val CAMERA_PERMISSION_REQUEST_CODE = 1
    }
}
