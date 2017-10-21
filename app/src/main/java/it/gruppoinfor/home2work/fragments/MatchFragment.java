package it.gruppoinfor.home2work.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.UserPrefs;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.adapters.MatchAdapter;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.models.Match;
import it.gruppoinfor.home2work.models.MatchItem;
import retrofit2.Callback;
import retrofit2.Response;


public class MatchFragment extends Fragment {

    @BindView(R.id.matches_recycler_view)
    RecyclerView matchRecyclerView;
    private SwipeRefreshLayout rootView;
    private Unbinder unbinder;

    private List<MatchItem> matches;

    public MatchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_match, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        initUI();
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
// TODO        if (isVisibleToUser) populateList();
    }

    private void initUI() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        matchRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(matchRecyclerView.getContext(), layoutManager.getOrientation());
        matchRecyclerView.addItemDecoration(dividerItemDecoration);

        //TODO rootView.setOnRefreshListener(this::refreshMatches);

        rootView.setColorSchemeResources(R.color.colorAccent);
    }

    private void refreshMatches() {
        rootView.setRefreshing(true);
        Client.getAPI().getUserMatches(Client.getSignedUser().getId()).enqueue(new Callback<List<MatchItem>>() {

            @Override
            public void onResponse(retrofit2.Call<List<MatchItem>> call, Response<List<MatchItem>> response) {
                rootView.setRefreshing(false);


                matches = response.body();

                Stream<MatchItem> matchStream = response.body().stream();
                long newMatches = matchStream.filter(m -> m.isNew()).count();

                if (newMatches > 0) {
                    ((MainActivity) getActivity()).bottomNavigation.setNotification(Long.toString(newMatches), 1);
                }

                populateList();


            }

            @Override
            public void onFailure(retrofit2.Call<List<MatchItem>> call, Throwable t) {
                rootView.setRefreshing(false);
                Toasty.warning(getContext(), getResources().getString(R.string.match_loading_error)).show();
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void populateList() {

        MatchAdapter matchesAdapter = new MatchAdapter(getActivity(), matches);
        matchRecyclerView.setAdapter(matchesAdapter);

    }

}
