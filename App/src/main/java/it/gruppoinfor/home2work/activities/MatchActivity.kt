package it.gruppoinfor.home2work.activities

import android.Manifest
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.directions.route.Route
import com.directions.route.RouteException
import com.directions.route.Routing
import com.directions.route.RoutingListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

import java.util.ArrayList
import java.util.Locale

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.utils.RouteUtils
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.LatLng
import it.gruppoinfor.home2workapi.model.Match

import it.gruppoinfor.home2work.utils.Converters.dateToString

class MatchActivity : AppCompatActivity() {
    @BindView(R.id.score_text)
    internal var scoreText: TextView? = null
    @BindView(R.id.home_view)
    internal var homeView: TextView? = null
    internal var mapFragment: SupportMapFragment
    @BindView(R.id.match_loading_view)
    internal var matchLoadingView: FrameLayout? = null
    @BindView(R.id.user_avatar)
    internal var userAvatar: ImageView? = null
    @BindView(R.id.name_view)
    internal var nameView: TextView? = null
    @BindView(R.id.job_view)
    internal var jobView: TextView? = null
    @BindView(R.id.timetable_time)
    internal var timetableTime: TextView? = null
    @BindView(R.id.timetable_weekdays)
    internal var timetableWeekdays: TextView? = null
    private var googleMap: GoogleMap? = null
    private var match: Match? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)
        ButterKnife.bind(this)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.onCreate(savedInstanceState)

        matchLoadingView!!.visibility = View.VISIBLE

        mapFragment.getMapAsync(MyMapReadyCallback(this))

        val intent = intent
        if (intent.hasExtra(EXTRA_MATCH)) {
            match = intent.getSerializableExtra(EXTRA_MATCH) as Match
            initUI()
        } else {
            Toasty.error(this, getString(R.string.activity_match_error)).show()
            finish()
        }

        HomeToWorkClient.getInstance().editMatch(match)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @OnClick(R.id.profile_container)
    fun onViewClicked() {
        val userIntent = Intent(this, ShowUserActivity::class.java)
        userIntent.putExtra("user", match!!.host)
        startActivity(userIntent)
    }

    private fun initUI() {

        val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder)

        Glide.with(this)
                .load(match!!.host.avatarURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(userAvatar!!)

        nameView!!.text = match!!.host.toString()
        jobView!!.text = match!!.host.company.toString()
        homeView!!.text = match!!.host.address.city

        timetableTime!!.text = String.format(
                resources.getString(R.string.activity_match_time),
                dateToString(match!!.startTime),
                dateToString(match!!.endTime)
        )

        val days = ArrayList<String>()
        for (d in match!!.weekdays)
            days.add(resources.getStringArray(R.array.giorni)[d])
        timetableWeekdays!!.text = TextUtils.join(", ", days)

    }

    private fun getScoreColor(score: Int): Int {
        return if (score < 60) {
            ContextCompat.getColor(this, R.color.red_500)
        } else if (score < 70) {
            ContextCompat.getColor(this, R.color.orange_600)
        } else if (score < 80) {
            ContextCompat.getColor(this, R.color.amber_400)
        } else if (score < 90) {
            ContextCompat.getColor(this, R.color.light_green_500)
        } else {
            ContextCompat.getColor(this, R.color.green_500)
        }
    }

    private fun refreshUI() {
        matchLoadingView!!.visibility = View.GONE
        val animator = ValueAnimator.ofInt(0, match!!.score)
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { valueAnimator -> scoreText!!.text = String.format(Locale.ITALIAN, "%1\$d%%", valueAnimator.animatedValue as Int) }
        animator.start()

        val color = getScoreColor(match!!.score!!)
        scoreText!!.setTextColor(color)
    }

    private inner class MyMapReadyCallback internal constructor(private val mContext: Context) : OnMapReadyCallback, RoutingListener {

        override fun onMapReady(gmap: GoogleMap) {
            googleMap = gmap
            googleMap!!.uiSettings.isMyLocationButtonEnabled = false

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                MapsInitializer.initialize(mContext)

                val matchWaypoints = ArrayList<com.google.android.gms.maps.model.LatLng>()
                matchWaypoints.add(com.google.android.gms.maps.model.LatLng(match!!.startLocation.lat!!, match!!.startLocation.lng!!))
                matchWaypoints.add(com.google.android.gms.maps.model.LatLng(match!!.endLocation.lat!!, match!!.endLocation.lng!!))

                val matchRouting = Routing.Builder()
                        .travelMode(Routing.TravelMode.WALKING)
                        .withListener(this)
                        .waypoints(matchWaypoints)
                        .key(GOOGLE_API_KEY)
                        .build()

                matchRouting.execute()

            }

        }

        override fun onRoutingFailure(e: RouteException) {
            Toasty.error(mContext, mContext.getString(R.string.activity_match_error)).show()
            finish()
        }

        override fun onRoutingStart() {

        }

        override fun onRoutingSuccess(arrayList: ArrayList<Route>, i: Int) {
            val route = arrayList[0]

            val polyOptions = PolylineOptions()
            polyOptions.width((12 + i * 3).toFloat())
            polyOptions.color(ContextCompat.getColor(mContext, R.color.red_500))
            polyOptions.addAll(route.points)

            googleMap!!.addPolyline(polyOptions)

            val latLngBounds = RouteUtils.getRouteBounds(route.points)

            val first = match!!.startLocation
            val last = match!!.endLocation

            googleMap!!.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(com.google.android.gms.maps.model.LatLng(first.lat!!, first.lng!!))
                    .title("Casa")
            )


            googleMap!!.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(com.google.android.gms.maps.model.LatLng(last.lat!!, last.lng!!))
                    .title("Lavoro")
            )

            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100))

            refreshUI()
        }

        override fun onRoutingCancelled() {

        }
    }

    companion object {

        val EXTRA_MATCH = "match"
        private val GOOGLE_API_KEY = "AIzaSyCh8NUxxBR-ayyEq_EGFUU1JFVVFVwUq-I"
    }

}
