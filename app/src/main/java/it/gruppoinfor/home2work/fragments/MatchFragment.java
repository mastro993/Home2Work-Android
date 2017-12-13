package it.gruppoinfor.home2work.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arasthel.asyncjob.AsyncJob;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.BookingActivity;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.MatchActivity;
import it.gruppoinfor.home2work.activities.RequestActivity;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Booking;
import it.gruppoinfor.home2workapi.model.Match;
import retrofit2.Call;


public class MatchFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private Unbinder unbinder;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    protected static List<Match> MatchList = new ArrayList<>();
    protected static List<Booking> BookingList = new ArrayList<>();
    protected static List<Booking> RequestList = new ArrayList<>();

    public MatchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initUI();
        refreshData();
        return rootView;
    }

    private void initUI() {
        toolbarTitle.setText("Match");
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        viewPager.addOnPageChangeListener(this);
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
        AsyncJob.doInBackground(() -> {

            Call<List<Match>> matchesCall = Client.getAPI().getUserMatches(Client.User.getId());
            Call<List<Booking>> bookingsCall = Client.getAPI().getUserBookings(Client.User.getId());
            Call<List<Booking>> requestsCall = Client.getAPI().getUserRequests(Client.User.getId());

            try {
                MatchList = new ArrayList<>(matchesCall.execute().body());
                BookingList = new ArrayList<>(bookingsCall.execute().body());
                RequestList = new ArrayList<>(requestsCall.execute().body());
            } catch (Exception e) {
                e.printStackTrace();
            }

            AsyncJob.doOnMainThread(() -> {
                swipeRefreshLayout.setRefreshing(false);
                refreshTabs();
                refreshBadgeCounter();
            });
        });
    }

    private void refreshTabs() {
        int currentPage = viewPager.getCurrentItem();
        MatchPagerAdapter pagerAdapter = new MatchPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(currentPage);
    }


    protected void refreshBadgeCounter() {
        long newMatches = 0, pendingRequests = 0;

        for (Match match : MatchList)
            if (match.isNew()) newMatches++;

        for (Booking request : RequestList)
            if (request.getBookingStatus() == Booking.PENDING) pendingRequests++;

        Long badgeCounter = newMatches + pendingRequests;

        ((MainActivity) getActivity()).setBadge(1, badgeCounter > 0 ? badgeCounter.toString() : "");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case BookingActivity.SHARE_STARTED:
                refreshData();
                break;
            case MatchActivity.BOOKING_ADDED:
                refreshData();
                viewPager.setCurrentItem(1);
                Toasty.success(getContext(), "Prenotazione effettuata").show();
                break;
            case MatchActivity.BOOKING_NOT_ADDED:
                Toasty.error(getContext(), "Prenotazione non effettuata").show();
                break;
            case RequestActivity.REQUEST_REJECTED:
            case RequestActivity.REQUEST_ACCEPTED:
                refreshData();
                viewPager.setCurrentItem(2);
                refreshBadgeCounter();
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MatchPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Pair<Fragment, String>> fragments;

        MatchPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            fragments.add(new Pair<>(new MatchFragmentList(), "Disponibili"));
            fragments.add(new Pair<>(new MatchFragmentBooking(), "Prenotati"));
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
