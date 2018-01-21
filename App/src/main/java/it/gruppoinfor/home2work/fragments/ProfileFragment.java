package it.gruppoinfor.home2work.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.EditProfileActivity;
import it.gruppoinfor.home2work.activities.SettingsActivity;
import it.gruppoinfor.home2work.activities.SignInActivity;
import it.gruppoinfor.home2work.custom.AppBarStateChangeListener;
import it.gruppoinfor.home2work.custom.AvatarView;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.model.User;


public class ProfileFragment extends Fragment implements ViewPager.OnPageChangeListener {

    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.job_text_view)
    TextView jobTextView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.avatar_view)
    AvatarView avatarView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.text_name_small)
    TextView textNameSmall;
    @BindView(R.id.text_company_small)
    TextView textCompanySmall;
    @BindView(R.id.toolbar_layout)
    LinearLayout toolbarLayout;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    private Unbinder mUnbinder;
    private Context mContext;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        setHasOptionsMenu(true);
        initUI();
        refreshData();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_match, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // TODO sort e filter match
            default:
                return super.onOptionsItemSelected(item);
        }
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
        mUnbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.profile_options_button)
    public void onViewClicked() {

        String[] options = new String[]{"Modifica profilo", "Impostazioni", "Esci"};

        new MaterialDialog.Builder(mContext)
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.dialog_logout_title);
                            builder.setMessage(R.string.dialog_logout_content);
                            builder.setPositiveButton(R.string.dialog_logout_confirm, ((dialogInterface, i) -> logout()));
                            builder.setNegativeButton(R.string.dialog_logout_decline, null);
                            builder.show();
                            break;
                    }
                })
                .show();

    }

    private void initUI() {
        appBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {

                switch (state) {
                    case COLLAPSED:
                        if (toolbarLayout.getAlpha() < 1.0f) {
                            toolbarLayout.setVisibility(View.VISIBLE);
                            toolbarLayout.animate()
                                    //.translationY(toolbarLayout.getHeight())
                                    .alpha(1.0f)
                                    .setListener(null);
                        }
                        break;
                    case IDLE:
                        if (toolbarLayout.getAlpha() > 0.0f) {
                            toolbarLayout.animate()
                                    .translationY(0)
                                    .alpha(0.0f)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            toolbarLayout.setVisibility(View.GONE);
                                        }
                                    });

                        }
                }

                Log.d("STATE", state.name());
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        User user = HomeToWorkClient.getUser();
        avatarView.setAvatarURL(user.getAvatarURL());
        nameTextView.setText(user.toString());
        jobTextView.setText(user.getCompany().toString());
        textNameSmall.setText(user.toString());
        textCompanySmall.setText(user.getCompany().toString());
    }

    private void refreshData() {
        swipeRefreshLayout.setRefreshing(true);

        HomeToWorkClient.getInstance().refreshUser(aVoid -> {
            swipeRefreshLayout.setRefreshing(false);
            refreshProfileUI();
        }, e -> {
            Toasty.error(mContext, "Non è possibile aggiornare le informazioni dell'utente al momento").show();
            refreshProfileUI();
        });

    }

    private void refreshProfileUI() {
        User user = HomeToWorkClient.getUser();

        // TODO UI profilo
        //  Profile Profile = Home2WorkClient.getUserProfile();
        avatarView.setExp(user.getExp());

        swipeRefreshLayout.setRefreshing(false);
    }

    private void logout() {
        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.signOutUser();

        // Avvio Activity di login
        Intent i = new Intent(getContext(), SignInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

}


