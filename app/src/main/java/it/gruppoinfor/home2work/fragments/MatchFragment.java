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

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.adapters.MatchAdapter;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.models.Match;
import it.gruppoinfor.home2work.models.User;
import retrofit2.Callback;
import retrofit2.Response;


public class MatchFragment extends Fragment {

    SwipeRefreshLayout rootView;
    User user;

    @BindView(R.id.matches_recycler_view)
    RecyclerView matchRecyclerView;

    public MatchFragment() {
        // Required empty public constructor
        user = Client.getUser();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_match, container, false);
        ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        initUI();
        refreshMatches();
        return rootView;
    }

    private void initUI() {
        rootView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMatches();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        matchRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(matchRecyclerView.getContext(), layoutManager.getOrientation());
        matchRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void refreshMatches() {
        rootView.setRefreshing(true);
        Client.getAPI().getUserMatches(user.getId()).enqueue(new Callback<List<Match>>() {

            @Override
            public void onResponse(retrofit2.Call<List<Match>> call, Response<List<Match>> response) {
                rootView.setRefreshing(false);
                Client.getUser().setMatches(response.body());
                populateList();
            }

            @Override
            public void onFailure(retrofit2.Call<List<Match>> call, Throwable t) {
                rootView.setRefreshing(false);
                Toasty.warning(getContext(), "Impossibile aggiornare lista match al momento, riprova più tardi").show();
            }

        });
    }

    private void populateList() {
        List<Match> matchList = Client.getUser().getMatches();

        MatchAdapter matchesAdapter = new MatchAdapter(getActivity(), matchList);

        matchRecyclerView.setAdapter(matchesAdapter);

    }

}
