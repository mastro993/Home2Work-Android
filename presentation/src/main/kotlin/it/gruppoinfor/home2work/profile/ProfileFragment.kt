package it.gruppoinfor.home2work.profile

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.LocalUserData
import it.gruppoinfor.home2work.common.ProgressBarAnimation
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.entities.Experience
import it.gruppoinfor.home2work.entities.Profile
import it.gruppoinfor.home2work.extensions.hide
import it.gruppoinfor.home2work.extensions.launchActivity
import it.gruppoinfor.home2work.extensions.show
import it.gruppoinfor.home2work.extensions.showToast
import it.gruppoinfor.home2work.settings.SettingsActivity
import it.gruppoinfor.home2work.sharehistory.ShareHistoryActivity
import it.gruppoinfor.home2work.views.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.view_activity_details.*
import kotlinx.android.synthetic.main.view_exp_details.*
import kotlinx.android.synthetic.main.view_profile_header.*
import kotlinx.android.synthetic.main.view_shares_details.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class ProfileFragment : Fragment() {

    @Inject
    lateinit var factory: ProfileVMFactory
    @Inject
    lateinit var localUserData: LocalUserData

    private var currentExp: Experience? = null
    private var profile: Profile? = null

    private lateinit var viewModel: ProfileViewModel
    private lateinit var screenStateView: ScreenStateView
    private lateinit var optionsButton: ImageButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var avatarView: AvatarView
    private lateinit var nameTextSmall: TextView
    private lateinit var nameText: TextView
    private lateinit var jobText: TextView

    private lateinit var experienceValueText: TextView
    private lateinit var experienceProgressBar: ProgressBar
    private lateinit var nextLeveLExpText: TextView
    private lateinit var currentLevelExpText: TextView

    private lateinit var activityChart: LineChart

    private lateinit var sharesChart: PieChart

    private lateinit var sharesHistoryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DipendencyInjector.createProfileComponent().inject(this)
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        viewModel.getProfile()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.viewState.observe(this, Observer {
            it?.let { handleViewState(it) }
        })

        viewModel.errorState.observe(this, Observer {
            it?.let { showToast(it) }
        })


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        appBar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: AppBarStateChangeListener.State) {

                when (state) {
                    AppBarStateChangeListener.State.COLLAPSED -> if (text_name_small.alpha < 1.0f) {
                        text_name_small.visibility = View.VISIBLE
                        text_name_small.animate()
                                //.translationY(toolbarLayout.getHeight())
                                .alpha(1.0f)
                                .setListener(null)
                    }
                    AppBarStateChangeListener.State.IDLE -> if (text_name_small.alpha > 0.0f) {
                        text_name_small.animate()
                                .translationY(0f)
                                .alpha(0.0f)
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        super.onAnimationEnd(animation)
                                        text_name_small.visibility = View.GONE
                                    }
                                })
                    }
                    AppBarStateChangeListener.State.EXPANDED -> {
                    }
                }

            }
        })

        screenStateView = status_view
        optionsButton = profile_options_button
        swipeRefreshLayout = swipe_refresh_layout
        avatarView = avatar_view
        nameTextSmall = text_name_small
        nameText = name_text_view
        jobText = job_text_view

        experienceValueText = text_exp_value
        currentLevelExpText = text_current_lvl_exp
        nextLeveLExpText = text_next_lvl_exp
        experienceProgressBar = progress_exp

        activityChart = chart_activity

        sharesChart = chart_shares

        sharesHistoryButton = button_shares_history

        initUI()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        DipendencyInjector.releaseProfileComponent()
    }

    private fun initUI() {

        optionsButton.setOnClickListener {
            context?.launchActivity<SettingsActivity>()
        }

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshProfile()
        }

        localUserData.user?.let {
            nameTextSmall.text = it.fullName
            avatarView.setAvatarURL(it.avatarUrl)
            nameText.text = it.fullName
            jobText.text = it.company.formattedName
        }

        activityChart.setViewPortOffsets(16f, 0f, 16f, 0f)
        activityChart.description.isEnabled = false
        activityChart.isDragEnabled = false
        activityChart.setPinchZoom(false)
        activityChart.setScaleEnabled(false)
        activityChart.setDrawGridBackground(false)
        activityChart.maxHighlightDistance = 300f
        activityChart.animateY(1400, Easing.EasingOption.EaseInOutQuad)
        activityChart.xAxis.isEnabled = true
        activityChart.xAxis.axisLineColor = ContextCompat.getColor(context!!, R.color.colorAccent)
        activityChart.xAxis.labelCount = 6
        activityChart.xAxis.granularity = 1f
        activityChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        activityChart.xAxis.setDrawGridLines(false)
        activityChart.xAxis.valueFormatter = ProfileFragment.ActivityXAxisValueFormatter()
        activityChart.xAxis.textColor = ContextCompat.getColor(context!!, R.color.colorAccent)
        activityChart.xAxis.setAvoidFirstLastClipping(true)
        activityChart.axisLeft.isEnabled = true
        activityChart.axisLeft.axisLineColor = ContextCompat.getColor(context!!, R.color.colorAccent)
        activityChart.axisLeft.labelCount = 3
        activityChart.axisLeft.gridColor = ContextCompat.getColor(context!!, R.color.colorAccentAlpha26)
        activityChart.axisLeft.textColor = ContextCompat.getColor(context!!, R.color.colorAccent)
        activityChart.axisRight.isEnabled = false
        activityChart.legend.isEnabled = false

        sharesChart.description.isEnabled = false
        sharesChart.isRotationEnabled = false
        sharesChart.setEntryLabelColor(ContextCompat.getColor(context!!, R.color.dark_bg_light_primary_text))
        sharesChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD)
        sharesChart.maxAngle = 180f
        sharesChart.rotationAngle = 180f
        sharesChart.setCenterTextOffset(0f, -22f)
        sharesChart.animateY(1400, Easing.EasingOption.EaseInOutQuad)
        sharesChart.setTransparentCircleAlpha(110)
        sharesChart.holeRadius = 58f
        sharesChart.transparentCircleRadius = 61f
        sharesChart.legend.isEnabled = false

        sharesHistoryButton.setOnClickListener {
            context?.launchActivity<ShareHistoryActivity>()
        }


    }

    private fun handleViewState(state: ProfileViewState) {

        screenStateView.setScreenState(state.screenState)
        swipeRefreshLayout.isRefreshing = state.isRefreshing

        state.profile?.let {
            profile_container.show()

            avatarView.setLevel(it.exp.level)
            this.profile = it

            updateExpView()
            updateActivityView()
            updateSharesView()


            val simpleDate = SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN)
            val strDt = simpleDate.format(it.regdate)
            text_regdate.text = strDt

        } ?: profile_container.hide()


    }

    private fun updateExpView() {
       profile?.let {
           val anim = ProgressBarAnimation(experienceProgressBar, currentExp?.progress, it.exp.progress)
           anim.duration = 500
           experienceProgressBar.startAnimation(anim)

           experienceValueText.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_exp_value), it.exp.amount)
           currentLevelExpText.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_current_lvl_exp_value), it.exp.currentLvLExp)
           nextLeveLExpText.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_next_lvl_exp_value), it.exp.nextLvlExp)
           currentExp = it.exp
       }
    }

    private fun updateActivityView(){
        profile?.let {
            text_shared_distance_value.text = String.format(Locale.ITALY, "%.2f km", it.stats.sharedDistance.div(1000f))
            text_month_shared_distance_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), it.stats.monthSharedDistance.div(1000f))
            text_month_shared_distance_avg_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), it.stats.monthSharedDistanceAvg.div(1000f))

            if (it.stats.sharedDistance > 0) {

                no_activity_chart_data_view.hide()
                chart_activity.show()

                val colors = ArrayList<Int>()
                colors.add(ContextCompat.getColor(context!!, R.color.colorAccent))

                val dataSets = ArrayList<ILineDataSet>()

                val activityEntries = ArrayList<Entry>()
                val avgEntries = ArrayList<Entry>()

                val thisMonth = Calendar.getInstance().get(Calendar.MONTH)

                for (i in 5 downTo 0) {

                    var month = thisMonth - i
                    if (month < 0) month += 12


                    val activityEntry = Entry(5 - i.toFloat(), it.activity[month + 1]?.distance?.toFloat()?.div(1000f)
                            ?: 0f)
                    val avgEntry = Entry(5 - i.toFloat(), it.stats.monthSharedDistanceAvg.div(1000f))

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

                no_activity_chart_data_view.show()
                chart_activity.hide()

            }
        }
    }

    private fun updateSharesView(){

        profile?.let {
            text_month_shares.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month), SimpleDateFormat("MMMM", Locale.ITALY).format(Date()).capitalize())
            text_month_shares_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_value), it.stats.monthShares)
            text_month_shares_avg_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_avg_value), it.stats.monthlySharesAvg)
            text_month_shares_record_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_value), it.stats.bestMonthShares)
            text_longest_share_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), it.stats.longestShare.div(1000f))

            if (it.stats.totalShares > 0) {

                no_share_chart_data_view.hide()
                chart_shares.show()

                val s = SpannableString("${it.stats.totalShares}\ncondivisioni effettuate")
                s.setSpan(RelativeSizeSpan(2.1f), 0, "${it.stats.totalShares}".length, 0)
                s.setSpan(StyleSpan(Typeface.BOLD), 0, "${it.stats.totalShares}".length, 0)
                s.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.light_bg_dark_primary_text)), 0, "${it.stats.totalShares}".length, 0)
                chart_shares.centerText = s

                val entriesPie = ArrayList<PieEntry>()
                if (it.stats.totalHostShares > 0) entriesPie.add(PieEntry(it.stats.totalHostShares.toFloat(), "Driver"))
                if (it.stats.totalGuestShares > 0) entriesPie.add(PieEntry(it.stats.totalGuestShares.toFloat(), "Guest"))

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

                no_share_chart_data_view.show()
                chart_shares.hide()

            }
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


