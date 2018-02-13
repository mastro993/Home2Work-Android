package it.gruppoinfor.home2work.fragments


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController

import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.annimon.stream.Optional
import com.annimon.stream.Stream
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.MainActivity
import it.gruppoinfor.home2work.activities.OngoingShareActivity
import it.gruppoinfor.home2work.adapters.SharesAdapter
import it.gruppoinfor.home2work.custom.OngoinShareView
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Guest
import it.gruppoinfor.home2workapi.model.LatLng
import it.gruppoinfor.home2workapi.model.Share

import it.gruppoinfor.home2work.activities.OngoingShareActivity.EXTRA_SHARE

/**
 * A simple [Fragment] subclass.
 */
class SharesFragment : Fragment(), ItemClickCallbacks {

    @BindView(R.id.shares_recycler_view)
    internal var sharesRecyclerView: RecyclerView? = null
    @BindView(R.id.swipe_refresh_layout)
    internal var swipeRefreshLayout: SwipeRefreshLayout? = null
    @BindView(R.id.ongoing_share_view)
    internal var ongoingShareView: OngoinShareView? = null
    @BindView(R.id.new_share_container_empty)
    internal var newShareContainerEmpty: View? = null
    @BindView(R.id.nested_scroll_view)
    internal var nestedScrollView: NestedScrollView? = null
    @BindView(R.id.fab_new_share)
    internal var fabNewShare: FloatingActionButton? = null

    private var unbinder: Unbinder? = null
    private var mContext: Context? = null
    private var mOngoingShare: Share? = null

