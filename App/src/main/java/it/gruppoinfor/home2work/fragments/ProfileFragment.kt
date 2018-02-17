package it.gruppoinfor.home2work.fragments

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.AppBarLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
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
import it.gruppoinfor.home2work.user.SessionManager
import it.gruppoinfor.home2work.utils.Const.REQ_CODE_AVATAR
import it.gruppoinfor.home2work.utils.Const.REQ_CODE_EXTERNAL_STORAGE
import it.gruppoinfor.home2work.utils.ImageUtils
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Experience
import it.gruppoinfor.home2workapi.model.UserProfile
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile_activity.*
import kotlinx.android.synthetic.main.fragment_profile_exp.*
import kotlinx.android.synthetic.main.fragment_profile_header.*
import kotlinx.android.synthetic.main.fragment_profile_shares.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {

    private lateinit var mProfile: UserProfile
    private var mExpOld: Experience = Experience()
    private var df: DecimalFormat = DecimalFormat("#,##0.00")

    init {
        df.decimalFormatSymbols = DecimalFormatSymbols(Locale.ITALY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        setHasOptionsMenu(true)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initSharesChart()
        initActivityChart()
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

        avatar_view.setAvatarURL(HomeToWorkClient.user?.avatarURL)
        avatar_view_small.setAvatarURL(HomeToWorkClient.user?.avatarURL)

        name_text_view.text = HomeToWorkClient.user.toString()
        job_text_view.text = HomeToWorkClient.user?.company.toString()
        text_name_small.text = HomeToWorkClient.user.toString()

        val simpleDate = SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN)
        val strDt = simpleDate.format(HomeToWorkClient.user?.regdate)
        text_regdate.text = strDt

    }

    private fun initSharesChart() {

        // TODO rimuovere valore se 0 %

        chart_shares.setUsePercentValues(true)
        chart_shares.description.isEnabled = false
        chart_shares.isRotationEnabled = false
        chart_shares.setEntryLabelColor(ContextCompat.getColor(context!!, R.color.dark_bg_light_primary_text))
        chart_shares.setEntryLabelTypeface(Typeface.DEFAULT_BOLD)
        chart_shares.maxAngle = 180f
        chart_shares.rotationAngle = 180f
        chart_shares.setCenterTextOffset(0f, -22f)
        chart_shares.animateY(1400, Easing.EasingOption.EaseInOutQuad)
        chart_shares.setTransparentCircleAlpha(110)
        chart_shares.holeRadius = 58f
        chart_shares.transparentCircleRadius = 61f

        val legend = chart_shares.legend
        legend.isEnabled = false

    }

    private fun initActivityChart() {

        // TODO marker con info mese selezionato

        chart_activity.setViewPortOffsets(0f, 0f, 0f, 0f)
        chart_activity.description.isEnabled = false
        chart_activity.isDragEnabled = false
        chart_activity.setPinchZoom(false)
        chart_activity.setScaleEnabled(false)
        chart_activity.setDrawGridBackground(false)
        chart_activity.maxHighlightDistance = 300f
        chart_activity.animateY(1400, Easing.EasingOption.EaseInOutQuad)
        chart_activity.xAxis.isEnabled = false
        chart_activity.axisLeft.isEnabled = false
        chart_activity.axisRight.isEnabled = false

        chart_activity.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                // TODO dialog con informazioni
                Toasty.info(context!!, e.toString()).show()
            }

            override fun onNothingSelected() {
                // ...
            }
        })

        val legend = chart_activity.legend
        legend.isEnabled = false

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
            refreshActivityChart()
            refreshSharesChart()
        }, OnFailureListener {
            Toasty.error(context!!, "Impossibile ottenere informazioni del profilo al momento").show()
            swipe_refresh_layout.isRefreshing = false
        })

    }

    private fun refreshUI() {

        // Avatar
        avatar_view.setLevel(mProfile.exp.level)
        avatar_view_small.setLevel(mProfile.exp.level)

        // Exp
        val anim = ProgressBarAnimation(progress_exp, mExpOld.progress, mProfile.exp.progress)
        anim.duration = 500
        progress_exp.startAnimation(anim)

        text_exp_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_exp_value), mProfile.exp.value)
        text_exp_left.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_exp_left), mProfile.exp.expForNextLevel)

        mExpOld = mProfile.exp

        // Condivisioni
        text_month_shares.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month), SimpleDateFormat("MMMM", Locale.ITALY).format(Date()).capitalize())
        text_month_shares_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_value), mProfile.stats.monthShares)
        text_month_shares_avg_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_avg_value), mProfile.stats.monthSharesAvg)

        // Attività
        // TODO attività ultimi 6 mesi
        // TODO mese più attivo
        text_month_shared_distance_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), mProfile.stats.monthSharedDistance)
        text_month_shared_distance_avg_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), mProfile.stats.monthSharedDistanceAvg)

    }

    private fun refreshSharesChart() {

        val s = SpannableString("${mProfile.stats.shares}\ncondivisioni effettuate")
        s.setSpan(RelativeSizeSpan(2.1f), 0, "${mProfile.stats.shares}".length, 0)
        s.setSpan(StyleSpan(Typeface.BOLD), 0, "${mProfile.stats.shares}".length, 0)
        s.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.light_bg_dark_primary_text)), 0, "${mProfile.stats.shares}".length, 0)
        chart_shares.centerText = s

        // TODO ottenere numero di condivisioni da driver o guest
        val entriesPie = ArrayList<PieEntry>()
        entriesPie.add(PieEntry(mProfile.stats.hostShares.toFloat(), "Driver"))
        entriesPie.add(PieEntry(mProfile.stats.guestShares.toFloat(), "Guest"))

        val pieDataSet = PieDataSet(entriesPie, null) // add entries to dataset
        pieDataSet.sliceSpace = 2f

        val colors = ArrayList<Int>()
        colors.add(ContextCompat.getColor(context!!, R.color.colorPrimary))
        colors.add(ContextCompat.getColor(context!!, R.color.colorAccent))

        pieDataSet.colors = colors

        val pieData = PieData(pieDataSet)
        pieData.setValueTextColor(ContextCompat.getColor(context!!, R.color.dark_bg_light_primary_text))
        pieData.setValueFormatter(PercentFormatter())
        pieData.setValueTextSize(16f)

        chart_shares.data = pieData
        chart_shares.invalidate()

    }

    private fun refreshActivityChart() {

        val colors = ArrayList<Int>()
        colors.add(ContextCompat.getColor(context!!, R.color.colorAccent))

        val entries = ArrayList<Entry>()
        for (i in 1..6) {
            // turn your data into Entry objects
            val random = Math.floor(1 + Random().nextDouble() * (50 - 1))
            entries.add(Entry(i.toFloat(), random.toFloat(), "Ciao"))
        }

        val dataSet = LineDataSet(entries, "Label") // add entries to dataset
        dataSet.color = ContextCompat.getColor(context!!, R.color.colorAccent)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawFilled(true)
        dataSet.fillDrawable = ContextCompat.getDrawable(context!!, R.drawable.drawable_activity_chart_fill)
        dataSet.fillAlpha = 100
        dataSet.lineWidth = 3f
        dataSet.setDrawCircles(true)
        dataSet.setDrawCircleHole(true)
        dataSet.circleRadius = 6f
        dataSet.circleHoleRadius = 4f
        dataSet.setCircleColorHole(ContextCompat.getColor(context!!, R.color.cardview_light_background))
        dataSet.setDrawValues(false)
        dataSet.valueTextColor = ContextCompat.getColor(context!!, R.color.light_bg_dark_primary_text)
        dataSet.valueTextSize = 12f
        dataSet.setDrawHorizontalHighlightIndicator(false)
        dataSet.setDrawVerticalHighlightIndicator(false)
        dataSet.circleColors = colors

        val lineData = LineData(dataSet)
        chart_activity.data = lineData
        chart_activity.invalidate()

    }

    private fun logout() {

        SessionManager.clearSession(context!!)

        // Avvio Activity di login
        val i = Intent(context, SignInActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)

    }


}


