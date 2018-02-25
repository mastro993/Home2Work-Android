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
import android.widget.Toast
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.MainActivity
import it.gruppoinfor.home2work.activities.OngoingShareActivity
import it.gruppoinfor.home2work.adapters.SharesAdapter
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2work.utils.Const.EXTRA_SHARE
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Guest
import it.gruppoinfor.home2workapi.model.LatLng
import it.gruppoinfor.home2workapi.model.Share
import kotlinx.android.synthetic.main.fragment_shares.*
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

        loading_view.visibility = View.VISIBLE

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = true
            refreshShares()
        }

        shares_recycler_view.isNestedScrollingEnabled = false

        fab_new_share.setOnClickListener {

            MaterialDialog.Builder(context!!)
                    .title(R.string.fragment_share_dialog_new_title)
                    .items(R.array.fragment_share_dialog_new_share_options)
                    .itemsColor(ContextCompat.getColor(context!!, R.color.light_bg_dark_secondary_text))
                    .itemsCallback { _, _, position, _ ->

                        when (position) {
                            0 -> createShare()
                            1 -> joinShare()
                        }

                    }
                    .show()

        }

        refreshShares()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == Const.REQ_CAMERA)
            joinShare()

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult is IntentResult) {

            val stringData = scanResult.contents.split(",")
            val shareId = java.lang.Long.parseLong(stringData[0])
            val latLng = LatLng(java.lang.Double.parseDouble(stringData[1]), java.lang.Double.parseDouble(stringData[2]))
            checkShareCode(shareId, latLng)

        } else if (requestCode == 0) {

            refreshUI()

        }

    }

    private fun refreshShares() {

        HomeToWorkClient.getInstance().getUserShares(OnSuccessListener { shares ->

            mOngoingShare = shares.findLast { share -> share.status == Share.Status.CREATED }


            if (mOngoingShare?.host == HomeToWorkClient.user ||
                    mOngoingShare?.guests?.first { guest -> guest.user == HomeToWorkClient.user && guest.status == Guest.Status.JOINED } != null) {

                shares.remove(mOngoingShare!!)

            }

            mShareList.clear()
            mShareList.addAll(shares)

            refreshUI()

        }, OnFailureListener {

            Toast.makeText(context!!, "Impossibile ottenere lista condivsioni al momento", Toast.LENGTH_SHORT).show()

            swipe_refresh_layout.isRefreshing = false
            loading_view.visibility = View.GONE

            it.printStackTrace()

        })

    }

    private fun refreshUI() {

        if (mShareList.size == 0) {

            empty_view.visibility = View.VISIBLE

        } else {

            empty_view.visibility = View.GONE

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

        if (mOngoingShare is Share) {

            ongoing_share_view.setShare(mOngoingShare)
            ongoing_share_view.setOnClickListener {

                val intent = Intent(context, OngoingShareActivity::class.java)
                intent.putExtra(Const.EXTRA_SHARE, mOngoingShare)
                startActivity(intent)

            }

            ongoing_share_view.visibility = View.VISIBLE
            fab_new_share.visibility = View.GONE

            (context as MainActivity).setNavigationBadge(Const.SHARES_TAB, "In corso")

        } else {

            ongoing_share_view.visibility = View.GONE
            fab_new_share.visibility = View.VISIBLE

            (context as MainActivity).setNavigationBadge(Const.SHARES_TAB, "")

        }

        swipe_refresh_layout.isRefreshing = false
        loading_view.visibility = View.GONE

    }

    private fun createShare() {

        val materialDialog = MaterialDialog.Builder(context!!)
                .title(R.string.fragment_share_dialog_new_title)
                .content(R.string.fragment_share_dialog_new_content)
                .contentGravity(GravityEnum.CENTER)
                .progress(true, 150, false)
                .show()

        HomeToWorkClient.getInstance().createShare(OnSuccessListener { share ->

            Answers.getInstance().logCustom(CustomEvent("Nuova condivisione"))

            materialDialog.dismiss()

            fab_new_share!!.visibility = View.GONE
            mOngoingShare = share
            refreshShares()
            val intent = Intent(activity, OngoingShareActivity::class.java)
            intent.putExtra(EXTRA_SHARE, share)
            context!!.startActivity(intent)

        }, OnFailureListener {

            materialDialog.dismiss()

            Toast.makeText(context!!, R.string.fragment_share_dialog_new_error, Toast.LENGTH_SHORT).show()

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
                    joinLocation == null || hostLocation.distanceTo(joinLocation) > 500 -> {
                        Toast.makeText(context!!, R.string.activity_ongoing_share_invalid_code, Toast.LENGTH_SHORT).show()
                        materialDialog.dismiss()
                    }
                    else -> HomeToWorkClient.getInstance().joinShare(shareID, joinLocation, OnSuccessListener { share ->

                        Answers.getInstance().logCustom(CustomEvent("Unione a condivisione"))

                        materialDialog.dismiss()
                        val intent = Intent(activity, OngoingShareActivity::class.java)
                        intent.putExtra(EXTRA_SHARE, share)
                        context!!.startActivity(intent)
                    }, OnFailureListener { e ->
                        Toast.makeText(context!!, R.string.activity_signin_server_error, Toast.LENGTH_SHORT).show()
                        materialDialog.dismiss()
                        e.printStackTrace()
                    })
                }

            }

        }

    }

}
