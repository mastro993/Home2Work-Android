package it.gruppoinfor.home2work.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.pixplicity.easyprefs.library.Prefs
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.fragments.HomeFragment
import it.gruppoinfor.home2work.fragments.MatchFragment
import it.gruppoinfor.home2work.fragments.ProfileFragment
import it.gruppoinfor.home2work.fragments.SharesFragment
import it.gruppoinfor.home2work.services.LocationService
import it.gruppoinfor.home2work.services.SyncService
import it.gruppoinfor.home2work.utils.Const
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        if (Prefs.getBoolean(Const.PREF_ACTIVITY_TRACKING, true)) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

        if (position == Const.PROFILE_TAB)
            supportActionBar?.hide()
        else
            supportActionBar?.show()

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    private fun initUI() {

        val homeTab = AHBottomNavigationItem(R.string.activity_main_tab_home, R.drawable.ic_nav_home, R.color.colorPrimaryDark)
        val matchTab = AHBottomNavigationItem(R.string.activity_main_tab_matches, R.drawable.ic_nav_match, R.color.colorPrimary)
        val sharesTab = AHBottomNavigationItem(R.string.activity_main_tab_shares, R.drawable.ic_nav_shares, R.color.light_blue_300)
        val profileTab = AHBottomNavigationItem(R.string.activity_main_tab_profile, R.drawable.ic_nav_profile, R.color.colorAccent)

        bottom_navigation.removeAllItems()
        bottom_navigation.addItem(homeTab)
        bottom_navigation.addItem(matchTab)
        bottom_navigation.addItem(sharesTab)
        bottom_navigation.addItem(profileTab)

        bottom_navigation.accentColor = ContextCompat.getColor(this, R.color.colorAccent)
        bottom_navigation.inactiveColor = ContextCompat.getColor(this, R.color.light_bg_dark_hint_text)
        bottom_navigation.isForceTint = true

        bottom_navigation.setNotificationBackgroundColor(ContextCompat.getColor(this, R.color.red_500))

        bottom_navigation.isBehaviorTranslationEnabled = false
        bottom_navigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW

        bottom_navigation.setOnTabSelectedListener { position, _ ->
            view_pager.setCurrentItem(position, false)
            title = bottom_navigation.getItem(position).getTitle(this)
            true
        }

        view_pager.offscreenPageLimit = 4
        val pagerAdapter = PagerAdapter(supportFragmentManager)
        view_pager.adapter = pagerAdapter
        view_pager.addOnPageChangeListener(this)

        bottom_navigation.currentItem = Const.HOME_TAB
        title = bottom_navigation.getItem(Const.HOME_TAB).getTitle(this)

    }

    fun setNavigationBadge(itemPosition: Int, title: String) {

        bottom_navigation.setNotification(title, itemPosition)

    }

    private inner class PagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            return when (position) {
                0 -> HomeFragment()
                1 -> MatchFragment()
                2 -> SharesFragment()
                3 -> ProfileFragment()
                else -> HomeFragment()
            }
        }

        override fun getCount(): Int {

            return 5
        }

    }


}
