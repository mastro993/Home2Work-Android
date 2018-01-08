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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.App;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.EditProfileActivity;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.SettingsActivity;
import it.gruppoinfor.home2work.activities.SignInActivity;
import it.gruppoinfor.home2work.custom.AvatarView;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2workapi.model.Achievement;
import it.gruppoinfor.home2workapi.model.Profile;
import it.gruppoinfor.home2workapi.model.User;


public class ProfileFragment extends Fragment implements ViewPager.OnPageChangeListener {

    protected static Profile Profile;
    protected static List<Achievement> AchievementList = new ArrayList<>();
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
    // protected Statistics statistics;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        this.activity = (MainActivity) context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CoordinatorLayout rootView = (CoordinatorLayout) inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        initUI();
        refreshData();
        return rootView;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.profile_options_button)
    public void onViewClicked() {

        String[] options = new String[]{"Modifica profilo", "Impostazioni", "Esci"};

        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .items(options)
                .itemsCallback((dialog, itemView, position, text) -> {
                    switch (position) {
                        case 0:
                            startActivity(new Intent(getActivity(), EditProfileActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(getActivity(), SettingsActivity.class));
                            break;
                        case 2:
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle(R.string.logout_dialog_title);
                            builder.setMessage(R.string.logout_dialog_content);
                            builder.setPositiveButton(R.string.logout_dialog_confirm, ((dialogInterface, i) -> logout()));
                            builder.setNegativeButton(R.string.logout_dialog_cancel, null);
                            builder.show();
                            break;
                    }
                })
                .show();

    }

    private void initUI() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        viewPager.addOnPageChangeListener(this);

        User user = App.home2WorkClient.getUser();
        avatarView.setAvatarURL(user.getAvatarURL());
        nameTextView.setText(user.toString());
        jobTextView.setText(user.getCompany().toString());
    }

    private void refreshData() {
        swipeRefreshLayout.setRefreshing(true);

        App.home2WorkClient.refreshUser(aVoid -> {
            swipeRefreshLayout.setRefreshing(false);
            refreshTabs();
            refreshProfileUI();
        }, e -> Toasty.error(getContext(), "Non è possibile aggiornare le informazioni dell'utente al momento").show());

    }

    private void refreshTabs() {
        int currentPage = viewPager.getCurrentItem();
        pagerAdapter = new ProgressPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(currentPage);
    }

    private void refreshProfileUI() {
        User user = App.home2WorkClient.getUser();

        // TODO UI profilo
        //  Profile Profile = Home2WorkClient.getUserProfile();
        avatarView.setExp(user.getExp());
    }

    private void logout() {
        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.signOutUser();

        // Avvio Activity di login
        Intent i = new Intent(getContext(), SignInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private class ProgressPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Pair<Fragment, String>> fragments;

        ProgressPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            fragments.add(new Pair<>(new ProfileFragmentDashboard(), "Dashboard"));
            fragments.add(new Pair<>(new ProfileFragmentAchievements(), "Obiettivi"));
            fragments.add(new Pair<>(new ProfileFragmentStats(), "Statistiche"));

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


