package it.gruppoinfor.home2work.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.fragments.HomeFragment;
import it.gruppoinfor.home2work.fragments.MatchFragment;
import it.gruppoinfor.home2work.fragments.MessagesFragment;
import it.gruppoinfor.home2work.fragments.ProgressFragment;
import it.gruppoinfor.home2work.fragments.SettingsFragment;
import it.gruppoinfor.home2work.receivers.SyncAlarmReceiver;
import it.gruppoinfor.home2work.services.RouteService;
import it.gruppoinfor.home2work.utils.UserPrefs;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.Mockup;
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

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            //getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        initUI();
        refreshData();

        // Servizio di localizzazione
        if (UserPrefs.activityTrackingEnabled) {
            if (!RouteService.isRunning(this)) {
                Intent locationIntent = new Intent(this, RouteService.class);
                startService(locationIntent);
            }
        }

        setSyncAlarm();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initUI() {

        // Create items
        AHBottomNavigationItem homeTab = new AHBottomNavigationItem(R.string.home_tab, R.drawable.ic_home, R.color.colorPrimaryDark);
        AHBottomNavigationItem matchTab = new AHBottomNavigationItem(R.string.match_tab, R.drawable.ic_match, R.color.colorPrimary);
        AHBottomNavigationItem progressTab = new AHBottomNavigationItem(R.string.progress_tab, R.drawable.ic_star_circle, R.color.light_blue_300);
        AHBottomNavigationItem notificationTab = new AHBottomNavigationItem(R.string.messages_tab, R.drawable.ic_mail, R.color.light_blue_500);
        AHBottomNavigationItem settingsTab = new AHBottomNavigationItem(R.string.settings_tab, R.drawable.ic_preferences, R.color.colorAccent);


        // Add items
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

        //bottomNavigation.setTranslucentNavigationEnabled(true);

        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);

        // Use colored navigation with circle reveal effect
        //bottomNavigation.setColored(true);

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

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        });


    }

    public void refreshData() {

        if (!UserPrefs.activityTrackingEnabled) bottomNavigation.setNotification("!", 4);

        // TODO fare refresh da web
        Mockup.refreshUserMatches(matchItems -> {
            Client.setUserMatches(matchItems);
            Stream<Match> matchStream = matchItems.stream();
            long newMatches = matchStream.filter(Match::isNew).count();
            bottomNavigation.setNotification(Long.toString(newMatches), 1);
        });

        // TODO fare refresh da web
        Mockup.refreshUserBookings(Client::setUserBookedMatches);

    }

    private void setSyncAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, SyncAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        am.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                AlarmManager.INTERVAL_HALF_DAY,
                pi);
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0:
                    frag = new HomeFragment();
                    break;
                case 1:
                    frag = new MatchFragment();
                    break;
                case 2:
                    frag = new ProgressFragment();
                    break;
                case 3:
                    frag = new MessagesFragment();
                    break;
                case 4:
                    frag = new SettingsFragment();
                    break;
            }
            return frag;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return 5;
        }


    }

}
