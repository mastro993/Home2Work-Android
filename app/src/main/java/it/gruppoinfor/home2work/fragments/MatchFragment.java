package it.gruppoinfor.home2work.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.MatchActivity;
import it.gruppoinfor.home2work.activities.RequestActivity;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.Mockup;
import it.gruppoinfor.home2workapi.model.Match;


public class MatchFragment extends Fragment {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Unbinder unbinder;
    private MatchPagerAdapter pagerAdapter;
    private boolean refreshingMatches = false;
    private boolean refreshingBookedMatches = false;
    private boolean refreshingRequests = false;

    public MatchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CoordinatorLayout rootView = (CoordinatorLayout) inflater.inflate(R.layout.fragment_match, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initUI();
        refreshMatches();
        return rootView;
    }

    private void initUI() {
        pagerAdapter = new MatchPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void refreshMatches() {
        refreshingMatches = true;
        refreshingBookedMatches = true;

        // TODO refresh da web
        Mockup.refreshUserMatches(matchItems -> {
            Client.setUserMatches(matchItems);
            refreshBadge();
            refreshingMatches = false;
            refreshUI();
        });

        // TODO refresh da web
        Mockup.refreshUserBookings(bookedMatchItems -> {
            Client.setUserBookedMatches(bookedMatchItems);
            refreshingBookedMatches = false;
            refreshUI();
        });

        // TODO refresh da web
        Mockup.refreshUserRequests(bookedMatchItems -> {
            Client.setUserRequests(bookedMatchItems);
            refreshingRequests = false;
            refreshUI();
        });

    }

    private void refreshUI() {
        if (!refreshingBookedMatches && !refreshingMatches && !refreshingRequests) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == MatchActivity.BOOKING_ADDED) {
            pagerAdapter = new MatchPagerAdapter(getChildFragmentManager());
            viewPager.setAdapter(pagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setCurrentItem(1);
        } else if (resultCode == RequestActivity.REQUEST_REJECTED || resultCode == RequestActivity.REQUEST_ACCEPTED){
            pagerAdapter = new MatchPagerAdapter(getChildFragmentManager());
            viewPager.setAdapter(pagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setCurrentItem(2);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MatchPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Pair<Fragment, String>> fragments;

        MatchPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            fragments.add(new Pair<>(new MatchFragmentList(), "Disponibili"));
            fragments.add(new Pair<>(new MatchFragmentBooking(), "Prenotazioni"));
            fragments.add(new Pair<>(new MatchFragmentRequest(), "Richieste"));
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
