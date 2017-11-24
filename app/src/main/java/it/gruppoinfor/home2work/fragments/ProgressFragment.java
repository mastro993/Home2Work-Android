package it.gruppoinfor.home2work.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
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
    @BindView(R.id.collapsed_toolbar_layout)
    RelativeLayout collapsedLayout;
    @BindView(R.id.collapsed_user_propic)
    CircleImageView collapsedUserPropic;
    @BindView(R.id.collapsed_name_text_view)
    TextView collapsedNameTextView;
    @BindView(R.id.collapsed_exp_level)
    TextView collapsedExpLevel;
    @BindView(R.id.collapsed_level_frame)
    ImageView collapsedLevelFrame;

    private boolean toolbarCollapsed = false;


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

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            boolean collapsing = collapsingToolbar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsingToolbar);
            if (collapsing && !toolbarCollapsed) {
                collapsedLayout.animate().alpha(1).setDuration(300);

                Animation showAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.up_bottom);
                collapsedLayout.startAnimation(showAnimation);

                toolbarCollapsed = true;
            } else if (!collapsing && toolbarCollapsed) {
                collapsedLayout.animate().alpha(0).setDuration(300);

                Animation hideAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_up);
                collapsedLayout.startAnimation(hideAnimation);

                toolbarCollapsed = false;
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

        nameTextView.setText(Client.getSignedUser().toString());
        collapsedNameTextView.setText(Client.getSignedUser().toString());
        //toolbar.setTitle(Client.getSignedUser().toString());
        //jobTextView.setText(Client.getSignedUser().getJob().getCompany().toString());

        avatarView.setAvatarURL(Client.getSignedUser().getAvatarURL());

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar_placeholder).dontAnimate();
        Glide.with(this).load(Client.getSignedUser().getAvatarURL()).apply(requestOptions).into(collapsedUserPropic);


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

        collapsedLevelFrame.setImageDrawable(avatarView.getShieldIcon());
        collapsedExpLevel.setText(Integer.toString(avatarView.getLevel()));

        if (avatarView.getLevel() > 99) {
            Shader textShader = new LinearGradient(
                    0, 0, 0, 60,
                    ContextCompat.getColor(getContext(), R.color.colorAccent),
                    ContextCompat.getColor(getContext(), R.color.colorPrimary),
                    Shader.TileMode.CLAMP);
            collapsedExpLevel.getPaint().setShader(textShader);
        }

    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_logout) {


        }

        return super.onOptionsItemSelected(item);
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


