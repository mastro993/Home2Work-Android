package it.gruppoinfor.home2work.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.arasthel.asyncjob.AsyncJob;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.fragments.HomeFragment;
import it.gruppoinfor.home2work.fragments.MatchFragment;
import it.gruppoinfor.home2work.fragments.MessagesFragment;
import it.gruppoinfor.home2work.fragments.ProgressFragment;
import it.gruppoinfor.home2work.fragments.SettingsFragment;
import it.gruppoinfor.home2work.services.LocationService;
import it.gruppoinfor.home2work.utils.UserPrefs;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Booking;
import it.gruppoinfor.home2workapi.model.Match;

public class MainActivity extends AppCompatActivity {

    private final int START_POSITION = 2;
    @BindView(R.id.bottom_navigation)


    public AHBottomNavigation bottomNavigation;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_pager)
    AHBottomNavigationViewPager viewPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initUI();

        // Servizio di localizzazione
        if (UserPrefs.activityTrackingEnabled) {
            Intent locationIntent = new Intent(this, LocationService.class);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(locationIntent);
            } else {
                startService(locationIntent);
            }
        }

    }

    private void initUI() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }

        // Create items
        AHBottomNavigationItem homeTab = new AHBottomNavigationItem(R.string.home_tab, R.drawable.ic_home, R.color.colorPrimaryDark);
        AHBottomNavigationItem matchTab = new AHBottomNavigationItem(R.string.match_tab, R.drawable.ic_match, R.color.colorPrimary);
        AHBottomNavigationItem progressTab = new AHBottomNavigationItem(R.string.progress_tab, R.drawable.ic_star_circle, R.color.light_blue_300);
        AHBottomNavigationItem notificationTab = new AHBottomNavigationItem(R.string.messages_tab, R.drawable.ic_mail, R.color.light_blue_500);
        AHBottomNavigationItem settingsTab = new AHBottomNavigationItem(R.string.settings_tab, R.drawable.ic_preferences, R.color.colorAccent);

        // Add items
        bottomNavigation.removeAllItems();
        bottomNavigation.addItem(homeTab);
        bottomNavigation.addItem(matchTab);
        bottomNavigation.addItem(progressTab);
        bottomNavigation.addItem(notificationTab);
        bottomNavigation.addItem(settingsTab);

        // Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(false);

        // Change colors
        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimary));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.grey_400));
        bottomNavigation.setForceTint(true);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);

        // Set current item programmatically
        bottomNavigation.setCurrentItem(START_POSITION);
        setTitle(bottomNavigation.getItem(START_POSITION).getTitle(this));

        // Customize notification (title, background, typeface)
        bottomNavigation.setNotificationBackgroundColor(ContextCompat.getColor(this, R.color.red_500));

        bottomNavigation.setOnTabSelectedListener(((position, wasSelected) -> {
            viewPager.setCurrentItem(position, false);
            setTitle(bottomNavigation.getItem(position).getTitle(this));
            return true;
        }));

        viewPager.setOffscreenPageLimit(4);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // Setup iniziale
        viewPager.setCurrentItem(START_POSITION);
        getSupportActionBar().hide();

        viewPager.addOnPageChangeListener(new MyPageChangeListener());
    }

    @Override
    public void setTitle(int titleId) {
        //toolbarTitle.setText(titleId);
        super.setTitle(titleId);
    }

    @Override
    public void setTitle(CharSequence title) {
        //toolbarTitle.setText(title);
        super.setTitle(title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (resultCode == BookingActivity.SHARE_STARTED) {
            Toasty.success(this, "Condivisione convalidata").show();
            viewPager.setCurrentItem(2);
            initUI();
        }
    }

    public void setBadge(int itemPosition, String title) {
        bottomNavigation.setNotification(title, itemPosition);
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragments;

        PagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            fragments.add(new HomeFragment());
            fragments.add(new MatchFragment());
            fragments.add(new ProgressFragment());
            fragments.add(new MessagesFragment());
            fragments.add(new SettingsFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }


    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 2 || position == 1)
                getSupportActionBar().hide();
            else
                getSupportActionBar().show();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
