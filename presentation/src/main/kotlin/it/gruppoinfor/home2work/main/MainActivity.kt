package it.gruppoinfor.home2work.main

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.widget.TextView
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseActivity
import it.gruppoinfor.home2work.common.events.ActiveShareEvent
import it.gruppoinfor.home2work.common.events.BottomNavBadgeEvent
import it.gruppoinfor.home2work.common.extensions.*
import it.gruppoinfor.home2work.common.user.SettingsPreferences
import it.gruppoinfor.home2work.entities.Share
import it.gruppoinfor.home2work.home.HomeFragment
import it.gruppoinfor.home2work.match.MatchesFragment
import it.gruppoinfor.home2work.profile.ProfileFragment
import it.gruppoinfor.home2work.ranks.RanksFragment
import it.gruppoinfor.home2work.sharecurrent.CurrentShareActivity
import it.gruppoinfor.home2work.sharecurrent.ShareCompleteDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_vacancy_mode_banner.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.find
import javax.inject.Inject

class MainActivity : BaseActivity<MainViewModel, MainVMFactory>() {


    private val CAMERA_PERMISSION_REQUEST_CODE = 1

    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    override fun getVMClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        initUI()
        observeViewState()

    }

    override fun onResume() {
        super.onResume()

        vacancy_mode_banner.apply {
            if (settingsPreferences.vacancyModeEnabled) {
                show()
                button_disable_vacancy_mode.setOnClickListener {
                    settingsPreferences.vacancyModeEnabled = false
                    hide()
                }
            } else {
                remove()
            }
        }


        viewModel.getCurrentShare()

        EventBus.getDefault().register(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        data?.let {
            if (requestCode == CurrentShareActivity.REQ_CODE) {

                if (it.hasExtra(CurrentShareActivity.EXTRA_SHARE)) {
                    val share = it.getSerializableExtra(CurrentShareActivity.EXTRA_SHARE) as Share
                    val dialog = ShareCompleteDialog(this, share)
                    dialog.show()
                }

            } else {
                val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, it)
                if (scanResult is IntentResult) {

                    val stringData = scanResult.contents.split(",")
                    val shareId = java.lang.Long.parseLong(stringData[0])

                    val hostLocation = Location("")
                    hostLocation.latitude = stringData[1].toDouble()
                    hostLocation.longitude = stringData[2].toDouble()

                    checkShareCode(shareId, hostLocation)

                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE)
            joinShare()

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    private fun initUI() {

        val homeTab = AHBottomNavigationItem(R.string.activity_main_tab_home, R.drawable.ic_navigation_home, R.color.colorPrimaryDark)
        val ranksTab = AHBottomNavigationItem(R.string.activity_main_tab_ranks, R.drawable.ic_navigation_ranks, R.color.colorPrimaryDark)
        val newShareButton = AHBottomNavigationItem(R.string.empty, R.drawable.ic_empty, R.color.teal_200) // Placeholder
        val matchTab = AHBottomNavigationItem(R.string.activity_main_tab_matches, R.drawable.ic_navigation_match, R.color.light_green_400)
        val profileTab = AHBottomNavigationItem(R.string.activity_main_tab_profile, R.drawable.ic_navigation_account, R.color.colorAccent)

        bn_main.removeAllItems()

        bn_main.addItem(homeTab)
        bn_main.addItem(ranksTab)
        bn_main.addItem(newShareButton)
        bn_main.addItem(matchTab)
        bn_main.addItem(profileTab)

        bn_main.accentColor = ContextCompat.getColor(this, R.color.bottom_nav_active)
        bn_main.inactiveColor = ContextCompat.getColor(this, R.color.bottom_nav_inactive)
        bn_main.isForceTint = true

        bn_main.setNotificationBackgroundColor(ContextCompat.getColor(this, R.color.red_500))

        bn_main.isBehaviorTranslationEnabled = false
        bn_main.titleState = AHBottomNavigation.TitleState.ALWAYS_HIDE

        bn_main.setOnTabSelectedListener { position, _ ->
            when (position) {
                NEW_SHARE_BUTTON_PLACEHOLDER -> {
                    bt_new_share.performClick()
                    false
                }
                else -> {
                    vp_main.setCurrentItem(position, false)
                    title = bn_main.getItem(position).getTitle(this)
                    true
                }
            }
        }

        bt_new_share.setOnClickListener {

            localUserData.currentShare?.let {
                launchActivity<CurrentShareActivity>(requestCode = CurrentShareActivity.REQ_CODE)
            } ?: let {
                val dialog = BottomSheetDialog(this)
                val sheetView = layoutInflater.inflate(R.layout.dialog_new_share, null)

                dialog.setContentView(sheetView)
                dialog.show()

                sheetView.find<TextView>(R.id.new_share_dialog_create_new).setOnClickListener {
                    dialog.dismiss()
                    createShare()
                }
                sheetView.find<TextView>(R.id.new_share_dialog_join).setOnClickListener {
                    dialog.dismiss()
                    joinShare()
                }
            }


        }

        vp_main.offscreenPageLimit = 4
        val pagerAdapter = PagerAdapter(supportFragmentManager)
        vp_main.adapter = pagerAdapter
        vp_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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

        bn_main.currentItem = HOME_TAB
        title = bn_main.getItem(HOME_TAB).getTitle(this)

    }

    fun setNavigationBadge(itemPosition: Int, title: String) {

        bn_main.setNotification(title, itemPosition)

    }

    private fun observeViewState() {

        viewModel.errorState.observe(this, Observer {
            it?.let {
                showToast(it)
            }
        })
        viewModel.viewState.observe(this, Observer {
            handleViewState(it)
        })
        viewModel.shareEvent.observe(this, Observer {
            launchActivity<CurrentShareActivity>(requestCode = CurrentShareActivity.REQ_CODE)
        })
    }

    private fun handleViewState(state: MainViewState?) {

        state?.let {


            if (it.creatingShare || it.joiningShare) {
                pb_current_share.show()
            } else {
                pb_current_share.remove()
            }

            if (it.shareInProgress) {
                bt_new_share.setBackgroundResource(R.drawable.bg_new_share_button_active)
                bt_new_share.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_new_share_button_icon_active))
            } else {
                bt_new_share.setBackgroundResource(R.drawable.bg_new_share_button_inactive)
                bt_new_share.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_new_share_button_icon_inactive))
            }

        }

    }

    private fun joinShare() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)

        } else {

            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            intentIntegrator.setOrientationLocked(false)
            intentIntegrator.initiateScan()

        }

    }

    private fun createShare() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.create()
            locationRequest.numUpdates = 1
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            mFusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val hostLocation = locationResult.lastLocation
                    viewModel.createShare(hostLocation)
                    mFusedLocationClient.removeLocationUpdates(this)
                }
            }, Looper.myLooper())

        }

    }

    private fun checkShareCode(shareId: Long, hostLocation: Location) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.create()
            locationRequest.numUpdates = 1
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            mFusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val joinLocation = locationResult.lastLocation
                    viewModel.joinShare(shareId, hostLocation, joinLocation)
                    mFusedLocationClient.removeLocationUpdates(this)
                }
            }, Looper.myLooper())

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActiveShareEvent) {
        viewModel.getCurrentShare()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBadgeEvent(event: BottomNavBadgeEvent) {

        bn_main.setNotification(event.badgeContent, event.tabPosition)

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
