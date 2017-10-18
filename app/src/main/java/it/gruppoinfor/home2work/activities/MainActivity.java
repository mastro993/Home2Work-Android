package it.gruppoinfor.home2work.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ntb.NavigationTabBar;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.fragments.MatchFragment;
import it.gruppoinfor.home2work.fragments.NotificationFragment;
import it.gruppoinfor.home2work.fragments.ProfileFragment;
import it.gruppoinfor.home2work.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.ntb_pager)
    ViewPager viewPager;
    @BindView(R.id.ntb)
    NavigationTabBar navigationTabBar;

    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        initUI();

    }

    private void initUI() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        pagerAdapter = new PagerAdapter(fragmentManager);
        viewPager.setAdapter(pagerAdapter);
        //pager.setPageTransformer(true, new DrawFromBackTransformer());

        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_match),
                        ContextCompat.getColor(this, R.color.grey_100)
                ).title("Match")
                        .badgeTitle("Match")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_user),
                        ContextCompat.getColor(this, R.color.grey_100)
                ).title("Profilo")
                        .badgeTitle("Profilo")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_bell),
                        ContextCompat.getColor(this, R.color.grey_100)
                ).title("Notifiche")
                        .badgeTitle("Notifiche")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_settings),
                        ContextCompat.getColor(this, R.color.grey_100)
                ).title("Impostazioni")
                        .badgeTitle("Impostazioni")
                        .build()
        );

        navigationTabBar.setOnPageChangeListener(this);
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        onPageSelected(0);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                setTitle("Match");
                break;
            case 1:
                setTitle("Profilo");
                break;
            case 2:
                setTitle("Notifiche");
                break;
            case 3:
                setTitle("Impostazioni");
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        pageTitle.setText(title);
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
                    frag = new MatchFragment();
                    break;
                case 1:
                    frag = new ProfileFragment();
                    break;
                case 2:
                    frag = new NotificationFragment();
                    break;
                case 3:
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
            return 4;
        }


    }

}
