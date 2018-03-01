package it.gruppoinfor.home2work.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
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
import android.widget.Toast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.custom.AppBarStateChangeListener
import it.gruppoinfor.home2work.fragments.ProfileFragment
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Experience
import it.gruppoinfor.home2workapi.model.User
import it.gruppoinfor.home2workapi.model.UserProfile
import kotlinx.android.synthetic.main.activity_show_user.*
import kotlinx.android.synthetic.main.layout_profile_activity.*
import kotlinx.android.synthetic.main.layout_profile_header_user.*
import kotlinx.android.synthetic.main.layout_profile_shares.*
import java.text.SimpleDateFormat
import java.util.*

class ShowUserActivity : AppCompatActivity() {

    private var mExpOld: Experience = Experience()
    private lateinit var mUser: User
    private var mProfile: UserProfile = UserProfile()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_show_user)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (intent.hasExtra(Const.EXTRA_USER)) {

            mUser = intent.getSerializableExtra(Const.EXTRA_USER) as User
            initUI()
            refreshProfile()

        } else {

            Toast.makeText(this, R.string.activity_show_user_error, Toast.LENGTH_SHORT).show()
            finish()

        }

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

        loading_view.visibility = View.VISIBLE

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

        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = true
            refreshProfile()
        }
        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)

        initHeaderUI()
        initSharesUI()
        initActivityUI()
        initFooterUI()

        button_send_message.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(Const.EXTRA_NEW_CHAT, mUser)
            startActivity(intent)
        }

    }

    private fun initHeaderUI() {

        avatar_view.setAvatarURL(mUser.avatarURL)
        avatar_view_small.setAvatarURL(mUser.avatarURL)

        name_text_view.text = mUser.toString()
        text_name_small.text = mUser.toString()

        job_text_view.text = mUser.company.toString()

    }

    private fun initSharesUI() {

        // TODO rimuovere valore se 0 %

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
        chart_activity.xAxis.axisLineColor = ContextCompat.getColor(this, R.color.colorAccent)
        chart_activity.xAxis.labelCount = 6
        chart_activity.xAxis.granularity = 1f
        chart_activity.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart_activity.xAxis.setDrawGridLines(false)
        chart_activity.xAxis.valueFormatter = ProfileFragment.ActivityXAxisValueFormatter()
        chart_activity.xAxis.textColor = ContextCompat.getColor(this, R.color.colorAccent)
        chart_activity.xAxis.setAvoidFirstLastClipping(true)

        chart_activity.axisLeft.isEnabled = false
        chart_activity.axisRight.isEnabled = false

        chart_activity.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                // TODO dialog con informazioni
            }

            override fun onNothingSelected() {
                // ...
            }
        })

        val legend = chart_activity.legend
        legend.isEnabled = false

    }

    private fun initFooterUI() {

        val simpleDate = SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN)
        val strDt = simpleDate.format(mUser.regdate)
        text_regdate.text = strDt

    }

    private fun refreshHeader() {

        avatar_view.setLevel(mProfile.exp.level)
        avatar_view_small.setLevel(mProfile.exp.level)

    }

    private fun refreshShares() {

        text_month_shares.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month), SimpleDateFormat("MMMM", Locale.ITALY).format(Date()).capitalize())
        text_month_shares_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_value), mProfile.stats.monthShares)
        text_month_shares_avg_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_avg_value), mProfile.stats.monthSharesAvg)

        val s = SpannableString("${mProfile.stats.shares}\ncondivisioni effettuate")
        s.setSpan(RelativeSizeSpan(2.1f), 0, "${mProfile.stats.shares}".length, 0)
        s.setSpan(StyleSpan(Typeface.BOLD), 0, "${mProfile.stats.shares}".length, 0)
        s.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.light_bg_dark_primary_text)), 0, "${mProfile.stats.shares}".length, 0)
        chart_shares.centerText = s

        val entriesPie = ArrayList<PieEntry>()
        entriesPie.add(PieEntry(mProfile.stats.hostShares.toFloat(), "Driver"))
        entriesPie.add(PieEntry(mProfile.stats.guestShares.toFloat(), "Guest"))

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

    }

    private fun refreshActivity() {

        val stats = mProfile.stats
        text_shared_distance_value.text = String.format(Locale.ITALY, "%.2f km", stats.sharedDistance / 1000f)
        // TODO mese più attivo
        text_month_shared_distance_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), stats.monthSharedDistance / 1000f)
        text_month_shared_distance_avg_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), stats.monthSharedDistanceAvg / 1000f)

        val colors = ArrayList<Int>()
        colors.add(ContextCompat.getColor(this, R.color.colorAccent))

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
        dataSet.color = ContextCompat.getColor(this, R.color.colorAccent)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawFilled(true)
        dataSet.fillDrawable = ContextCompat.getDrawable(this, R.drawable.drawable_activity_chart_fill)
        dataSet.fillAlpha = 100
        dataSet.lineWidth = 3f
        dataSet.setDrawCircles(true)
        dataSet.setDrawCircleHole(true)
        dataSet.circleRadius = 6f
        dataSet.circleHoleRadius = 4f
        dataSet.setCircleColorHole(ContextCompat.getColor(this, R.color.cardview_light_background))
        dataSet.setDrawValues(false)
        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.light_bg_dark_primary_text)
        dataSet.valueTextSize = 12f
        dataSet.setDrawHorizontalHighlightIndicator(false)
        dataSet.setDrawVerticalHighlightIndicator(false)
        dataSet.circleColors = colors

        val lineData = LineData(dataSet)
        chart_activity.data = lineData
        chart_activity.invalidate()

    }

    private fun refreshProfile() {

        HomeToWorkClient.getUserProfileById(mUser.id, OnSuccessListener { userProfile ->

            mProfile = userProfile

            refreshHeader()
            refreshShares()
            refreshActivity()

            swipe_refresh_layout.isRefreshing = false
            loading_view.visibility = View.GONE

        }, OnFailureListener {

            Toast.makeText(this, "Impossibile ottenere informazioni dell'utente al momento", Toast.LENGTH_SHORT).show()

            swipe_refresh_layout.isRefreshing = false

        })

    }

}
