package it.gruppoinfor.home2work.user

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import it.gruppoinfor.home2work.Constants.REQ_CODE_AVATAR
import it.gruppoinfor.home2work.Constants.REQ_CODE_EXTERNAL_STORAGE
import it.gruppoinfor.home2work.MainActivity
import it.gruppoinfor.home2work.common.ProgressBarAnimation
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.extensions.showToast
import it.gruppoinfor.home2work.settings.SettingsActivity
import it.gruppoinfor.home2work.shares.SharesActivity
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.user.UserExp
import it.gruppoinfor.home2workapi.user.UserProfile
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_profile_activity.*
import kotlinx.android.synthetic.main.layout_profile_exp.*
import kotlinx.android.synthetic.main.layout_profile_header.*
import kotlinx.android.synthetic.main.layout_profile_shares.*
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment(), ProfileView {

    private val mProfilePresenter: ProfilePresenter = ProfilePresenterImpl(this)
    private var mExpOld: UserExp = UserExp()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        setHasOptionsMenu(true)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

        mProfilePresenter.onViewCreated()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data!=null){
            if (requestCode == REQ_CODE_AVATAR && resultCode == RESULT_OK) {
                mProfilePresenter.uploadAvatar(context!!, data.data)
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

    override fun onLoading() {
        profile_container.visibility = View.GONE
        status_view.loading()
    }

    override fun onLoadingError(errorMessage: String) {
        status_view.error(errorMessage)
    }

    override fun onRefresh() {
        swipe_refresh_layout.isRefreshing = true
    }

    override fun onRefreshDone() {
        swipe_refresh_layout.isRefreshing = false
    }

    override fun setProfileData(userProfile: UserProfile) {

        status_view.done()
        profile_container.visibility = View.VISIBLE

        // Header
        val exp = userProfile.exp
        avatar_view.setLevel(exp.level)
        avatar_view_small.setLevel(exp.level)

        // Exp
        val anim = ProgressBarAnimation(progress_exp, mExpOld.progress, exp.progress)
        anim.duration = 500
        progress_exp.startAnimation(anim)

        text_exp_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_exp_value), exp.amount)
        text_current_lvl_exp.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_current_lvl_exp_value), exp.currentLvLExp)
        text_next_lvl_exp.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_next_lvl_exp_value), exp.nextLvlExp)

        mExpOld = exp

        // Footer
        val simpleDate = SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN)
        val strDt = simpleDate.format(userProfile.regdate)
        text_regdate.text = strDt


        val stats = userProfile.stats

        // Attività
        text_shared_distance_value.text = String.format(Locale.ITALY, "%.2f km", stats.sharedDistance.div(1000f))
        text_month_shared_distance_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), stats.monthSharedDistance.div(1000f))
        text_month_shared_distance_avg_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), stats.monthSharedDistanceAvg.div(1000f))

        if (stats.sharedDistance > 0) {

            val colors = ArrayList<Int>()
            colors.add(ContextCompat.getColor(context!!, R.color.colorAccent))

            val dataSets = ArrayList<ILineDataSet>()

            val activityEntries = ArrayList<Entry>()
            val avgEntries = ArrayList<Entry>()

            val thisMonth = Calendar.getInstance().get(Calendar.MONTH)

            for (i in 5 downTo 0) {

                var month = thisMonth - i
                if (month < 0) month += 12

                val activity = userProfile.activity.find { it.month == month + 1 }
                val activityEntry = Entry(5 - i.toFloat(), activity?.distance?.toFloat()?.div(1000f)
                        ?: 0f)
                val avgEntry = Entry(5 - i.toFloat(), stats.monthSharedDistanceAvg.div(1000f))

                activityEntries.add(activityEntry)
                avgEntries.add(avgEntry)

            }

            // Grafico media
            Collections.sort(avgEntries, EntryXComparator())
            val avgDataSet = LineDataSet(avgEntries, "Media")
            avgDataSet.color = ContextCompat.getColor(context!!, R.color.colorPrimary)
            avgDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            avgDataSet.setDrawFilled(true)
            avgDataSet.fillDrawable = ContextCompat.getDrawable(context!!, R.drawable.bg_chart_activity_avg_fill)
            avgDataSet.fillAlpha = 100
            avgDataSet.lineWidth = 3f
            avgDataSet.setDrawCircles(false)
            avgDataSet.setDrawValues(false)
            avgDataSet.setDrawHorizontalHighlightIndicator(false)
            avgDataSet.setDrawVerticalHighlightIndicator(false)
            avgDataSet.enableDashedLine(20f, 15f, 0f)
            dataSets.add(avgDataSet)

            // Grafico attività
            Collections.sort(activityEntries, EntryXComparator())
            val activityDataSet = LineDataSet(activityEntries, "Attività")
            activityDataSet.color = ContextCompat.getColor(context!!, R.color.colorAccent)
            activityDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            activityDataSet.setDrawFilled(true)
            activityDataSet.fillDrawable = ContextCompat.getDrawable(context!!, R.drawable.bg_chart_activity_fill)
            activityDataSet.fillAlpha = 100
            activityDataSet.lineWidth = 3f
            activityDataSet.setDrawCircles(true)
            activityDataSet.setDrawCircleHole(true)
            activityDataSet.circleRadius = 6f
            activityDataSet.circleHoleRadius = 4f
            activityDataSet.setCircleColorHole(ContextCompat.getColor(context!!, R.color.cardview_light_background))
            activityDataSet.setDrawValues(false)
            activityDataSet.valueTextColor = ContextCompat.getColor(context!!, R.color.light_bg_dark_primary_text)
            activityDataSet.valueTextSize = 12f
            activityDataSet.setDrawHorizontalHighlightIndicator(false)
            activityDataSet.setDrawVerticalHighlightIndicator(false)
            activityDataSet.circleColors = colors
            dataSets.add(activityDataSet)


            val lineData = LineData(dataSets)
            chart_activity.data = lineData
            chart_activity.invalidate()

            no_activity_chart_data_view.visibility = View.GONE
            chart_activity.visibility = View.VISIBLE

        } else {

            no_activity_chart_data_view.visibility = View.VISIBLE
            chart_activity.visibility = View.GONE

        }

        // Condivisioni
        text_month_shares.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month), SimpleDateFormat("MMMM", Locale.ITALY).format(Date()).capitalize())
        text_month_shares_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_value), stats.monthShares)
        text_month_shares_avg_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_avg_value), stats.monthlySharesAvg)
        text_month_shares_record_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_value), stats.bestMonthShares)
        text_longest_share_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), stats.longestShare.div(1000f))

        if (stats.totalShares > 0) {

            val s = SpannableString("${stats.totalShares}\ncondivisioni effettuate")
            s.setSpan(RelativeSizeSpan(2.1f), 0, "${stats.totalShares}".length, 0)
            s.setSpan(StyleSpan(Typeface.BOLD), 0, "${stats.totalShares}".length, 0)
            s.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.light_bg_dark_primary_text)), 0, "${stats.totalShares}".length, 0)
            chart_shares.centerText = s

            val entriesPie = ArrayList<PieEntry>()
            if (stats.totalHostShares > 0) entriesPie.add(PieEntry(stats.totalHostShares.toFloat(), "Driver"))
            if (stats.totalGuestShares > 0) entriesPie.add(PieEntry(stats.totalGuestShares.toFloat(), "Guest"))

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

            no_share_chart_data_view.visibility = View.GONE
            chart_shares.visibility = View.VISIBLE

        } else {

            no_share_chart_data_view.visibility = View.VISIBLE
            chart_shares.visibility = View.GONE

        }

    }

    override fun showErrorMessage(errorMessage: String) {
        showToast(errorMessage)
    }

    override fun onAvatarUploaded() {
        avatar_view.setAvatarURL(HomeToWorkClient.user?.avatarURL)
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

        profile_options_button.setOnClickListener {
            startActivity(context!!.intentFor<SettingsActivity>())
        }

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh_layout.setOnRefreshListener { mProfilePresenter.onRefresh() }

        avatar_view.setOnLongClickListener {

            val dialog = BottomSheetDialog(context!!)
            val sheetView = layoutInflater.inflate(R.layout.dialog_avatar_options, null)

            dialog.setContentView(sheetView)
            dialog.show()

            sheetView.find<TextView>(R.id.avatar_dialog_edit).setOnClickListener {
                dialog.dismiss()
                if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((context as MainActivity?)!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQ_CODE_EXTERNAL_STORAGE)
                } else {
                    selectImageIntent()
                }
            }

            true

        }

        // Header
        avatar_view_small.setAvatarURL(HomeToWorkClient.user?.avatarURL)
        text_name_small.text = HomeToWorkClient.user.toString()
        avatar_view.setAvatarURL(HomeToWorkClient.user?.avatarURL)
        name_text_view.text = HomeToWorkClient.user.toString()
        job_text_view.text = HomeToWorkClient.user?.company.toString()

        // Attività
        chart_activity.setViewPortOffsets(16f, 0f, 16f, 0f)
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
        chart_activity.axisLeft.isEnabled = true
        chart_activity.axisLeft.axisLineColor = ContextCompat.getColor(context!!, R.color.colorAccent)
        chart_activity.axisLeft.labelCount = 3
        chart_activity.axisLeft.gridColor = ContextCompat.getColor(context!!, R.color.colorAccentAlpha26)
        chart_activity.axisLeft.textColor = ContextCompat.getColor(context!!, R.color.colorAccent)
        chart_activity.axisRight.isEnabled = false
        chart_activity.legend.isEnabled = false

        // Condivisioni
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
        chart_shares.legend.isEnabled = false

        button_shares_history.setOnClickListener {
            context!!.startActivity(context!!.intentFor<SharesActivity>())
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


