package it.gruppoinfor.home2work.fragments

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView

import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog

import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.EditProfileActivity
import it.gruppoinfor.home2work.activities.MainActivity
import it.gruppoinfor.home2work.activities.SettingsActivity
import it.gruppoinfor.home2work.activities.SignInActivity
import it.gruppoinfor.home2work.custom.AppBarStateChangeListener
import it.gruppoinfor.home2work.custom.AvatarView
import it.gruppoinfor.home2work.custom.ProgressBarAnimation
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2work.utils.Converters
import it.gruppoinfor.home2work.utils.ImageTools
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Experience
import it.gruppoinfor.home2workapi.model.Statistics
import it.gruppoinfor.home2workapi.model.User
import it.gruppoinfor.home2workapi.model.UserProfile
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

import android.app.Activity.RESULT_OK


class ProfileFragment : Fragment() {

    private val REQ_CODE_AVATAR = 1
    private val REQ_CODE_EXTERNAL_STORAGE = 2

    @BindView(R.id.name_text_view)
    internal var nameTextView: TextView? = null
    @BindView(R.id.job_text_view)
    internal var jobTextView: TextView? = null
    @BindView(R.id.swipe_refresh_layout)
    internal var swipeRefreshLayout: SwipeRefreshLayout? = null
    @BindView(R.id.avatar_view)
    internal var avatarView: AvatarView? = null
    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null
    @BindView(R.id.appBar)
    internal var appBar: AppBarLayout? = null
    @BindView(R.id.collapsingToolbar)
    internal var collapsingToolbar: CollapsingToolbarLayout? = null
    @BindView(R.id.text_name_small)
    internal var textNameSmall: TextView? = null
    @BindView(R.id.toolbar_layout)
    internal var toolbarLayout: View? = null
    @BindView(R.id.rootView)
    internal var rootView: CoordinatorLayout? = null
    @BindView(R.id.text_exp)
    internal var textExp: TextView? = null
    @BindView(R.id.progress_exp)
    internal var progressExp: ProgressBar? = null
    @BindView(R.id.text_exp_left)
    internal var textExpLeft: TextView? = null
    @BindView(R.id.text_shared_distance)
    internal var textSharedDistance: TextView? = null
    @BindView(R.id.text_regdate)
    internal var textRegdate: TextView? = null
    @BindView(R.id.text_exp_lv)
    internal var textExpLv: TextView? = null
    private var mUnbinder: Unbinder? = null
    private var mContext: Context? = null
    private var mProfile: UserProfile? = null
    private var mExpOld: Experience? = null
    private var df: DecimalFormat? = null

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        mUnbinder = ButterKnife.bind(this, rootView)

        df = DecimalFormat("#,##0.00")
        df!!.decimalFormatSymbols = DecimalFormatSymbols(Locale.ITALY)

