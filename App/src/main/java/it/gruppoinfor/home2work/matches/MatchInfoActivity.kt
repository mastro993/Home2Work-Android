package it.gruppoinfor.home2work.matches

import android.Manifest
import android.animation.ValueAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.transition.Explode
import android.transition.Fade
import android.transition.Transition
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.directions.route.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import it.gruppoinfor.home2work.Constants.GOOGLE_API_KEY
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.user.UserActivityArgs
import it.gruppoinfor.home2work.utils.DateFormatUtils.dateToString
import it.gruppoinfor.home2work.utils.RouteUtils
import kotlinx.android.synthetic.main.activity_match.*

class MatchInfoActivity : AppCompatActivity() {

    private lateinit var googleMap: GoogleMap

    private val args by lazy {
        MatchInfoActivityArgs.deserializeFrom(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        //map.onCreate(savedInstanceState)

        status_view.loading()

        //map.getMapAsync(MyMapReadyCallback(this))


        //initUI()

        window.enterTransition = Fade()

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

        val match = args.match

        profile_container.setOnClickListener {

            val user = match.host!!

            UserActivityArgs(
                    userId = user.id,
                    userName = user.toString(),
                    userAvatarUrl = user.avatarURL,
                    userCompanyId = user.company.id,
                    userCompanyName = user.company.name
            ).launch(this)


        }

        val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder)

        Glide.with(this)
                .load(match.host?.avatarURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(user_avatar)

        name_view.text = match.host.toString()
        job_view.text = match.host?.company.toString()
        home_view.text = match.host?.address?.city

        /*timetable_time.text = String.format(
                resources.getString(R.string.activity_match_time),
                dateToString(match.startTime),
                dateToString(match.endTime)
        )

        val days = ArrayList<String>()
        match.weekdays.mapTo(days) { resources.getStringArray(R.array.giorni)[it] }
        timetable_weekdays.text = TextUtils.join(", ", days)*/

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

        val match = args.match

        status_view.done()
        val animator = ValueAnimator.ofInt(0, match.getScore())
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { valueAnimator -> score_text.text = "${valueAnimator.animatedValue}" }
        animator.start()

        val color = getScoreColor(match.getScore())
        score_text.setTextColor(color)

    }

    private inner class MyMapReadyCallback internal constructor(private val mContext: Context) : OnMapReadyCallback, RoutingListener {

        override fun onMapReady(gmap: GoogleMap) {

            val match = args.match

            googleMap = gmap
            googleMap.uiSettings.isMyLocationButtonEnabled = false

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                MapsInitializer.initialize(mContext)

                val matchWaypoints = ArrayList<com.google.android.gms.maps.model.LatLng>()
               /* matchWaypoints.add(com.google.android.gms.maps.model.LatLng(match.startLocation.lat, match.startLocation.lng))
                matchWaypoints.add(com.google.android.gms.maps.model.LatLng(match.endLocation.lat, match.endLocation.lng))*/

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

            Toast.makeText(mContext, R.string.activity_match_error, Toast.LENGTH_SHORT).show()
            finish()

        }

        override fun onRoutingStart() {

        }

        override fun onRoutingSuccess(arrayList: ArrayList<Route>, i: Int) {

            val match = args.match

            val route = arrayList[0]

            val polyOptions = PolylineOptions()
            polyOptions.width((12 + i * 3).toFloat())
            polyOptions.color(ContextCompat.getColor(mContext, R.color.red_500))
            polyOptions.addAll(route.points)

            googleMap.addPolyline(polyOptions)

            val latLngBounds = RouteUtils.getRouteBounds(route.points)

            /*googleMap.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(com.google.android.gms.maps.model.LatLng(match.startLocation.lat, match.startLocation.lng))
                    .title("Casa")
            )

            googleMap.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(com.google.android.gms.maps.model.LatLng(match.endLocation.lat, match.endLocation.lng))
                    .title("Lavoro")
            )*/

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100))

            refreshUI()

        }

        override fun onRoutingCancelled() {

        }
    }


}
