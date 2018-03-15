package it.gruppoinfor.home2work

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.pixplicity.easyprefs.library.Prefs
import it.gruppoinfor.home2work.firebase.NewMessageEvent
import it.gruppoinfor.home2work.firebase.OngoingShareEvent
import it.gruppoinfor.home2work.home.HomeFragment
import it.gruppoinfor.home2work.matches.MatchesFragment
import it.gruppoinfor.home2work.ranks.RanksFragment
import it.gruppoinfor.home2work.settings.SettingsActivity
import it.gruppoinfor.home2work.shares.OngoingShareActivity
import it.gruppoinfor.home2work.tracking.LocationService
import it.gruppoinfor.home2work.user.ProfileFragment
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity(), MainView {

    private val mMainPresenter: MainPresenter = MainPresenterImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO avvio servizio tracking
        if (Prefs.getBoolean(SettingsActivity.PREFS_ACTIVITY_TRACKING, true)) {
            val locationIntent = Intent(this, LocationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(locationIntent)
            } else {
                startService(locationIntent)
            }
        }

        initUI()

    }

    override fun onResume() {
        super.onResume()

        mMainPresenter.onResume()

        EventBus.getDefault().register(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }

        if (data != null) {
            val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (scanResult is IntentResult) {

                val stringData = scanResult.contents.split(",")
                val shareId = java.lang.Long.parseLong(stringData[0])
                val latLng = LatLng(stringData[1].toDouble(), stringData[2].toDouble())
                checkShareCode(shareId, latLng)

            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == Constants.REQ_CAMERA)
            joinShare()

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onShareCreated() {
        Answers.getInstance().logCustom(CustomEvent("Condivisione creata"))
        startActivity(intentFor<OngoingShareActivity>())
    }

    override fun onShareJoined() {
        Answers.getInstance().logCustom(CustomEvent("Unione a condivisione"))
        startActivity(intentFor<OngoingShareActivity>())
    }

    override fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onOngoingShareRefresh() {
        if (HomeToWorkClient.ongoingShare != null) {
            ongoing_share_progress_bar.visibility = View.VISIBLE
            button_new_share.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_car))
        } else {
            ongoing_share_progress_bar.visibility = View.GONE
            button_new_share.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_car))
        }
    }

    private fun initUI() {

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        val homeTab = AHBottomNavigationItem(R.string.activity_main_tab_home, R.drawable.ic_navigation_home, R.color.colorPrimaryDark)
        val ranksTab = AHBottomNavigationItem(R.string.activity_main_tab_ranks, R.drawable.ic_navigation_ranks, R.color.colorPrimaryDark)
        val newShareButton = AHBottomNavigationItem(R.string.empty, R.drawable.ic_empty, R.color.teal_200) // Placeholder
        val matchTab = AHBottomNavigationItem(R.string.activity_main_tab_matches, R.drawable.ic_navigation_match, R.color.light_green_400)
        val profileTab = AHBottomNavigationItem(R.string.activity_main_tab_profile, R.drawable.ic_navigation_account, R.color.colorAccent)

        bottom_navigation.removeAllItems()

        bottom_navigation.addItem(homeTab)
        bottom_navigation.addItem(ranksTab)
        bottom_navigation.addItem(newShareButton)
        bottom_navigation.addItem(matchTab)
        bottom_navigation.addItem(profileTab)

        bottom_navigation.accentColor = ContextCompat.getColor(this, R.color.colorAccent)
        bottom_navigation.inactiveColor = ContextCompat.getColor(this, R.color.light_bg_dark_disabled_text)
        bottom_navigation.isForceTint = true

        bottom_navigation.setNotificationBackgroundColor(ContextCompat.getColor(this, R.color.red_500))

        bottom_navigation.isBehaviorTranslationEnabled = false
        bottom_navigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW

        bottom_navigation.setOnTabSelectedListener { position, _ ->
            when (position) {
                NEW_SHARE_BUTTON_PLACEHOLDER -> {
                    false
                }
                else -> {
                    view_pager.setCurrentItem(position, false)
                    title = bottom_navigation.getItem(position).getTitle(this)
                    true
                }
            }
        }

        button_new_share.setOnClickListener {

            if (HomeToWorkClient.ongoingShare == null) {

                val dialog = BottomSheetDialog(this)
                val sheetView = layoutInflater.inflate(R.layout.dialog_new_share, null)

                dialog.setContentView(sheetView)
                dialog.show()

                sheetView.find<TextView>(R.id.new_share_dialog_create_new).setOnClickListener {
                    dialog.dismiss()
                    mMainPresenter.newShare()
                }
                sheetView.find<TextView>(R.id.new_share_dialog_join).setOnClickListener {
                    dialog.dismiss()
                    joinShare()
                }
            } else {
                startActivity(intentFor<OngoingShareActivity>())
            }

        }

        view_pager.offscreenPageLimit = 4
        val pagerAdapter = PagerAdapter(supportFragmentManager)
        view_pager.adapter = pagerAdapter
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

                if (position == PROFILE_TAB)
                    supportActionBar?.hide()
                else
                    supportActionBar?.show()

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        bottom_navigation.currentItem = HOME_TAB
        title = bottom_navigation.getItem(HOME_TAB).getTitle(this)

    }

    fun setNavigationBadge(itemPosition: Int, title: String) {

        bottom_navigation.setNotification(title, itemPosition)

    }

    private fun joinShare() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), Constants.REQ_CAMERA)

        } else {

            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            intentIntegrator.setOrientationLocked(false)
            intentIntegrator.initiateScan()

        }

    }

    private fun checkShareCode(shareId: Long, hostLatLng: LatLng) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.create()
            locationRequest.numUpdates = 1
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            mFusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val joinLocation = locationResult.lastLocation
                    mMainPresenter.joinShare(shareId, hostLatLng, joinLocation)
                    mFusedLocationClient.removeLocationUpdates(this)
                }
            }, Looper.myLooper())

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onMessageEvent(event: OngoingShareEvent) {

        mMainPresenter.getOngoingShare()

    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
        mMainPresenter.onPause()
    }

    private inner class PagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            return when (position) {
                HOME_TAB -> HomeFragment()
                RANKS_TAB -> RanksFragment()
                MATCHES_TAB -> MatchesFragment()
                PROFILE_TAB -> ProfileFragment()
                else -> HomeFragment()
            }
        }

        override fun getCount(): Int {

            return 5
        }

    }

    companion object {
        const val HOME_TAB = 0
        const val RANKS_TAB = 1
        const val NEW_SHARE_BUTTON_PLACEHOLDER = 2
        const val MATCHES_TAB = 3
        const val PROFILE_TAB = 4
    }


}
