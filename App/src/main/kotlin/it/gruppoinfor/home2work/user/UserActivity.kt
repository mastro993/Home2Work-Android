package it.gruppoinfor.home2work.user

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.MenuItem
import android.view.View
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.chat.ChatActivityArgs
import it.gruppoinfor.home2work.extensions.showToast
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.layout_profile_activity.*
import kotlinx.android.synthetic.main.layout_profile_header.*
import kotlinx.android.synthetic.main.layout_profile_shares.*
import java.text.SimpleDateFormat
import java.util.*

class UserActivity : AppCompatActivity(), UserView {

    private lateinit var mUserPresenter: UserPresenter

    private val args by lazy {
        UserActivityArgs.deserializeFrom(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_user)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        mUserPresenter = UserPresenterImpl(this, args.userId)

        initUI()

        mUserPresenter.onCreate()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
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

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh_layout.setOnRefreshListener { mUserPresenter.onRefresh() }

        button_send_message.setOnClickListener {
            ChatActivityArgs(
                    chatId = 0L,
                    recipientId = args.userId,
                    recipientName = args.userName
            ).launch(this)
        }

        // Header
        text_name_small.text = args.userName
        avatar_view_small.setAvatarURL(args.userAvatarUrl)
        avatar_view.setAvatarURL(args.userAvatarUrl)
        name_text_view.text = args.userName
        job_text_view.text = args.userCompanyName

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
        chart_activity.xAxis.axisLineColor = ContextCompat.getColor(this, R.color.colorAccent)
        chart_activity.xAxis.labelCount = 6
        chart_activity.xAxis.granularity = 1f
        chart_activity.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart_activity.xAxis.setDrawGridLines(false)
        chart_activity.xAxis.valueFormatter = ProfileFragment.ActivityXAxisValueFormatter()
        chart_activity.xAxis.textColor = ContextCompat.getColor(this, R.color.colorAccent)
        chart_activity.xAxis.setAvoidFirstLastClipping(true)
        chart_activity.axisLeft.isEnabled = true
        chart_activity.axisLeft.axisLineColor = ContextCompat.getColor(this, R.color.colorAccent)
        chart_activity.axisLeft.labelCount = 3
        chart_activity.axisLeft.gridColor = ContextCompat.getColor(this, R.color.colorAccentAlpha26)
        chart_activity.axisLeft.textColor = ContextCompat.getColor(this, R.color.colorAccent)
        chart_activity.axisRight.isEnabled = false
        chart_activity.legend.isEnabled = false

        // Condivisioni
        chart_shares.setUsePercentValues(true)
        chart_shares.description.isEnabled = false
        chart_shares.isRotationEnabled = false
        chart_shares.setEntryLabelColor(ContextCompat.getColor(this, R.color.dark_bg_light_primary_text))
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

    override fun setProfileData(userProfile: UserProfile) {

        status_view.done()
        profile_container.visibility = View.VISIBLE

        // Header
        val exp = userProfile.exp
        avatar_view.setLevel(exp.level)
        avatar_view_small.setLevel(exp.level)

        val stats = userProfile.stats

        // Footer
        val simpleDate = SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN)
        val strDt = simpleDate.format(userProfile.regdate)
        text_regdate.text = strDt

        // Attività
        text_shared_distance_value.text = String.format(Locale.ITALY, "%.2f km", stats.sharedDistance.div(1000f))
        text_month_shared_distance_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), stats.monthSharedDistance.div(1000f))
        text_month_shared_distance_avg_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), stats.monthSharedDistanceAvg.div(1000f))

        if (stats.sharedDistance > 0) {

            val colors = ArrayList<Int>()
            colors.add(ContextCompat.getColor(this, R.color.colorAccent))

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
            avgDataSet.color = ContextCompat.getColor(this, R.color.colorPrimary)
            avgDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            avgDataSet.setDrawFilled(true)
            avgDataSet.fillDrawable = ContextCompat.getDrawable(this, R.drawable.bg_chart_activity_avg_fill)
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
            activityDataSet.color = ContextCompat.getColor(this, R.color.colorAccent)
            activityDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            activityDataSet.setDrawFilled(true)
            activityDataSet.fillDrawable = ContextCompat.getDrawable(this, R.drawable.bg_chart_activity_fill)
            activityDataSet.fillAlpha = 100
            activityDataSet.lineWidth = 3f
            activityDataSet.setDrawCircles(true)
            activityDataSet.setDrawCircleHole(true)
            activityDataSet.circleRadius = 6f
            activityDataSet.circleHoleRadius = 4f
            activityDataSet.setCircleColorHole(ContextCompat.getColor(this, R.color.cardview_light_background))
            activityDataSet.setDrawValues(false)
            activityDataSet.valueTextColor = ContextCompat.getColor(this, R.color.light_bg_dark_primary_text)
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
            s.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.light_bg_dark_primary_text)), 0, "${stats.totalShares}".length, 0)
            chart_shares.centerText = s

            val entriesPie = ArrayList<PieEntry>()
            if (stats.totalHostShares > 0) entriesPie.add(PieEntry(stats.totalHostShares.toFloat(), "Driver"))
            if (stats.totalGuestShares > 0) entriesPie.add(PieEntry(stats.totalGuestShares.toFloat(), "Guest"))

            val pieDataSet = PieDataSet(entriesPie, null) // add entries to dataset
            pieDataSet.sliceSpace = 2f
            pieDataSet.setDrawValues(false)

            val colors = ArrayList<Int>()
            colors.add(ContextCompat.getColor(this, R.color.colorPrimary))
            colors.add(ContextCompat.getColor(this, R.color.colorAccent))

            pieDataSet.colors = colors

            val pieData = PieData(pieDataSet)
            pieData.setValueTextColor(ContextCompat.getColor(this, R.color.dark_bg_light_primary_text))
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

    override fun onLoading() {
        status_view.loading()
        profile_container.visibility = View.GONE
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

    override fun showErrorMessage(errorMessage: String) {
        showToast(errorMessage)
    }
}
