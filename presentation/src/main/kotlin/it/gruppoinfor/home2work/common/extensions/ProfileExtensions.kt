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
    val anim = ProgressBarAnimation(this, this.progress.toFloat(), value)
    anim.duration = 500
    startAnimation(anim)
}

fun LineChart.setUp() {

    setViewPortOffsets(32f, 0f, 32f, 0f)

    description.isEnabled = false
    isDragEnabled = false

    setPinchZoom(false)
    setScaleEnabled(false)
    setDrawGridBackground(false)

    maxHighlightDistance = 300f

    animateY(1400, Easing.EasingOption.EaseInOutQuad)

    xAxis.isEnabled = true
    xAxis.axisLineColor = ContextCompat.getColor(context!!, R.color.colorAccent)
    xAxis.labelCount = 6
    xAxis.granularity = 1f
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.setDrawGridLines(false)
    xAxis.valueFormatter = ActivityXAxisValueFormatter()
    xAxis.textColor = ContextCompat.getColor(context!!, R.color.colorAccent)
    xAxis.setAvoidFirstLastClipping(true)

    axisLeft.isEnabled = true
    axisLeft.axisLineColor = ContextCompat.getColor(context!!, R.color.colorAccent)
    axisLeft.labelCount = 3
    axisLeft.gridColor = ContextCompat.getColor(context!!, R.color.colorAccentAlpha26)
    axisLeft.textColor = ContextCompat.getColor(context!!, R.color.colorAccent)

    axisRight.isEnabled = false

    legend.isEnabled = false

}

fun LineChart.setData(activity: Map<Int, SharingActivity>, average: Float) {

    val colors = listOf(ContextCompat.getColor(context!!, R.color.colorAccent))
    val dataSets = arrayListOf<ILineDataSet>()
    val activityEntries = arrayListOf<Entry>()
    val avgEntries = arrayListOf<Entry>()

    val thisMonth = Calendar.getInstance().get(Calendar.MONTH)

    for (i in 5 downTo 0) {

        var month = thisMonth - i
        if (month < 0) month += 12


        val activityEntry = Entry(5 - i.toFloat(), activity[month + 1]?.distance?.toFloat()?.div(1000f)
                ?: 0f)
        val avgEntry = Entry(5 - i.toFloat(), average)

        activityEntries.add(activityEntry)
        avgEntries.add(avgEntry)

    }

    // Media
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

    // Attività
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
    val s = SpannableString("${stats.totalShares}\ncondivisioni effettuate")
    s.setSpan(RelativeSizeSpan(2.1f), 0, "${stats.totalShares}".length, 0)
    s.setSpan(StyleSpan(Typeface.BOLD), 0, "${stats.totalShares}".length, 0)
    s.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.light_bg_dark_primary_text)), 0, "${stats.totalShares}".length, 0)
    centerText = s

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

    data = pieData
    invalidate()

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