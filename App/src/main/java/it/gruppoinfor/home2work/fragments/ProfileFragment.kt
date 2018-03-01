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
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.*
import android.widget.Toast
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.MainActivity
import it.gruppoinfor.home2work.activities.SettingsActivity
import it.gruppoinfor.home2work.custom.AppBarStateChangeListener
import it.gruppoinfor.home2work.custom.ProgressBarAnimation
import it.gruppoinfor.home2work.utils.Const.REQ_CODE_AVATAR
import it.gruppoinfor.home2work.utils.Const.REQ_CODE_EXTERNAL_STORAGE
import it.gruppoinfor.home2work.utils.ImageUtils
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Experience
import it.gruppoinfor.home2workapi.model.UserProfile
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_profile_activity.*
import kotlinx.android.synthetic.main.layout_profile_exp.*
import kotlinx.android.synthetic.main.layout_profile_header.*
import kotlinx.android.synthetic.main.layout_profile_shares.*
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
        getProfile()

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

                HomeToWorkClient.uploadAvatar(body, OnSuccessListener
                {
                    avatar_view.setAvatarURL(HomeToWorkClient.user?.avatarURL)
                    materialDialog.dismiss()
                }
                        , OnFailureListener {
                    Toast.makeText(context!!, R.string.activity_edit_profile_avatar_upload_error, Toast.LENGTH_SHORT).show()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun selectImageIntent() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.activity_configuration_avatar_selection)), REQ_CODE_AVATAR)

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

        profile_options_button.setOnClickListener { startActivity(Intent(activity, SettingsActivity::class.java)) }

        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = true
            refreshProfile()
        }
        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)

        initHeaderUI()
        initSharesUI()
        initActivityUI()
        initFooterUI()

        getProfile()

    }

    private fun initHeaderUI() {

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
        avatar_view.setAvatarURL(HomeToWorkClient.user?.avatarURL)
        avatar_view_small.setAvatarURL(HomeToWorkClient.user?.avatarURL)

        name_text_view.text = HomeToWorkClient.user.toString()
        job_text_view.text = HomeToWorkClient.user?.company.toString()

        text_name_small.text = HomeToWorkClient.user.toString()

    }

    private fun initSharesUI() {

        //chart_shares.setUsePercentValues(true)
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

    private fun initActivityUI() {

        // TODO marker con info mese selezionato

        chart_activity.setViewPortOffsets(0f, 0f, 0f, 0f)
        chart_activity.description.isEnabled = false
        chart_activity.isDragEnabled = false
        chart_activity.setPinchZoom(false)
        chart_activity.setScaleEnabled(false)
        chart_activity.setDrawGridBackground(false)
        chart_activity.maxHighlightDistance = 300f
        chart_activity.animateY(1400, Easing.EasingOption.EaseInOutQuad)

        chart_activity.xAxis.isEnabled = true
        chart_activity.xAxis.axisLineColor = ContextCompat.getColor(context!!, R.color.colorAccent)
        chart_activity.xAxis.labelCount = 6
        chart_activity.xAxis.granularity = 1f
        chart_activity.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart_activity.xAxis.setDrawGridLines(false)
        chart_activity.xAxis.valueFormatter = ActivityXAxisValueFormatter()
        chart_activity.xAxis.textColor = ContextCompat.getColor(context!!, R.color.colorAccent)
        chart_activity.xAxis.setAvoidFirstLastClipping(true)

        chart_activity.axisLeft.isEnabled = false
        chart_activity.axisRight.isEnabled = false

/*        chart_activity.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                // ...
            }

            override fun onNothingSelected() {
                // ...
            }
        })*/

        val legend = chart_activity.legend
        legend.isEnabled = false

    }

    private fun initFooterUI() {

        val simpleDate = SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN)
        val strDt = simpleDate.format(HomeToWorkClient.user?.regdate)
        text_regdate.text = strDt

    }

    private fun getProfile() {

        status_view.loading()

        HomeToWorkClient.getProfile(OnSuccessListener { userProfile ->

            status_view.done()
            mProfile = userProfile
            refreshUI()

        }, OnFailureListener {

            status_view.error("Impossibile ottenere informazioni del profilo al momento")

        })

    }

    private fun refreshProfile() {

        HomeToWorkClient.getProfile(OnSuccessListener { userProfile ->

            mProfile = userProfile
            refreshHeaderUI()
            refreshExpUI()
            refreshSharesUI()
            refreshActivityUI()

            swipe_refresh_layout.isRefreshing = false

        }, OnFailureListener {

            Toast.makeText(context!!, "Impossibile aggiornare le informazioni del profilo al momento", Toast.LENGTH_SHORT).show()
            swipe_refresh_layout.isRefreshing = false

        })

    }

    private fun refreshUI() {

        refreshHeaderUI()
        refreshExpUI()
        refreshSharesUI()
        refreshActivityUI()

    }

    private fun refreshHeaderUI() {

        avatar_view.setLevel(mProfile.exp.level)
        avatar_view_small.setLevel(mProfile.exp.level)

    }

    private fun refreshExpUI() {

        val anim = ProgressBarAnimation(progress_exp, mExpOld.progress, mProfile.exp.progress)
        anim.duration = 500
        progress_exp.startAnimation(anim)

        text_exp_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_exp_value), mProfile.exp.amount)
        text_current_lvl_exp.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_current_lvl_exp_value), mProfile.exp.currentLvLExp)
        text_next_lvl_exp.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_next_lvl_exp_value), mProfile.exp.nextLvlExp)

        mExpOld = mProfile.exp

    }

    private fun refreshSharesUI() {

        if (mProfile.stats.shares > 0) {
            text_month_shares.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month), SimpleDateFormat("MMMM", Locale.ITALY).format(Date()).capitalize())
            text_month_shares_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_value), mProfile.stats.monthShares)
            text_month_shares_avg_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_avg_value), mProfile.stats.monthSharesAvg)

            val s = SpannableString("${mProfile.stats.shares}\ncondivisioni effettuate")
            s.setSpan(RelativeSizeSpan(2.1f), 0, "${mProfile.stats.shares}".length, 0)
            s.setSpan(StyleSpan(Typeface.BOLD), 0, "${mProfile.stats.shares}".length, 0)
            s.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.light_bg_dark_primary_text)), 0, "${mProfile.stats.shares}".length, 0)
            chart_shares.centerText = s

            val entriesPie = ArrayList<PieEntry>()
            if (mProfile.stats.hostShares > 0) entriesPie.add(PieEntry(mProfile.stats.hostShares.toFloat(), "Driver"))
            if (mProfile.stats.guestShares > 0) entriesPie.add(PieEntry(mProfile.stats.guestShares.toFloat(), "Guest"))

            val pieDataSet = PieDataSet(entriesPie, null) // add entries to dataset
            pieDataSet.sliceSpace = 2f
            pieDataSet.setDrawValues(false)

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

            view_no_shares.visibility = View.GONE

        } else {

            view_no_shares.visibility = View.VISIBLE


        }

    }

    private fun refreshActivityUI() {

        val stats = mProfile.stats

        if (stats.sharedDistance > 0) {
            text_shared_distance_value.text = String.format(Locale.ITALY, "%.2f km", stats.sharedDistance / 1000f)
            // TODO mese più attivo
            text_month_shared_distance_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), stats.monthSharedDistance / 1000f)
            text_month_shared_distance_avg_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), stats.monthSharedDistanceAvg / 1000f)

            val colors = ArrayList<Int>()
            colors.add(ContextCompat.getColor(context!!, R.color.colorAccent))

            val entries = ArrayList<Entry>()
            val cal = Calendar.getInstance()
            val thisMonth = cal.get(Calendar.MONTH)

            for (i in 5 downTo 0) {

                var month = thisMonth - i
                if (month < 0) month += 12

                val activity = mProfile.activity.find { it.month == month + 1 }
                val entry = Entry(5 - i.toFloat(), activity?.distance?.toFloat()?.div(1000f)
                        ?: 0f)
                entries.add(entry)

            }

            Collections.sort(entries, EntryXComparator())

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

            view_no_activity.visibility = View.GONE

        } else {

            view_no_activity.visibility = View.VISIBLE

        }

    }

    class ActivityXAxisValueFormatter : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {

            val cal = Calendar.getInstance()
            val thisMonth = cal.get(Calendar.MONTH)
            var month = thisMonth - (5 - value.toInt())
            if (month < 0) month += 12

            cal.set(Calendar.MONTH, month)
            return SimpleDateFormat("MMM", Locale.ITALY).format(cal.time).toUpperCase()
        }
    }


}


