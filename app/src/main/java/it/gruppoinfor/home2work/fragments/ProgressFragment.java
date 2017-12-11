package it.gruppoinfor.home2work.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.arasthel.asyncjob.AsyncJob;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.EditProfileActivity;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.SignInActivity;
import it.gruppoinfor.home2work.custom.AvatarView;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Profile;
import it.gruppoinfor.home2workapi.model.Share;
import retrofit2.Call;


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
    @BindView(R.id.edit_profile_button)
    ImageButton editProfileButton;


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

        initUI();
        refreshData();
        return rootView;
    }

    private void initUI() {

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        viewPager.addOnPageChangeListener(this);

        avatarView.setAvatarURL(Client.getSignedUser().getAvatarURL());
        nameTextView.setText(Client.getSignedUser().toString());
        jobTextView.setText(Client.getSignedUser().getCompany().toString());
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

            Call<List<Share>> sharesCall = Client.getAPI().getUserShares(Client.getSignedUser().getId());
            // TODO Call<List<Achievement>> bookingsCall = Client.getAPI().getUserAchievements(Client.getSignedUser().getId());
            // TODO Call<Profile> requestsCall = Client.getAPI().getUserProfile(Client.getSignedUser().getId());

            try {
                Client.setUserShares(new ArrayList<>(sharesCall.execute().body()));
                //Client.setUserAchivements(new ArrayList<>(bookingsCall.execute().body()));
                //Client.setUserProfile(new ArrayList<>(requestsCall.execute().body()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            AsyncJob.doOnMainThread(() -> {
                swipeRefreshLayout.setRefreshing(false);
                refreshTabs();
                refreshProfileUI();
            });

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
        // TODO avatarView.setExp(profile.getExp(), profile.getExpLevel(), profile.getExpLevelProgress());
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    private void logout() {
        SessionManager.with(getContext()).signOutUser();

        // Avvio Activity di login
        Intent i = new Intent(getContext(), SignInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @OnClick(R.id.edit_profile_button)
    public void onViewClicked() {
        startActivity(new Intent(getActivity(), EditProfileActivity.class));
    }

    private class ProgressPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Pair<Fragment, String>> fragments;

        ProgressPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            fragments.add(new Pair<>(new ProgressFragmentDashboard(), "Dashboard"));
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


