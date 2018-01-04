package it.gruppoinfor.home2work.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.MatchActivity;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2work.adapters.MatchAdapter;
import it.gruppoinfor.home2workapi.Home2WorkClient;
import it.gruppoinfor.home2workapi.model.Match;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchFragment extends Fragment implements ItemClickCallbacks {

    @BindView(R.id.matches_recycler_view)
    RecyclerView matchesRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.no_matches_view)
    TextView noMatchesView;
    private Unbinder unbinder;
    private MatchAdapter matchesAdapter;
    private MainActivity activity;

    private List<Match> matchList = new ArrayList<>();

    public MatchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        initUI();
        refreshData();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        activity = (MainActivity) context;
        super.onAttach(context);
    }

    private void initUI() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
    }

    private void refreshList() {

        boolean noMatches = false;
        for (Match match : matchList) {
            if (match.getScore() == 0) noMatches = true;
        }

        if (noMatches) noMatchesView.setVisibility(View.VISIBLE);
        else noMatchesView.setVisibility(View.GONE);

        matchList.add(0, new Match());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(matchesRecyclerView.getContext(), layoutManager.getOrientation());

        matchesRecyclerView.setLayoutManager(layoutManager);
        matchesRecyclerView.setLayoutAnimation(animation);
        //matchesRecyclerView.addItemDecoration(dividerItemDecoration);

        matchesAdapter = new MatchAdapter(getActivity(), matchList);
        matchesAdapter.notifyDataSetChanged();
        matchesRecyclerView.setAdapter(matchesAdapter);
        matchesAdapter.setItemClickCallbacks(this);
    }

    private void refreshData() {
        swipeRefreshLayout.setRefreshing(true);

        Home2WorkClient.getAPI().getMatches(Home2WorkClient.User.getId()).enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.code() == 200) {
                    matchList = response.body();
                    refreshBadgeCounter();
                    refreshList();
                } else {
                    Toasty.error(activity, "Impossibile ottenere i match").show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                Toasty.error(activity, "Impossibile ottenere i match").show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    protected void refreshBadgeCounter() {
        Integer newMatches = 0;

        for (Match match : matchList)
            if (match.isNew()) newMatches++;

        activity.setBadge(1, newMatches > 0 ? newMatches.toString() : "");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showMatchDetails(int position) {
        Match match = matchList.get(position);

        Intent matchIntent = new Intent(getContext(), MatchActivity.class);
        matchIntent.putExtra("matchID", match.getMatchID());
        startActivity(matchIntent);
    }

    private void showMatchUserProfile(int position) {
        /*
        TODO Activity profilo utente
        Intent userIntent = new Intent(activity, ShowUserActivity.class);
        User matchedUser = match.getHost();
        userIntent.putExtra("userID", matchedUser.getId());
        activity.startActivity(userIntent);*/
    }

    @Override
    public void onItemClick(View view, int position) {
        Match match = matchList.get(position);
        if (match.getScore() == 0) {
            showMatchUserProfile(position);
        } else {
            if (match.isNew()) {
                match.setNew(false);
                matchesAdapter.notifyItemChanged(position);
                refreshBadgeCounter();
            }
            showMatchDetails(position);
        }

    }

    @Override
    public boolean onLongItemClick(View view, int position) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_match_item_options, false)
                .show();

        Button showUserProfileButton = (Button) materialDialog.findViewById(R.id.show_user_profile_button);
        Button hideMatchButton = (Button) materialDialog.findViewById(R.id.hide_match_button);

        showUserProfileButton.setOnClickListener(view1 -> {
            materialDialog.dismiss();
            showMatchUserProfile(position);
        });

        hideMatchButton.setOnClickListener(view12 -> {
            materialDialog.dismiss();
            showHideMatchDialog(position);
        });

        return true;
    }

    private void showHideMatchDialog(int position) {
        Match matchItem = matchList.get(position);
        MaterialDialog hideDialog = new MaterialDialog.Builder(activity)
                .title(R.string.match_item_hide_dialog_title)
                .content(R.string.match_item_hide_dialog_content)
                .positiveText(R.string.match_item_hide_dialog_confirm)
                .negativeText(R.string.match_item_hide_dialog_cancel)
                .onPositive((dialog, which) -> {

                    matchItem.setHidden(true);
                    Home2WorkClient.getAPI().editMatch(matchItem).enqueue(new Callback<Match>() {
                        @Override
                        public void onResponse(Call<Match> call, Response<Match> response) {
                            if (response.code() == 200) {
                                matchList.remove(position);
                                matchesAdapter.remove(position);
                                Toasty.success(activity, "Match nascosto").show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Match> call, Throwable t) {

                        }
                    });
                })
                .build();

        hideDialog.show();
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


}
