package it.gruppoinfor.home2work.common.extensions

import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.ProgressBar
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
import it.gruppoinfor.home2work.common.views.ProgressBarAnimation
import it.gruppoinfor.home2work.entities.SharingActivity
import it.gruppoinfor.home2work.entities.Statistics
import java.text.SimpleDateFormat
import java.util.*

/**
 * Estensioni per semplificare il setUp dei profili in ProfileFragment ed UserActivity
 */

fun ProgressBar.animateTo(value: Float) {
    val anim = ProgressBarAnimation(this, this.progress.toFloat(), value).apply {
        duration = 500
    }
    startAnimation(anim)
}

fun LineChart.setUp() {

    setViewPortOffsets(32f, 0f, 32f, 0f)

    description.apply {
        isEnabled = false
    }

    isDragEnabled = false

    setPinchZoom(false)
    setScaleEnabled(false)
    setDrawGridBackground(false)

    maxHighlightDistance = 300f

    animateY(1400, Easing.EasingOption.EaseInOutQuad)

    xAxis.apply {
        isEnabled = true
        axisLineColor = ContextCompat.getColor(context!!, R.color.colorAccent)
        labelCount = 6
        granularity = 1f
        position = XAxis.XAxisPosition.BOTTOM
        setDrawGridLines(false)
        valueFormatter = ActivityXAxisValueFormatter()
        textColor = ContextCompat.getColor(context!!, R.color.colorAccent)
        setAvoidFirstLastClipping(true)
    }

    axisLeft.apply {
        isEnabled = true
        axisLineColor = ContextCompat.getColor(context!!, R.color.colorAccent)
        labelCount = 3
        gridColor = ContextCompat.getColor(context!!, R.color.colorAccentAlpha26)
        textColor = ContextCompat.getColor(context!!, R.color.colorAccent)
    }

    axisRight.apply {
        isEnabled = false
    }

    legend.apply {
        isEnabled = false
    }

}

fun LineChart.setData(activity: Map<String, SharingActivity>, average: Float) {

    val colors = listOf(ContextCompat.getColor(context!!, R.color.colorAccent))
    val dataSets = arrayListOf<ILineDataSet>()

    val activityEntries = arrayListOf<Entry>()
    val avgEntries = arrayListOf<Entry>()

    val startMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    val startYear = Calendar.getInstance().get(Calendar.YEAR)

    for (i in 5 downTo 0) {

        var month = startMonth - i
        var year = startYear

        if (month < 1) {
            month += 12
            year--
        }

        activity["$year-$month"]?.let {

            val activityEntry = Entry(5 - i.toFloat(), it.distance.toFloat().div(1000f))
            val avgEntry = Entry(5 - i.toFloat(), it.distanceTrend.div(1000f))

            activityEntries.add(activityEntry)
            avgEntries.add(avgEntry)

        }


    }

    // Media
    Collections.sort(avgEntries, EntryXComparator())

    val avgDataSet = LineDataSet(avgEntries, "Media").apply {
        color = ContextCompat.getColor(context!!, R.color.colorPrimary)
        mode = LineDataSet.Mode.CUBIC_BEZIER

        setDrawFilled(true)
        fillDrawable = ContextCompat.getDrawable(context!!, R.drawable.bg_chart_activity_avg_fill)
        fillAlpha = 100
        lineWidth = 3f

        setDrawCircles(false)
        setDrawValues(false)

        setDrawHorizontalHighlightIndicator(false)
        setDrawVerticalHighlightIndicator(false)

        enableDashedLine(20f, 15f, 0f)
    }

    dataSets.add(avgDataSet)

    // Attività
    Collections.sort(activityEntries, EntryXComparator())

    val activityDataSet = LineDataSet(activityEntries, "Attività").apply {
        color = ContextCompat.getColor(context!!, R.color.colorAccent)
        mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        setDrawFilled(true)

        fillDrawable = ContextCompat.getDrawable(context!!, R.drawable.bg_chart_activity_fill)
        fillAlpha = 100
        lineWidth = 3f

        setDrawCircles(true)
        setDrawCircleHole(true)
        setDrawValues(false)
        circleRadius = 6f
        circleHoleRadius = 4f
        setCircleColorHole(ContextCompat.getColor(context!!, R.color.cardview_light_background))

        valueTextColor = ContextCompat.getColor(context!!, R.color.light_bg_dark_primary_text)
        valueTextSize = 12f

        setDrawHorizontalHighlightIndicator(false)
        setDrawVerticalHighlightIndicator(false)

        circleColors = colors
    }


    dataSets.add(activityDataSet)

    data = LineData(dataSets)
    invalidate()

}

fun PieChart.setUp() {
    description.isEnabled = false
    isRotationEnabled = false
    setEntryLabelColor(ContextCompat.getColor(context!!, R.color.dark_bg_light_primary_text))
    setEntryLabelTypeface(Typeface.DEFAULT_BOLD)
    maxAngle = 180f
    rotationAngle = 180f
    setCenterTextOffset(0f, -22f)
    animateY(1400, Easing.EasingOption.EaseInOutQuad)
    setTransparentCircleAlpha(110)
    holeRadius = 58f
    transparentCircleRadius = 61f
    legend.isEnabled = false
}

fun PieChart.setData(stats: Statistics) {

    val s = SpannableString("${stats.totalShares}\ncondivisioni effettuate").apply {
        setSpan(RelativeSizeSpan(2.1f), 0, "${stats.totalShares}".length, 0)
        setSpan(StyleSpan(Typeface.BOLD), 0, "${stats.totalShares}".length, 0)
        setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.light_bg_dark_primary_text)), 0, "${stats.totalShares}".length, 0)
    }
    centerText = s

    val entriesPie = ArrayList<PieEntry>().apply {
        if (stats.totalHostShares > 0) add(PieEntry(stats.totalHostShares.toFloat(), "Driver"))
        if (stats.totalGuestShares > 0) add(PieEntry(stats.totalGuestShares.toFloat(), "Guest"))
    }

    val pieDataSet = PieDataSet(entriesPie, null).apply {
        sliceSpace = 2f
        setDrawValues(false)
    }

    val colors = ArrayList<Int>().apply {
        add(ContextCompat.getColor(context!!, R.color.colorPrimary))
        add(ContextCompat.getColor(context!!, R.color.colorAccent))
    }

    pieDataSet.colors = colors

    val pieData = PieData(pieDataSet).apply {
        setValueTextColor(ContextCompat.getColor(context!!, R.color.dark_bg_light_primary_text))
        setValueFormatter(PercentFormatter())
        setValueTextSize(16f)
    }


    data = pieData
    invalidate()

}

class ActivityXAxisValueFormatter : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {

        val cal = Calendar.getInstance().apply {
            val thisMonth = get(Calendar.MONTH)
            var month = thisMonth - (5 - value.toInt())
            if (month < 0) month += 12
            set(Calendar.MONTH, month)
        }

        return SimpleDateFormat("MMM", Locale.ITALY).format(cal.time).toUpperCase()
    }
}