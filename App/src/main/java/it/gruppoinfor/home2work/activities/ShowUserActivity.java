package it.gruppoinfor.home2work.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.custom.AppBarStateChangeListener;
import it.gruppoinfor.home2work.custom.AvatarView;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.model.User;

public class ShowUserActivity extends AppCompatActivity {

    public static final String EXTRA_USER = "user";

    @BindView(R.id.text_name_small)
    TextView textNameSmall;
    @BindView(R.id.toolbar_layout)
    LinearLayout toolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.avatar_view)
    AvatarView avatarView;
    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.job_text_view)
    TextView jobTextView;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_USER)) {
            mUser = (User) intent.getSerializableExtra(EXTRA_USER);
            initUI();
        } else {
            Toasty.error(this, getString(R.string.activity_show_user_error)).show();
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        avatarView.setAvatarURL(mUser.getAvatarURL());
        avatarView.setExp(mUser.getExp());
        nameTextView.setText(mUser.toString());
        jobTextView.setText(mUser.getCompany().toString());
        textNameSmall.setText(mUser.toString());

    }

    private void refreshData() {
        swipeRefreshLayout.setRefreshing(true);

        HomeToWorkClient.getInstance().getUser(mUser.getId(), aVoid -> {
            swipeRefreshLayout.setRefreshing(false);
            initAvatarView();
        }, e -> {
            Toasty.error(this, "Non è possibile aggiornare le informazioni dell'utente al momento").show();
            initAvatarView();
        });

    }

    private void initAvatarView() {

        // TODO UI profilo
        //  Profile Profile = Home2WorkClient.getUserProfile();
        avatarView.setExp(mUser.getExp());

        swipeRefreshLayout.setRefreshing(false);
    }
}
