package it.gruppoinfor.home2work.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.wang.avi.AVLoadingIndicatorView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import it.gruppoinfor.home2work.Converters;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.SessionManager;
import it.gruppoinfor.home2work.activities.SignInActivity;
import it.gruppoinfor.home2work.adapters.ProfileAchievementAdapter;
import it.gruppoinfor.home2work.api.APIClient;
import it.gruppoinfor.home2work.models.Achievement;
import it.gruppoinfor.home2work.models.Company;
import it.gruppoinfor.home2work.models.Karma;
import it.gruppoinfor.home2work.models.Route;
import it.gruppoinfor.home2work.models.Share;
import it.gruppoinfor.home2work.models.Statistics;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    @BindView(R.id.karma_donut_progress)
    DonutProgress karmaDonutProgress;
    @BindView(R.id.user_propic)
    CircleImageView userPropic;
    @BindView(R.id.level_container)
    ImageView levelContainer;
    @BindView(R.id.karma_level)
    TextView karmaLevel;
    @BindView(R.id.name_view)
    TextView nameView;
    @BindView(R.id.job_text_view)
    TextView jobView;
    @BindView(R.id.routes_recycler_view)
    RecyclerView routesRecyclerView;
    @BindView(R.id.routes_loading)
    AVLoadingIndicatorView routesLoading;
    @BindView(R.id.routes_more_button)
    Button routesMoreButton;
    @BindView(R.id.shares_list)
    RecyclerView sharesList;
    @BindView(R.id.shares_loading)
    AVLoadingIndicatorView sharesLoading;
    @BindView(R.id.shares_more_button)
    Button sharesMoreButton;
    @BindView(R.id.achievements_recycler_view)
    RecyclerView achievementsRecyclerView;
    @BindView(R.id.achievements_loading)
    AVLoadingIndicatorView achievementsLoading;
    @BindView(R.id.achievements_more_button)
    Button achievementsMoreButton;
    @BindView(R.id.statsTitle)
    TextView statsTitle;
    @BindView(R.id.regdate_text_view)
    TextView regdateTextView;
    @BindView(R.id.distance_text_view)
    TextView distanceTextView;
    @BindView(R.id.gas_text_view)
    TextView gasTextView;
    @BindView(R.id.emissions_text_view)
    TextView emissionsTextView;
    @BindView(R.id.shares_text_view)
    TextView sharesTextView;
    @BindView(R.id.shared_distance_text_view)
    TextView sharedDistanceTextView;
    @BindView(R.id.saved_gas_text_view)
    TextView savedGasTextView;
    @BindView(R.id.saved_emissions_text_view)
    TextView savedEmissionsTextView;
    private SwipeRefreshLayout rootView;
    private Unbinder unbinder;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        initUI();
        return rootView;
    }

    private void initUI() {
        Resources res = getResources();

        rootView.setOnRefreshListener(this::refreshData);
        rootView.setRefreshing(false);

        nameView.setText(APIClient.getAccount().toString());

        Company company = APIClient.getAccount().getJob().getCompany();

        jobView.setText(company.getName() + " (" + company.getAddress().getDistrict() + ")");

        Glide.with(getContext())
                .load(APIClient.getAccount().getAvatarURL())
                .centerCrop()
                .placeholder(R.drawable.ic_avatar_placeholder)
                .crossFade()
                .into(userPropic);

        Statistics stats = APIClient.getAccount().getStatistics();

        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.CEILING);

        regdateTextView.setText(String.format(res.getString(R.string.profile_regdate),Converters.dateToString(APIClient.getAccount().getRegistrationDate(), "dd/MM/yyyy")));
        distanceTextView.setText(String.format(res.getString(R.string.profile_distance),df.format(stats.getDistance())));
        gasTextView.setText(String.format(res.getString(R.string.profile_gas),df.format(stats.getConsumption())));
        emissionsTextView.setText(String.format(res.getString(R.string.profile_emissions),df.format(stats.getEmission())));
        sharesTextView.setText(String.format(res.getString(R.string.profile_shares),stats.getShares()));
        sharedDistanceTextView.setText(String.format(res.getString(R.string.profile_shared_distance),df.format(stats.getSharedDistance())));
        savedGasTextView.setText(String.format(res.getString(R.string.profile_saved_gas),df.format(stats.getSavedConsumption())));
        savedEmissionsTextView.setText(String.format(res.getString(R.string.profile_saved_emissions),df.format(stats.getSavedEmission())));

        Karma karma = APIClient.getAccount().getKarma();

        karmaLevel.setText(String.valueOf(karma.getLevel()));
        karmaDonutProgress.setProgress(karma.getProgress());

        rootView.setRefreshing(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());

        achievementsRecyclerView.setLayoutManager(layoutManager);
        achievementsRecyclerView.addItemDecoration(dividerItemDecoration);

        refreshData();


    }

    private void refreshData(){
        APIClient.API().getUserRoutes(APIClient.getAccount().getId()).enqueue(new Callback<List<Route>>() {
            @Override
            public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                APIClient.getAccount().setRoutes(response.body());
                //setupRouteList();
            }

            @Override
            public void onFailure(Call<List<Route>> call, Throwable t) {
                //setupRouteList();
            }

        });

        APIClient.API().getUserShares(APIClient.getAccount().getId()).enqueue(new Callback<List<Share>>() {
            @Override
            public void onResponse(Call<List<Share>> call, Response<List<Share>> response) {
                APIClient.getAccount().setShares(response.body());
                //setupShareList();
            }

            @Override
            public void onFailure(Call<List<Share>> call, Throwable t) {
                //setupShareList();
            }
        });

        APIClient.API().getUserAchievements(APIClient.getAccount().getId()).enqueue(new Callback<List<Achievement>>() {
            @Override
            public void onResponse(Call<List<Achievement>> call, Response<List<Achievement>> response) {
                achievementsLoading.setVisibility(View.GONE);
                APIClient.getAccount().setAchievements(response.body());
                ProfileAchievementAdapter profileAchievementAdapter = new ProfileAchievementAdapter(getActivity(), APIClient.getAccount().getAchievements());
                achievementsRecyclerView.setAdapter(profileAchievementAdapter);
            }

            @Override
            public void onFailure(Call<List<Achievement>> call, Throwable t) {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

 if (id == R.id.action_logout) {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());
            builder.setTitle("Logout");
            builder.setMessage("Sei sicuro di voler uscire?");
            builder.setPositiveButton("Esci", ((dialogInterface, i) -> logout()));
            builder.setNegativeButton("Cancella", null);
            builder.show();

        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SessionManager sessionManager = new SessionManager(getActivity());
        sessionManager.signOutUser();

        // Avvio Activity di login
        Intent i = new Intent(getActivity(), SignInActivity.class);
        i.putExtra(SessionManager.AUTH_CODE, SessionManager.AuthCode.SIGNED_OUT);
        startActivity(i);
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.user_propic)
    public void onUserPropicClicked() {
    }

    @OnClick(R.id.routes_more_button)
    public void onRoutesMoreButtonClicked() {
    }

    @OnClick(R.id.shares_more_button)
    public void onSharesMoreButtonClicked() {
    }

    @OnClick(R.id.achievements_more_button)
    public void onAchievementsMoreButtonClicked() {
    }
}
