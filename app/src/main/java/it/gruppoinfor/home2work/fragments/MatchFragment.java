package it.gruppoinfor.home2work.fragments;


import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.Mockup;
import it.gruppoinfor.home2workapi.model.Match;


public class MatchFragment extends Fragment {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Unbinder unbinder;
    private MatchPagerAdapter pagerAdapter;
    private boolean refreshingMatches = false;
    private boolean refreshingBookedMatches = false;

    public MatchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CoordinatorLayout rootView = (CoordinatorLayout) inflater.inflate(R.layout.fragment_match, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initUI();
        return rootView;
    }

    private void initUI() {
        pagerAdapter = new MatchPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        swipeRefreshLayout.setOnRefreshListener(this::refreshMatches);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (Client.getUserMatches() == null || Client.getUserBookings() == null) {
                refreshMatches();
            } else {
                refreshUI();
            }
        }
    }

    private void refreshMatches() {
        swipeRefreshLayout.setRefreshing(true);
        refreshingMatches = true;
        refreshingBookedMatches = true;

        // TODO fare refresh da web

        Mockup.refreshUserMatches(matchItems -> {
            Client.setUserMatches(matchItems);
            refreshBadge();
            refreshingMatches = false;
            refreshUI();
        });

        Mockup.refreshUserBookedMatches(bookedMatchItems -> {
            Client.setUserBookedMatches(bookedMatchItems);
            refreshingBookedMatches = false;
            refreshUI();
        });

    }

    private void refreshUI() {
        if (!refreshingBookedMatches && !refreshingMatches) {
            swipeRefreshLayout.setRefreshing(false);
            pagerAdapter = new MatchPagerAdapter(getChildFragmentManager());
            viewPager.setAdapter(pagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
        }
    }


    private void refreshBadge() {
        Stream<Match> matchStream = Client.getUserMatches().stream();
        long newMatches = matchStream.filter(m -> !m.isHidden()).filter(Match::isNew).count();

        if (newMatches > 0) {
            ((MainActivity) getActivity()).bottomNavigation.setNotification(Long.toString(newMatches), 1);
        } else {
            ((MainActivity) getActivity()).bottomNavigation.setNotification("", 1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class MatchPagerAdapter extends FragmentStatePagerAdapter {

        MatchPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0:
                    frag = new MatchListFragment();
                    break;
                case 1:
                    frag = new MatchBookingFragment();
                    break;
                case 2:
                    frag = new MatchRequestFragment();
                    break;
            }
            return frag;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Disponibili";
                case 1:
                    return "Prenotazioni";
                case 2:
                    return "Richieste";
            }
            return null;
        }


        @Override
        public int getCount() {
            return 3;
        }


    }


}
