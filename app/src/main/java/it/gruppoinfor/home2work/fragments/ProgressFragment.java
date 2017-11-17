package it.gruppoinfor.home2work.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.custom.AvatarView;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.Mockup;
import it.gruppoinfor.home2workapi.model.Profile;


public class ProgressFragment extends Fragment implements ViewPager.OnPageChangeListener {

    Unbinder unbinder;
    MainActivity activity;
    ProgressPagerAdapter pagerAdapter;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.job_text_view)
    TextView jobTextView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.avatar_view)
    AvatarView avatarView;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;


    public ProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        this.activity = (MainActivity) context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CoordinatorLayout rootView = (CoordinatorLayout) inflater.inflate(R.layout.fragment_progress, container, false);
        unbinder = ButterKnife.bind(this, rootView);


        appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (collapsingToolbar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsingToolbar)) {
                //hello.animate().alpha(1).setDuration(600);
                Log.i("WE", "true");
            } else {
                //hello.animate().alpha(0).setDuration(600);
                Log.i("WE", "false");
            }
        });


        initUI();
        refreshData();
        return rootView;
    }

    private void initUI() {

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        viewPager.addOnPageChangeListener(this);

        //nameTextView.setText(Client.getSignedUser().toString());
        toolbar.setTitle(Client.getSignedUser().toString());
        jobTextView.setText(Client.getSignedUser().getJob().getCompany().toString());

        avatarView.setAvatarURL(Client.getSignedUser().getAvatarURL());


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        toggleRefreshing(state == ViewPager.SCROLL_STATE_IDLE);
    }

    public void toggleRefreshing(boolean enabled) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(enabled);
        }
    }

    private void refreshData() {
        swipeRefreshLayout.setRefreshing(true);

        // TODO refresh profilo dal server
        Mockup.getUserProfileAsync(profile -> {
            swipeRefreshLayout.setRefreshing(false);
            Client.setUserProfile(profile);
            refreshProfileUI();
            refreshTabs();
        });
    }

    private void refreshTabs() {
        int currentPage = viewPager.getCurrentItem();
        pagerAdapter = new ProgressPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(currentPage);
    }

    private void refreshProfileUI() {

        Profile profile = Client.getUserProfile();

        avatarView.setExp(profile.getExp(), profile.getExpLevel(), profile.getExpLevelProgress());

    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    private class ProgressPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Pair<Fragment, String>> fragments;

        ProgressPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            fragments.add(new Pair<>(new ProgressFragmentProfile(), "Profilo"));
            fragments.add(new Pair<>(new ProgressFragmentAchievements(), "Obiettivi"));
            fragments.add(new Pair<>(new ProgressFragmentShares(), "Condivisioni"));
            fragments.add(new Pair<>(new ProgressFragmentStats(), "Statistiche"));

        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position).first;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).second;
        }


        @Override
        public int getCount() {
            return fragments.size();
        }


    }
}


