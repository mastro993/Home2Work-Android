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

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.fragments.HomeFragment;
import it.gruppoinfor.home2work.fragments.MatchFragment;
import it.gruppoinfor.home2work.fragments.ProfileFragment;
import it.gruppoinfor.home2work.services.LocationService;
import it.gruppoinfor.home2work.utils.UserPrefs;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.bottom_navigation)


    public AHBottomNavigation bottomNavigation;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_pager)
    AHBottomNavigationViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }

        initUI();

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

        // Navigation
        AHBottomNavigationItem homeTab = new AHBottomNavigationItem(R.string.home_tab, R.drawable.ic_home, R.color.colorPrimaryDark);
        AHBottomNavigationItem matchTab = new AHBottomNavigationItem(R.string.match_tab, R.drawable.ic_match, R.color.colorPrimary);
        AHBottomNavigationItem progressTab = new AHBottomNavigationItem(R.string.progress_tab, R.drawable.ic_user, R.color.light_blue_300);

        bottomNavigation.removeAllItems();
        bottomNavigation.addItem(homeTab);
        bottomNavigation.addItem(matchTab);
        bottomNavigation.addItem(progressTab);

        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimary));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.grey_400));
        bottomNavigation.setForceTint(true);

        bottomNavigation.setNotificationBackgroundColor(ContextCompat.getColor(this, R.color.red_500));

        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);

        bottomNavigation.setOnTabSelectedListener(((position, wasSelected) -> {
            viewPager.setCurrentItem(position, false);
            setTitle(bottomNavigation.getItem(position).getTitle(this));
            return true;
        }));

        // View Pager
        viewPager.setOffscreenPageLimit(4);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);

        // Setup iniziale
        bottomNavigation.setCurrentItem(0);
        setTitle(bottomNavigation.getItem(0).getTitle(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            fragments.add(new ProfileFragment());
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 2)
            getSupportActionBar().hide();
        else
            getSupportActionBar().show();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