        setHasOptionsMenu(true)
        initUI()
        refreshData()
        return rootView
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser && mProfile == null) refreshData()
        super.setUserVisibleHint(isVisibleToUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQ_CODE_AVATAR && resultCode == RESULT_OK) {
            try {

                val selectedImageUri = data.data
                val bitmap = MediaStore.Images.Media.getBitmap(mContext!!.contentResolver, selectedImageUri)
                val propic = ImageTools.shrinkBitmap(bitmap, 300)

                val file = Converters.bitmapToFile(mContext!!, propic)
                val decodedAvatar = ImageTools.decodeFile(file.path)
                val decodedFile = File(decodedAvatar)

                val mime = ImageTools.getMimeType(decodedFile.path)
                val mediaType = MediaType.parse(mime!!)

                val requestFile = RequestBody.create(mediaType, decodedFile)

                val filename = HomeToWorkClient.getUser().id!!.toString() + ".jpg"

                val body = MultipartBody.Part.createFormData("avatar", filename, requestFile)

                val materialDialog = MaterialDialog.Builder(mContext!!)
                        .content(R.string.fragment_profile_avatar_upload)
                        .contentGravity(GravityEnum.CENTER)
                        .progress(true, 150, true)
                        .show()

                HomeToWorkClient.getInstance().uploadAvatar(body,
                        { responseBody ->
                            avatarView!!.setAvatarURL(HomeToWorkClient.getUser().avatarURL)
                            materialDialog.dismiss()
                        }
                ) { e ->
                    Toasty.error(mContext!!, getString(R.string.activity_edit_profile_avatar_upload_error)).show()
                    materialDialog.dismiss()
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onDestroyView() {
        mUnbinder!!.unbind()
        super.onDestroyView()
    }

    @OnClick(R.id.profile_options_button)
    fun onProfileOptionsClicked() {

        MaterialDialog.Builder(mContext!!)
                .items(*mContext!!.resources.getStringArray(R.array.fragment_profile_options))
                .itemsCallback { dialog, itemView, position, text ->
                    when (position) {
                        0 -> startActivity(Intent(activity, EditProfileActivity::class.java))
                        1 -> startActivity(Intent(activity, SettingsActivity::class.java))
                        2 -> {
                            val builder = AlertDialog.Builder(mContext!!)
                            builder.setTitle(R.string.dialog_logout_title)
                            builder.setMessage(R.string.dialog_logout_content)
                            builder.setPositiveButton(R.string.dialog_logout_confirm) { dialogInterface, i -> logout() }
                            builder.setNegativeButton(R.string.dialog_logout_decline, null)
                            builder.show()
                        }
                    }
                }
                .show()

    }

    @OnClick(R.id.avatar_view)
    fun onAvatarClicked() {

        MaterialDialog.Builder(mContext!!)
                .title(R.string.fragment_profile_avatar_title)
                .items(*mContext!!.resources.getStringArray(R.array.fragment_profile_avatar_options))
                .itemsCallback { dialog, itemView, position, text ->
                    when (position) {
                        0 -> if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((mContext as MainActivity?)!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQ_CODE_EXTERNAL_STORAGE)
                        } else {
                            selectImageIntent()
                        }
                    }
                }
                .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQ_CODE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                selectImageIntent()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun selectImageIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.activity_configuration_avatar_selection)), REQ_CODE_AVATAR)
    }

    private fun initUI() {
        appBar!!.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: AppBarStateChangeListener.State) {
                when (state) {
                    AppBarStateChangeListener.State.COLLAPSED -> if (toolbarLayout!!.alpha < 1.0f) {
                        toolbarLayout!!.visibility = View.VISIBLE
                        toolbarLayout!!.animate()
                                //.translationY(toolbarLayout.getHeight())
                                .alpha(1.0f)
                                .setListener(null)
                    }
                    AppBarStateChangeListener.State.IDLE -> if (toolbarLayout!!.alpha > 0.0f) {
                        toolbarLayout!!.animate()
                                .translationY(0f)
                                .alpha(0.0f)
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        super.onAnimationEnd(animation)
                                        toolbarLayout!!.visibility = View.GONE
                                    }
                                })
                    }
                }
            }
        })

        swipeRefreshLayout!!.setOnRefreshListener {
            swipeRefreshLayout!!.isRefreshing = true
            refreshData()
        }
        swipeRefreshLayout!!.setColorSchemeResources(R.color.colorAccent)

        val user = HomeToWorkClient.getUser()
        avatarView!!.setAvatarURL(user.avatarURL)
        nameTextView!!.text = user.toString()
        jobTextView!!.text = user.company.toString()
        textNameSmall!!.text = user.toString()

        val simpleDate = SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN)
        val strDt = simpleDate.format(user.regdate)
        textRegdate!!.text = strDt

    }

    private fun refreshData() {
        HomeToWorkClient.getInstance().getUserProfile({ userProfile ->
            swipeRefreshLayout!!.isRefreshing = false
            mProfile = userProfile
            refreshUI()
        }) { e ->
            Toasty.error(mContext!!, "Impossibile ottenere informazioni del profilo al momento").show()
            swipeRefreshLayout!!.isRefreshing = false
        }

    }

    private fun refreshUI() {
        avatarView!!.setLevel(mProfile!!.exp.level!!)

        val exp = mProfile!!.exp

        val anim = ProgressBarAnimation(progressExp,
                if (mExpOld == null) 0 else mExpOld!!.progress,
                exp.progress)
        anim.duration = 500
        progressExp!!.startAnimation(anim)

        textExp!!.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_exp_value), exp.value, exp.nextLvlExp)
        textExpLv!!.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_exp_level), exp.level)

        textExpLeft!!.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_exp_left), exp.nextLvlExp!! - exp.value!!)

        mExpOld = exp

        val stats = mProfile!!.stats

        //textShares.setText(String.format(Locale.ITALIAN, "%1$d", stats.getShares()));
        val distanceInKm = stats.sharedDistance!! / 1000f
        textSharedDistance!!.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_distance_value), distanceInKm)
    }


    private fun logout() {
        SessionManager.clearSession(context!!)

        // Avvio Activity di login
        val i = Intent(context, SignInActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

}// Required empty public constructor


