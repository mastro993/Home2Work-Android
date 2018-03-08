package it.gruppoinfor.home2work

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.pixplicity.easyprefs.library.Prefs
import it.gruppoinfor.home2work.user.SettingsActivity
import it.gruppoinfor.home2work.home.HomeFragment
import it.gruppoinfor.home2work.matches.MatchFragment
import it.gruppoinfor.home2work.user.UserProfileFragment
import it.gruppoinfor.home2work.ranks.RanksFragment
import it.gruppoinfor.home2work.tracking.LocationService
import it.gruppoinfor.home2work.firebase.MessagingService
import it.gruppoinfor.home2work.tracking.SyncService
import it.gruppoinfor.home2work.share.OngoingShareActivity
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.common.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity() {

    private val mMessageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            refreshOngoingShareView()

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Prefs.getBoolean(SettingsActivity.PREFS_ACTIVITY_TRACKING, true)) {
            val locationIntent = Intent(this, LocationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(locationIntent)
            } else {
                startService(locationIntent)
            }
        }

        startService(Intent(this, SyncService::class.java))

        initUI()

    }

    override fun onStart() {
        super.onStart()

        refreshOngoingShareView()

        HomeToWorkClient.getOngoingShare(OnSuccessListener {
            refreshOngoingShareView()
        }, OnFailureListener {

        })

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                IntentFilter(MessagingService.SHARE_COMPLETE_REQUEST)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                IntentFilter(MessagingService.SHARE_LEAVE_REQUEST)
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }

        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult is IntentResult) {

            val stringData = scanResult.contents.split(",")
            val shareId = java.lang.Long.parseLong(stringData[0])
            val latLng = LatLng(stringData[1].toDouble(), stringData[2].toDouble())
            checkShareCode(shareId, latLng)

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == Constants.REQ_CAMERA)
            joinShare()

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun refreshOngoingShareView() {
        if (HomeToWorkClient.ongoingShare != null) {
            ongoing_share_progress_bar.visibility = View.VISIBLE
            button_new_share.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_car))
        } else {
            ongoing_share_progress_bar.visibility = View.GONE
            button_new_share.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_plus))
        }
    }

    private fun initUI() {

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        val homeTab = AHBottomNavigationItem(R.string.activity_main_tab_home, R.drawable.ic_nav_home, R.color.colorPrimaryDark)
        val ranksTab = AHBottomNavigationItem(R.string.activity_main_tab_ranks, R.drawable.ic_trophy, R.color.colorPrimaryDark)
        val newShareButton = AHBottomNavigationItem(R.string.empty, R.drawable.ic_empty, R.color.teal_200) // Placeholder
        val matchTab = AHBottomNavigationItem(R.string.activity_main_tab_matches, R.drawable.ic_nav_match, R.color.light_green_400)
        val profileTab = AHBottomNavigationItem(R.string.activity_main_tab_profile, R.drawable.ic_nav_profile, R.color.colorAccent)

        bottom_navigation.removeAllItems()

        bottom_navigation.addItem(homeTab)
        bottom_navigation.addItem(ranksTab)
        bottom_navigation.addItem(newShareButton)
        bottom_navigation.addItem(matchTab)
        bottom_navigation.addItem(profileTab)

        bottom_navigation.accentColor = ContextCompat.getColor(this, R.color.colorAccent)
        bottom_navigation.inactiveColor = ContextCompat.getColor(this, R.color.light_bg_dark_hint_text)
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
                    createShare()
                }
                sheetView.find<TextView>(R.id.new_share_dialog_join).setOnClickListener {
                    dialog.dismiss()
                    joinShare()
                }
            } else {
                startActivity(intentFor<OngoingShareActivity>(Constants.EXTRA_SHARE to HomeToWorkClient.ongoingShare))
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

    private fun createShare() {


        HomeToWorkClient.createNewShare(OnSuccessListener { share ->

            Answers.getInstance().logCustom(CustomEvent("Nuova condivisione"))
            startActivity(intentFor<OngoingShareActivity>(Constants.EXTRA_SHARE to share))

        }, OnFailureListener {

            Toast.makeText(this, R.string.fragment_share_dialog_new_error, Toast.LENGTH_SHORT).show()

        })

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

    private fun checkShareCode(shareID: Long?, hostLocation: LatLng) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mFusedLocationClient.lastLocation.addOnSuccessListener { joinLocation ->

                when {
                    joinLocation == null || hostLocation.distanceTo(joinLocation) > 500 -> {

                        Toast.makeText(this, R.string.activity_ongoing_share_invalid_code, Toast.LENGTH_SHORT).show()

                    }
                    else -> HomeToWorkClient.joinShare(shareID, joinLocation, OnSuccessListener { share ->

                        Answers.getInstance().logCustom(CustomEvent("Unione a condivisione"))
                        startActivity(intentFor<OngoingShareActivity>(Constants.EXTRA_SHARE to share))

                    }, OnFailureListener { e ->

                        Toast.makeText(this, R.string.activity_signin_server_error, Toast.LENGTH_SHORT).show()
                        e.printStackTrace()

                    })
                }

            }

        }

    }

    private inner class PagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            return when (position) {
                HOME_TAB -> HomeFragment()
                RANKS_TAB -> RanksFragment()
                MATCHES_TAB -> MatchFragment()
                PROFILE_TAB -> UserProfileFragment()
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
