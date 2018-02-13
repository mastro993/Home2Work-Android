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
import android.support.v7.widget.Toolbar

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager

import butterknife.BindView
import butterknife.ButterKnife
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.fragments.HomeFragment
import it.gruppoinfor.home2work.fragments.MatchFragment
import it.gruppoinfor.home2work.fragments.ProfileFragment
import it.gruppoinfor.home2work.fragments.SharesFragment
import it.gruppoinfor.home2work.services.LocationService
import it.gruppoinfor.home2work.services.SyncService
import it.gruppoinfor.home2work.user.UserPrefs

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

    @BindView(R.id.bottom_navigation)
    var bottomNavigation: AHBottomNavigation? = null
    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null
    @BindView(R.id.view_pager)
    internal var viewPager: AHBottomNavigationViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        /*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowHomeEnabled(false)
        }

        initUI()

        if (UserPrefs.TrackingEnabled) {
            val locationIntent = Intent(this, LocationService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(locationIntent)
            } else {
                startService(locationIntent)
            }
        }

        startService(Intent(this, SyncService::class.java))

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
        if (supportActionBar != null) {
            if (position == 3)
                supportActionBar!!.hide()
            else
                supportActionBar!!.show()
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    private fun initUI() {

        // Navigation
        val homeTab = AHBottomNavigationItem(R.string.activity_main_tab_home, R.drawable.ic_home, R.color.colorPrimaryDark)
        val matchTab = AHBottomNavigationItem(R.string.activity_main_tab_matches, R.drawable.ic_match, R.color.colorPrimary)
        val sharesTab = AHBottomNavigationItem(R.string.activity_main_tab_shares, R.drawable.ic_car_side, R.color.light_blue_300)
        val profileTab = AHBottomNavigationItem(R.string.activity_main_tab_profile, R.drawable.ic_user, R.color.colorAccent)

        bottomNavigation!!.removeAllItems()
        bottomNavigation!!.addItem(homeTab)
        bottomNavigation!!.addItem(matchTab)
        bottomNavigation!!.addItem(sharesTab)
        bottomNavigation!!.addItem(profileTab)

        bottomNavigation!!.accentColor = ContextCompat.getColor(this, R.color.colorAccent)
        bottomNavigation!!.inactiveColor = ContextCompat.getColor(this, R.color.light_bg_dark_hint_text)
        bottomNavigation!!.isForceTint = true

        bottomNavigation!!.setNotificationBackgroundColor(ContextCompat.getColor(this, R.color.red_500))

        bottomNavigation!!.isBehaviorTranslationEnabled = false
        bottomNavigation!!.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW

        bottomNavigation!!.setOnTabSelectedListener { position, wasSelected ->
            viewPager!!.setCurrentItem(position, false)
            title = bottomNavigation!!.getItem(position).getTitle(this)
            true
        }

        // View Pager
        viewPager!!.offscreenPageLimit = 4
        val pagerAdapter = PagerAdapter(supportFragmentManager)
        viewPager!!.adapter = pagerAdapter
        viewPager!!.addOnPageChangeListener(this)

        // Setup iniziale
        bottomNavigation!!.currentItem = 0
        title = bottomNavigation!!.getItem(0).getTitle(this)
    }

    fun setBadge(itemPosition: Int, title: String) {
        bottomNavigation!!.setNotification(title, itemPosition)
    }

    private inner class PagerAdapter
    //List<Fragment> fragments;

    internal constructor(fm: FragmentManager)/*            fragments = new ArrayList<>();
            fragments.add(new HomeFragment());
            fragments.add(new MatchFragment());
            fragments.add(new SharesFragment());
            fragments.add(new ProfileFragment());*/ : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            //return fragments.get(position);
            when (position) {
                0 -> return HomeFragment()
                1 -> return MatchFragment()
                2 -> return SharesFragment()
                3 -> return ProfileFragment()
                else -> return HomeFragment()
            }
        }

        override fun getCount(): Int {
            /*return fragments.size();*/
            return 4
        }


    }


}
