package it.gruppoinfor.home2work.fragments

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.AppBarLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.EditProfileActivity
import it.gruppoinfor.home2work.activities.MainActivity
import it.gruppoinfor.home2work.activities.SettingsActivity
import it.gruppoinfor.home2work.activities.SignInActivity
import it.gruppoinfor.home2work.custom.AppBarStateChangeListener
import it.gruppoinfor.home2work.custom.ProgressBarAnimation
import it.gruppoinfor.home2work.user.Const.REQ_CODE_AVATAR
import it.gruppoinfor.home2work.user.Const.REQ_CODE_EXTERNAL_STORAGE
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2work.utils.ImageUtils
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Experience
import it.gruppoinfor.home2workapi.model.UserProfile
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile_card_exp.*
import kotlinx.android.synthetic.main.fragment_profile_header.*
import kotlinx.android.synthetic.main.fragment_profile_stats.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment() {


    private lateinit var mProfile: UserProfile
    private var mExpOld: Experience = Experience()
    private lateinit var df: DecimalFormat

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        df = DecimalFormat("#,##0.00")
        df.decimalFormatSymbols = DecimalFormatSymbols(Locale.ITALY)
        setHasOptionsMenu(true)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        refreshData()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) refreshData()
        super.setUserVisibleHint(isVisibleToUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQ_CODE_AVATAR && resultCode == RESULT_OK) {
            try {

                val selectedImageUri = data.data
                val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, selectedImageUri)
                val propic = ImageUtils.shrinkBitmap(bitmap, 300)

                val file = ImageUtils.bitmapToFile(context!!, propic)
                val decodedAvatar = ImageUtils.decodeFile(file.path)
                val decodedFile = File(decodedAvatar)

                val mime = ImageUtils.getMimeType(decodedFile.path)
                val mediaType = MediaType.parse(mime!!)

                val requestFile = RequestBody.create(mediaType, decodedFile)

                val filename = "${HomeToWorkClient.user?.id}.jpg"

                val body = MultipartBody.Part.createFormData("avatar", filename, requestFile)

                val materialDialog = MaterialDialog.Builder(context!!)
                        .content(R.string.fragment_profile_avatar_upload)
                        .contentGravity(GravityEnum.CENTER)
                        .progress(true, 150, true)
                        .show()

                HomeToWorkClient.getInstance().uploadAvatar(body, OnSuccessListener
                {
                    avatar_view.setAvatarURL(HomeToWorkClient.user?.avatarURL)
                    materialDialog.dismiss()
                }
                        , OnFailureListener {
                    Toasty.error(context!!, getString(R.string.activity_edit_profile_avatar_upload_error)).show()
                    materialDialog.dismiss()
                })


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQ_CODE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                selectImageIntent()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initUI() {
        appBar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: AppBarStateChangeListener.State) {
                when (state) {
                    AppBarStateChangeListener.State.COLLAPSED -> if (toolbar_layout.alpha < 1.0f) {
                        toolbar_layout.visibility = View.VISIBLE
                        toolbar_layout.animate()
                                //.translationY(toolbarLayout.getHeight())
                                .alpha(1.0f)
                                .setListener(null)
                    }
                    AppBarStateChangeListener.State.IDLE -> if (toolbar_layout.alpha > 0.0f) {
                        toolbar_layout.animate()
                                .translationY(0f)
                                .alpha(0.0f)
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        super.onAnimationEnd(animation)
                                        toolbar_layout.visibility = View.GONE
                                    }
                                })
                    }
                    AppBarStateChangeListener.State.EXPANDED -> {
                    }
                }
            }
        })
        avatar_view.setOnClickListener {
            MaterialDialog.Builder(context!!)
                    .title(R.string.fragment_profile_avatar_title)
                    .items(*context!!.resources.getStringArray(R.array.fragment_profile_avatar_options))
                    .itemsCallback { _, _, position, _ ->
                        when (position) {
                            0 -> if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((context as MainActivity?)!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQ_CODE_EXTERNAL_STORAGE)
                            } else {
                                selectImageIntent()
                            }
                        }
                    }
                    .show()
        }
        profile_options_button.setOnClickListener {
            MaterialDialog.Builder(context!!)
                    .items(*context!!.resources.getStringArray(R.array.fragment_profile_options))
                    .itemsCallback { _, _, position, _ ->
                        when (position) {
                            0 -> startActivity(Intent(activity, EditProfileActivity::class.java))
                            1 -> startActivity(Intent(activity, SettingsActivity::class.java))
                            2 -> {
                                val builder = AlertDialog.Builder(context!!)
                                builder.setTitle(R.string.dialog_logout_title)
                                builder.setMessage(R.string.dialog_logout_content)
                                builder.setPositiveButton(R.string.dialog_logout_confirm) { _, _ -> logout() }
                                builder.setNegativeButton(R.string.dialog_logout_decline, null)
                                builder.show()
                            }
                        }
                    }
                    .show()
        }

        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = true
            refreshData()
        }
        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)

        val user = HomeToWorkClient.user
        avatar_view.setAvatarURL(user?.avatarURL)
        name_text_view.text = user.toString()
        job_text_view.text = user?.company.toString()
        text_name_small.text = user.toString()

        val simpleDate = SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN)
        val strDt = simpleDate.format(user?.regdate)
        text_regdate.text = strDt

    }

    private fun selectImageIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.activity_configuration_avatar_selection)), REQ_CODE_AVATAR)
    }

    private fun refreshData() {
        HomeToWorkClient.getInstance().getUserProfile(OnSuccessListener { userProfile ->
            swipe_refresh_layout.isRefreshing = false
            mProfile = userProfile
            refreshUI()
        }, OnFailureListener {
            Toasty.error(context!!, "Impossibile ottenere informazioni del profilo al momento").show()
            swipe_refresh_layout.isRefreshing = false
        })

    }

    private fun refreshUI() {

        avatar_view.setLevel(mProfile.exp.level)

        val exp = mProfile.exp

        val anim = ProgressBarAnimation(progress_exp,
                mExpOld.progress,
                exp.progress)
        anim.duration = 500
        progress_exp!!.startAnimation(anim)

        text_exp.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_exp_value), exp.value, exp.nextLvlExp)
        text_exp_lv.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_exp_level), exp.level)

        text_exp_left.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_exp_left), exp.expForNextLevel)

        mExpOld = exp

        val stats = mProfile.stats
        //textShares.setText(String.format(Locale.ITALIAN, "%1$d", stats.getShares()));
        val distanceInKm = stats.sharedDistance / 1000f
        shared_distance_text_view.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_distance_value), distanceInKm)
    }


    private fun logout() {
        SessionManager.clearSession(context!!)

        // Avvio Activity di login
        val i = Intent(context, SignInActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

}// Required empty public constructor