    private val mShareList = ArrayList<Share>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_shares, container, false)
        unbinder = ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)

        swipeRefreshLayout!!.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout!!.setOnRefreshListener {
            swipeRefreshLayout!!.isRefreshing = true
            refreshData()
        }
        sharesRecyclerView!!.isNestedScrollingEnabled = false
        return rootView
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
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            joinShare()
        }
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
            Toasty.error(mContext!!, mContext!!.getString(R.string.activity_ongoing_share_invalid_code))
    }

    override fun onDestroyView() {
        unbinder!!.unbind()
        super.onDestroyView()
    }

    override fun onItemClick(view: View, position: Int) {
        // TODO share click
        /*Share share = mShareList.get(position);
        Intent intent = new Intent(getActivity(), OngoingShareActivity.class);
        intent.putExtra("SHARE_ID", share.getId());
        getActivity().startActivity(intent);*/
    }

    override fun onLongItemClick(view: View, position: Int): Boolean {
        // TODO share long click

        return true
    }

    @OnClick(R.id.fab_new_share, R.id.button_first_share)
    fun onNewShareClicked() {

        MaterialDialog.Builder(mContext!!)
                .title(R.string.fragment_share_dialog_new_title)
                .items(R.array.fragment_share_dialog_new_share_options)
                .itemsCallback { dialog, itemView, position, text ->
                    when (position) {
                        0 -> createShare()
                        1 -> joinShare()
                    }
                }
                .show()
    }

    private fun initUI() {
        swipeRefreshLayout!!.isRefreshing = false

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

            sharesRecyclerView!!.layoutManager = layoutManager
            sharesRecyclerView!!.layoutAnimation = animation

            val mSharesAdapter = SharesAdapter(activity, mShareList)
            mSharesAdapter.setItemClickCallbacks(this)
            sharesRecyclerView!!.adapter = mSharesAdapter
        }

        if (mOngoingShare != null) {
            fabVisibility = View.GONE
            newShareVisibility = View.GONE
            ongoingShareVisibility = View.VISIBLE

            ongoingShareView!!.setShare(mOngoingShare)
            (mContext as MainActivity).setBadge(2, "In corso")

        } else {
            ongoingShareVisibility = View.GONE
            (mContext as MainActivity).setBadge(2, "")
        }

        // Applico la visibilità solo alla fine dei controlli
        sharesRecyclerView!!.visibility = listVisibility
        fabNewShare!!.visibility = fabVisibility
        newShareContainerEmpty!!.visibility = newShareVisibility
        ongoingShareView!!.visibility = ongoingShareVisibility


    }

    private fun refreshData() {
        HomeToWorkClient.getInstance().getUserShares({ shares ->

            mOngoingShare = null

            val ongoingShareOptional = Stream.of(shares)
                    .filter { value -> value.status == Share.Status.CREATED }
                    .findFirst()

            if (ongoingShareOptional.isPresent) {

                val ongoingShare = ongoingShareOptional.get()

                // Controllo se l'utente è host o guest della condivisione
                if (ongoingShare.host == HomeToWorkClient.getUser()) {
                    mOngoingShare = ongoingShare
                    shares.remove(mOngoingShare)
                } else {
                    // Se è guest controllo se ha completato la condivisione o è ancora in corso
                    val shareGuestOptional = Stream.of(ongoingShare.guests)
                            .filter { value -> value.user == HomeToWorkClient.getUser() && value.status == Guest.Status.JOINED }
                            .findFirst()
                    if (shareGuestOptional.isPresent) {
                        mOngoingShare = ongoingShare
                        shares.remove(mOngoingShare)
                    }
                }


            }

            mShareList.clear()
            mShareList.addAll(shares)
            initUI()

        }) { e ->
            //Toasty.error(getContext(), "Impossibile ottenere lista condivsioni al momento").show();
            initUI()
        }
    }

    fun joinShare() {
        if (ContextCompat.checkSelfPermission(mContext!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((mContext as MainActivity?)!!, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            val intentIntegrator = IntentIntegrator(activity)
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            intentIntegrator.setOrientationLocked(false)
            intentIntegrator.initiateScan()
        }
    }

    private fun checkShareCode(shareID: Long?, hostLocation: LatLng) {
        val materialDialog = MaterialDialog.Builder(mContext!!)
                .title(R.string.fragment_share_dialog_new_title)
                .content(R.string.fragment_share_dialog_join_content)
                .contentGravity(GravityEnum.CENTER)
                .progress(true, 150, true)
                .show()


        if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext!!)
            mFusedLocationClient.lastLocation.addOnSuccessListener { joinLocation ->

                if (joinLocation == null) {
                    Toasty.error(mContext!!, mContext!!.getString(R.string.activity_ongoing_share_invalid_code)).show()
                    materialDialog.dismiss()
                    return@mFusedLocationClient.getLastLocation().addOnSuccessListener
                }

                if (hostLocation.distanceTo(joinLocation) > 500) {
                    Toasty.error(mContext!!, mContext!!.getString(R.string.activity_ongoing_share_invalid_code)).show()
                    materialDialog.dismiss()
                    return@mFusedLocationClient.getLastLocation().addOnSuccessListener
                }

                HomeToWorkClient.getInstance().joinShare(shareID, joinLocation, { share ->
                    materialDialog.dismiss()
                    val intent = Intent(activity, OngoingShareActivity::class.java)
                    intent.putExtra(EXTRA_SHARE, share)
                    mContext!!.startActivity(intent)
                }) { e ->
                    Toasty.error(mContext!!, mContext!!.getString(R.string.activity_signin_server_error)).show()
                    materialDialog.dismiss()
                    e.printStackTrace()
                }

            }

        }
    }

    private fun createShare() {
        val materialDialog = MaterialDialog.Builder(mContext!!)
                .title(R.string.fragment_share_dialog_new_title)
                .content(R.string.fragment_share_dialog_new_content)
                .contentGravity(GravityEnum.CENTER)
                .progress(true, 150, true)
                .show()

        HomeToWorkClient.getInstance().createShare({ share ->
            materialDialog.dismiss()
            fabNewShare!!.visibility = View.GONE
            mOngoingShare = share
            initUI()
            val intent = Intent(activity, OngoingShareActivity::class.java)
            intent.putExtra(EXTRA_SHARE, share)
            mContext!!.startActivity(intent)
        }) { e ->
            materialDialog.dismiss()
            Toasty.error(mContext!!, mContext!!.getString(R.string.fragment_share_dialog_new_error)).show()
        }


    }

    companion object {

        private val CAMERA_PERMISSION_REQUEST_CODE = 1
    }

}// Required empty public constructor
