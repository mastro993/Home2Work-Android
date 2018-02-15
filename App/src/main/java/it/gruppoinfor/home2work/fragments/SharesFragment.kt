package it.gruppoinfor.home2work.fragments


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.annimon.stream.Stream
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.zxing.integration.android.IntentIntegrator
import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.MainActivity
import it.gruppoinfor.home2work.activities.OngoingShareActivity
import it.gruppoinfor.home2work.adapters.SharesAdapter
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2work.user.Const
import it.gruppoinfor.home2work.user.Const.EXTRA_SHARE
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Guest
import it.gruppoinfor.home2workapi.model.LatLng
import it.gruppoinfor.home2workapi.model.Share
import kotlinx.android.synthetic.main.fragment_shares.*
import kotlinx.android.synthetic.main.layout_share_empty.*
import java.util.*

class SharesFragment : Fragment() {

    private var mOngoingShare: Share? = null
    private val mShareList = ArrayList<Share>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_shares, container, false)
        setHasOptionsMenu(true)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = true
            refreshData()
        }

        shares_recycler_view.isNestedScrollingEnabled = false

    }

    override fun onResume() {
        super.onResume()

        refreshData()

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {

        if (isVisibleToUser && mShareList.size == 0) refreshData()

        super.setUserVisibleHint(isVisibleToUser)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == Const.REQ_CAMERA)
            joinShare()

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null) {
            val stringData = scanResult.contents.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val shareId = java.lang.Long.parseLong(stringData[0])
            val latLng = LatLng(java.lang.Double.parseDouble(stringData[1]), java.lang.Double.parseDouble(stringData[2]))
            checkShareCode(shareId, latLng)
        } else
            Toasty.error(context!!, context!!.getString(R.string.activity_ongoing_share_invalid_code))

    }

    private fun initUI() {

        swipe_refresh_layout.isRefreshing = false

        // Utilizzo dei falg per la visivilità per evitare dei glitch durante il controllo
        val listVisibility: Int
        var fabVisibility: Int
        var newShareVisibility: Int
        val ongoingShareVisibility: Int

        if (mShareList.size == 0) {

            listVisibility = View.GONE
            fabVisibility = View.GONE
            newShareVisibility = View.VISIBLE

        } else {

            listVisibility = View.VISIBLE
            fabVisibility = View.VISIBLE
            newShareVisibility = View.GONE

            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)

            shares_recycler_view.layoutManager = layoutManager
            shares_recycler_view.layoutAnimation = animation

            val mSharesAdapter = SharesAdapter(context as MainActivity, mShareList)
            mSharesAdapter.setItemClickCallbacks(object : ItemClickCallbacks {
                override fun onItemClick(view: View, position: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onLongItemClick(view: View, position: Int): Boolean {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
            shares_recycler_view.adapter = mSharesAdapter
        }

        if (mOngoingShare != null) {
            fabVisibility = View.GONE
            newShareVisibility = View.GONE
            ongoingShareVisibility = View.VISIBLE

            ongoing_share_view.setShare(mOngoingShare!!)
            (context as MainActivity).setBadge(2, "In corso")

        } else {
            ongoingShareVisibility = View.GONE
            (context as MainActivity).setBadge(2, "")
        }

        // Applico la visibilità solo alla fine dei controlli
        shares_recycler_view.visibility = listVisibility
        fab_new_share.visibility = fabVisibility
        new_share_container_empty.visibility = newShareVisibility
        ongoing_share_view.visibility = ongoingShareVisibility

        fab_new_share.setOnClickListener {
            newShareDialog()
        }
        new_share_container_empty.setOnClickListener {
            newShareDialog()
        }

    }

    private fun newShareDialog() {

        MaterialDialog.Builder(context!!)
                .title(R.string.fragment_share_dialog_new_title)
                .items(R.array.fragment_share_dialog_new_share_options)
                .itemsCallback { _, _, position, _ ->
                    when (position) {
                        0 -> createShare()
                        1 -> joinShare()
                    }
                }
                .show()

    }

    private fun refreshData() {

        HomeToWorkClient.getInstance().getUserShares(OnSuccessListener { shares ->

            mOngoingShare = null

            val ongoingShareOptional = Stream.of(shares)
                    .filter { value -> value.status == Share.Status.CREATED }
                    .findFirst()

            if (ongoingShareOptional.isPresent) {

                val ongoingShare = ongoingShareOptional.get()

                // Controllo se l'utente è host o guest della condivisione
                if (ongoingShare.host == HomeToWorkClient.user) {
                    mOngoingShare = ongoingShare
                    shares.remove(ongoingShare)
                } else {
                    // Se è guest controllo se ha completato la condivisione o è ancora in corso
                    val shareGuestOptional = Stream.of(ongoingShare.guests)
                            .filter { value -> value.user == HomeToWorkClient.user && value.status == Guest.Status.JOINED }
                            .findFirst()
                    if (shareGuestOptional.isPresent) {
                        mOngoingShare = ongoingShare
                        shares.remove(ongoingShare)
                    }
                }

            }

            mShareList.clear()
            mShareList.addAll(shares)
            initUI()

        }, OnFailureListener {
            //Toasty.error(getContext(), "Impossibile ottenere lista condivsioni al momento").show();
            initUI()
        })

    }

    private fun joinShare() {

        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((context as MainActivity?)!!, arrayOf(Manifest.permission.CAMERA), Const.REQ_CAMERA)
        } else {
            val intentIntegrator = IntentIntegrator(activity)
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            intentIntegrator.setOrientationLocked(false)
            intentIntegrator.initiateScan()
        }

    }

    private fun checkShareCode(shareID: Long?, hostLocation: LatLng) {

        val materialDialog = MaterialDialog.Builder(context!!)
                .title(R.string.fragment_share_dialog_new_title)
                .content(R.string.fragment_share_dialog_join_content)
                .contentGravity(GravityEnum.CENTER)
                .progress(true, 150, true)
                .show()


        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
            mFusedLocationClient.lastLocation.addOnSuccessListener { joinLocation ->

                when {
                    joinLocation == null -> {
                        Toasty.error(context!!, context!!.getString(R.string.activity_ongoing_share_invalid_code)).show()
                        materialDialog.dismiss()
                    }
                    hostLocation.distanceTo(joinLocation) > 500 -> {
                        Toasty.error(context!!, context!!.getString(R.string.activity_ongoing_share_invalid_code)).show()
                        materialDialog.dismiss()
                    }
                    else -> HomeToWorkClient.getInstance().joinShare(shareID, joinLocation, OnSuccessListener { share ->
                        materialDialog.dismiss()
                        val intent = Intent(activity, OngoingShareActivity::class.java)
                        intent.putExtra(EXTRA_SHARE, share)
                        context!!.startActivity(intent)
                    }, OnFailureListener { e ->
                        Toasty.error(context!!, context!!.getString(R.string.activity_signin_server_error)).show()
                        materialDialog.dismiss()
                        e.printStackTrace()
                    })
                }

            }

        }

    }

    private fun createShare() {

        val materialDialog = MaterialDialog.Builder(context!!)
                .title(R.string.fragment_share_dialog_new_title)
                .content(R.string.fragment_share_dialog_new_content)
                .contentGravity(GravityEnum.CENTER)
                .progress(true, 150, true)
                .show()

        HomeToWorkClient.getInstance().createShare(OnSuccessListener { share ->
            materialDialog.dismiss()
            fab_new_share!!.visibility = View.GONE
            mOngoingShare = share
            initUI()
            val intent = Intent(activity, OngoingShareActivity::class.java)
            intent.putExtra(EXTRA_SHARE, share)
            context!!.startActivity(intent)
        }, OnFailureListener {
            materialDialog.dismiss()
            Toasty.error(context!!, context!!.getString(R.string.fragment_share_dialog_new_error)).show()
        })

    }

}
