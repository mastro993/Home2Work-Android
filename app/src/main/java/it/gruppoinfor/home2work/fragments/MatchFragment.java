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

import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.adapters.BookedMatchAdapter;
import it.gruppoinfor.home2work.adapters.MatchAdapter;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.api.Mockup;
import it.gruppoinfor.home2work.models.MatchItem;


public class MatchFragment extends Fragment {

    @BindView(R.id.matches_recycler_view)
    RecyclerView matchRecyclerView;
    private SwipeRefreshLayout rootView;
    private Unbinder unbinder;

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
        if (isVisibleToUser) populateList();
    }

    private void initUI() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        matchRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(matchRecyclerView.getContext(), layoutManager.getOrientation());
        matchRecyclerView.addItemDecoration(dividerItemDecoration);

        rootView.setOnRefreshListener(this::refreshMatches);

        rootView.setColorSchemeResources(R.color.colorAccent);
    }

    private void refreshMatches() {
        rootView.setRefreshing(true);

        // TODO fare refresh da web
        Mockup.refreshUserMatches(matchItems -> {
            rootView.setRefreshing(false);
            Client.setUserMatches(matchItems);

            Stream<MatchItem> matchStream = matchItems.stream();
            long newMatches = matchStream.filter(MatchItem::isNew).count();

            if (newMatches > 0) {
                ((MainActivity) getActivity()).bottomNavigation.setNotification(Long.toString(newMatches), 1);
            }

            populateList();
        });

        // TODO fare refresh da web
        Mockup.refreshUserBookedMatches(bookedMatchItems -> {
            Client.setUserBookedMatches(bookedMatchItems);

            //BookedMatchAdapter bookedMatchAdapter = new BookedMatchAdapter(getActivity(), bookedMatchItems);
            //bookedMatchesRecyclerView.setAdapter(bookedMatchAdapter);

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void populateList() {

        MatchAdapter matchesAdapter = new MatchAdapter(getActivity(), Client.getUserMatches());
        matchRecyclerView.setAdapter(matchesAdapter);

    }

}
