package it.gruppoinfor.home2work

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import it.gruppoinfor.home2work.common.events.ActiveShareEvent
import it.gruppoinfor.home2work.common.events.BottomNavBadgeEvent
import it.gruppoinfor.home2work.common.extensions.remove
import it.gruppoinfor.home2work.common.extensions.show
import it.gruppoinfor.home2work.common.extensions.showToast
import it.gruppoinfor.home2work.common.services.LocationService
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.home.HomeFragment
import it.gruppoinfor.home2work.match.MatchesFragment
import it.gruppoinfor.home2work.profile.ProfileFragment
import it.gruppoinfor.home2work.ranks.RanksFragment
import it.gruppoinfor.home2work.sharecurrent.CurrentShareActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_REQUEST_CODE = 1

    @Inject
    lateinit var factory: MainVMFactory
    @Inject
    lateinit var localUserData: LocalUserData

    private lateinit var viewModel: MainViewModel
    private lateinit var bottomNavigation: AHBottomNavigation
    private lateinit var viewPager: ViewPager
    private lateinit var shareButton: ImageButton
    private lateinit var newShareProgress: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DipendencyInjector.createMainComponent().inject(this)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        bottomNavigation = bn_main
        viewPager = vp_main
        shareButton = bt_new_share
        newShareProgress = pb_current_share

        initUI()
        observeViewState()

        // TODO avvio servizio tracking
        val locationIntent = Intent(this, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(locationIntent)
        } else {
            startService(locationIntent)
        }

    }

    override fun onResume() {
        super.onResume()

        viewModel.getCurrentShare()

        EventBus.getDefault().register(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }

        data?.let {
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

    override fun onDestroy() {
        super.onDestroy()
        DipendencyInjector.releaseMainComponent()
    }

    private fun initUI() {

        val homeTab = AHBottomNavigationItem(R.string.activity_main_tab_home, R.drawable.ic_navigation_home, R.color.colorPrimaryDark)
        val ranksTab = AHBottomNavigationItem(R.string.activity_main_tab_ranks, R.drawable.ic_navigation_ranks, R.color.colorPrimaryDark)
        val newShareButton = AHBottomNavigationItem(R.string.empty, R.drawable.ic_empty, R.color.teal_200) // Placeholder
        val matchTab = AHBottomNavigationItem(R.string.activity_main_tab_matches, R.drawable.ic_navigation_match, R.color.light_green_400)
        val profileTab = AHBottomNavigationItem(R.string.activity_main_tab_profile, R.drawable.ic_navigation_account, R.color.colorAccent)

        bottomNavigation.removeAllItems()

        bottomNavigation.addItem(homeTab)
        bottomNavigation.addItem(ranksTab)
        bottomNavigation.addItem(newShareButton)
        bottomNavigation.addItem(matchTab)
        bottomNavigation.addItem(profileTab)

        bottomNavigation.accentColor = ContextCompat.getColor(this, R.color.bottom_nav_active)
        bottomNavigation.inactiveColor = ContextCompat.getColor(this, R.color.bottom_nav_inactive)
        bottomNavigation.isForceTint = true

        bottomNavigation.setNotificationBackgroundColor(ContextCompat.getColor(this, R.color.red_500))

        bottomNavigation.isBehaviorTranslationEnabled = false
        bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_HIDE

        bottomNavigation.setOnTabSelectedListener { position, _ ->
            when (position) {
                NEW_SHARE_BUTTON_PLACEHOLDER -> {
                    shareButton.performClick()
                    false
                }
                else -> {
                    viewPager.setCurrentItem(position, false)
                    title = bottomNavigation.getItem(position).getTitle(this)
                    true
                }
            }
        }

        shareButton.setOnClickListener {

            if (localUserData.currentShare == null) {

                val dialog = BottomSheetDialog(this)
                val sheetView = layoutInflater.inflate(R.layout.dialog_new_share, null)

                dialog.setContentView(sheetView)
                dialog.show()

                sheetView.find<TextView>(R.id.new_share_dialog_create_new).setOnClickListener {
                    dialog.dismiss()
                    viewModel.createShare()
                }
                sheetView.find<TextView>(R.id.new_share_dialog_join).setOnClickListener {
                    dialog.dismiss()
                    joinShare()
                }
            } else {
                startActivity(intentFor<CurrentShareActivity>())
            }

        }

        viewPager.offscreenPageLimit = 4
        val pagerAdapter = PagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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

        bottomNavigation.currentItem = HOME_TAB
        title = bottomNavigation.getItem(HOME_TAB).getTitle(this)

    }

    fun setNavigationBadge(itemPosition: Int, title: String) {

        bottomNavigation.setNotification(title, itemPosition)

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
            startActivity(intentFor<CurrentShareActivity>())
        })
    }

    private fun handleViewState(state: MainViewState?) {

        state?.let {


            if (it.creatingShare || it.joiningShare) {
                newShareProgress.show()
            } else {
                newShareProgress.remove()
            }

            if (it.shareInProgress) {
                shareButton.setBackgroundResource(R.drawable.bg_new_share_button_active)
                shareButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_new_share_button_icon_active))
            } else {
                shareButton.setBackgroundResource(R.drawable.bg_new_share_button_inactive)
                shareButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_new_share_button_icon_inactive))
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

        bottomNavigation.setNotification(event.badgeContent, event.tabPosition)

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
