package it.gruppoinfor.home2work.fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.custom.DonutProgressAnimation;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.Mockup;
import it.gruppoinfor.home2workapi.model.Karma;


public class ProgressFragment extends Fragment implements ViewPager.OnPageChangeListener {

    Unbinder unbinder;
    MainActivity activity;
    ProgressPagerAdapter pagerAdapter;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.karma_donut_progress)
    DonutProgress karmaDonutProgress;
    @BindView(R.id.user_propic)
    CircleImageView userPropic;
    @BindView(R.id.karma_level)
    TextView karmaLevel;
    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.job_text_view)
    TextView jobTextView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;


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

        nameTextView.setText(Client.getSignedUser().toString());
        jobTextView.setText(Client.getSignedUser().getJob().getCompany().toString());

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar_placeholder).dontAnimate();

        Glide.with(getActivity())
                .load(Client.getSignedUser().getAvatarURL())
                .apply(requestOptions)
                .into(userPropic);

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
            Client.setUserProfile(profile);
            swipeRefreshLayout.setRefreshing(false);
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

        Karma karma = Client.getUserProfile().getKarma();

        DonutProgressAnimation animation = new DonutProgressAnimation(karmaDonutProgress, 0, karma.getLevelProgres());
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        karmaDonutProgress.startAnimation(animation);

        ValueAnimator animator = ValueAnimator.ofInt(0, karma.getLevel());
        animator.setDuration(250);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation1 ->
                karmaLevel.setText(animation1.getAnimatedValue().toString())
        );
        animator.start();

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
            fragments.add(new Pair<>(new ProgressFragmentKarma(), "Karma"));
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


