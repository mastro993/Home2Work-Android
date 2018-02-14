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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.directions.route.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.user.Const.EXTRA_MATCH
import it.gruppoinfor.home2work.user.Const.EXTRA_USER
import it.gruppoinfor.home2work.user.Const.GOOGLE_API_KEY
import it.gruppoinfor.home2work.utils.DateFormatUtils.dateToString
import it.gruppoinfor.home2work.utils.RouteUtils
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Match
import kotlinx.android.synthetic.main.activity_match.*

class MatchActivity : AppCompatActivity() {
    private lateinit var googleMap: GoogleMap
    private lateinit var match: Match

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.onCreate(savedInstanceState)

        match_loading_view.visibility = View.VISIBLE

        map.getMapAsync(MyMapReadyCallback(this))

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


    private fun initUI() {

        profile_container.setOnClickListener {
            val userIntent = Intent(this, ShowUserActivity::class.java)
            userIntent.putExtra(EXTRA_USER, match.host)
            startActivity(userIntent)
        }

        val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder)

        Glide.with(this)
                .load(match.host.avatarURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(user_avatar)

        name_view.text = match.host.toString()
        job_view.text = match.host.company.toString()
        home_view.text = match.host.address.city

        timetable_time.text = String.format(
                resources.getString(R.string.activity_match_time),
                dateToString(match.startTime),
                dateToString(match.endTime)
        )

        val days = ArrayList<String>()
        match.weekdays.mapTo(days) { resources.getStringArray(R.array.giorni)[it] }
        timetable_weekdays.text = TextUtils.join(", ", days)

    }

    private fun getScoreColor(score: Int): Int {
        return when {
            score < 60 -> ContextCompat.getColor(this, R.color.red_500)
            score < 70 -> ContextCompat.getColor(this, R.color.orange_600)
            score < 80 -> ContextCompat.getColor(this, R.color.amber_400)
            score < 90 -> ContextCompat.getColor(this, R.color.light_green_500)
            else -> ContextCompat.getColor(this, R.color.green_500)
        }
    }

    private fun refreshUI() {
        match_loading_view.visibility = View.GONE
        val animator = ValueAnimator.ofInt(0, match.score)
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { valueAnimator -> score_text.text = "${valueAnimator.animatedValue}" }
        animator.start()

        val color = getScoreColor(match.score)
        score_text.setTextColor(color)
    }

    private inner class MyMapReadyCallback internal constructor(private val mContext: Context) : OnMapReadyCallback, RoutingListener {

        override fun onMapReady(gmap: GoogleMap) {
            googleMap = gmap
            googleMap.uiSettings.isMyLocationButtonEnabled = false

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                MapsInitializer.initialize(mContext)

                val matchWaypoints = ArrayList<com.google.android.gms.maps.model.LatLng>()
                matchWaypoints.add(com.google.android.gms.maps.model.LatLng(match.startLocation.lat, match.startLocation.lng))
                matchWaypoints.add(com.google.android.gms.maps.model.LatLng(match.endLocation.lat, match.endLocation.lng))

                val matchRouting = Routing.Builder()
                        .travelMode(AbstractRouting.TravelMode.WALKING)
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

            googleMap.addPolyline(polyOptions)

            val latLngBounds = RouteUtils.getRouteBounds(route.points)

            googleMap.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(com.google.android.gms.maps.model.LatLng(match.startLocation.lat, match.startLocation.lng))
                    .title("Casa")
            )

            googleMap.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(com.google.android.gms.maps.model.LatLng(match.endLocation.lat, match.endLocation.lng))
                    .title("Lavoro")
            )

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100))

            refreshUI()
        }

        override fun onRoutingCancelled() {

        }
    }


}
