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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ntb.NavigationTabBar;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.fragments.HomeFragment;
import it.gruppoinfor.home2work.fragments.MatchFragment;
import it.gruppoinfor.home2work.fragments.NotificationFragment;
import it.gruppoinfor.home2work.fragments.ProfileFragment;
import it.gruppoinfor.home2work.fragments.SettingsFragment;
import it.gruppoinfor.home2work.models.Match;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    public NavigationTabBar.Model homeModel;
    public NavigationTabBar.Model matchModel;
    public NavigationTabBar.Model notificationModel;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.ntb_pager)
    ViewPager viewPager;
    @BindView(R.id.ntb)
    NavigationTabBar navigationTabBar;

    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        initUI();
        refreshData();

    }

    private void initUI() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();

        homeModel = new NavigationTabBar.Model.Builder(ContextCompat.getDrawable(this, R.drawable.ic_home), ContextCompat.getColor(this, R.color.white))
                .title("Home")
                .badgeTitle("Home")
                .build();

        models.add(homeModel);

        matchModel = new NavigationTabBar.Model.Builder(ContextCompat.getDrawable(this, R.drawable.ic_match), ContextCompat.getColor(this, R.color.white))
                .title("Match")
                .badgeTitle("Match")
                .build();

        models.add(matchModel);

        models.add(new NavigationTabBar.Model.Builder(ContextCompat.getDrawable(this, R.drawable.ic_user), ContextCompat.getColor(this, R.color.white))
                .title("Profilo")
                .badgeTitle("Profilo")
                .build()
        );


        notificationModel = new NavigationTabBar.Model.Builder(ContextCompat.getDrawable(this, R.drawable.ic_bell), ContextCompat.getColor(this, R.color.white))
                .title("Notifiche")
                .badgeTitle("Notifiche")
                .build();

        models.add(notificationModel);

        models.add(new NavigationTabBar.Model.Builder(ContextCompat.getDrawable(this, R.drawable.ic_settings), ContextCompat.getColor(this, R.color.white))
                .title("Impostazioni")
                .badgeTitle("Impostazioni")
                .build()
        );

        navigationTabBar.setOnPageChangeListener(this);
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 2);
        onPageSelected(2);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                setTitle("Home");
                break;
            case 1:
                setTitle("Match");
                break;
            case 2:
                setTitle("Profilo");
                break;
            case 3:
                setTitle("Notifiche");
            case 4:
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

    public void refreshData() {
        Client.getAPI().getUserMatches(Client.getUser().getId()).enqueue(new Callback<List<Match>>() {

            @Override
            public void onResponse(retrofit2.Call<List<Match>> call, Response<List<Match>> response) {
                Client.getUser().setMatches(response.body());

                Stream<Match> matchStream = response.body().stream();
                long newMatches = matchStream.filter(m -> m.isNew()).count();

                matchModel.hideBadge();

                if(newMatches > 0){
                    matchModel.setBadgeTitle(Long.toString(newMatches));
                    matchModel.showBadge();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Match>> call, Throwable t) {

            }

        });
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
                    frag = new ProfileFragment();
                    break;
                case 3:
                    frag = new NotificationFragment();
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
