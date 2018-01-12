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
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.App;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.MatchActivity;
import it.gruppoinfor.home2work.activities.SettingsActivity;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2work.adapters.MatchAdapter;
import it.gruppoinfor.home2workapi.model.Match;

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
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        refreshData();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        activity = (MainActivity) context;
        super.onAttach(context);
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
            Intent matchIntent = new Intent(getContext(), MatchActivity.class);
            matchIntent.putExtra("match", matchList.get(position));
            startActivity(matchIntent);
        }
    }

    @Override
    public boolean onLongItemClick(View view, int position) {

        String[] options = new String[]{"Mostra profilo utente", "Nascondi"};

        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .items(options)
                .itemsCallback((dialog, itemView, p, text) -> {
                    switch (p) {
                        case 0:
                            showMatchUserProfile(position);
                            break;
                        case 1:
                            startActivity(new Intent(getActivity(), SettingsActivity.class));
                            break;
                        case 2:
                            showHideMatchDialog(position);
                            break;
                    }
                })
                .show();


        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void refreshData() {
        swipeRefreshLayout.setRefreshing(true);

        App.home2WorkClient.getUserMatches(matches -> {
            matchList = matches;
            refreshBadgeCounter();
            refreshList();
            swipeRefreshLayout.setRefreshing(false);
        }, e -> {
            Toasty.error(activity, "Impossibile ottenere i match").show();
            swipeRefreshLayout.setRefreshing(false);
        });

    }

    private void refreshList() {

        boolean noMatches = false;
        for (Match match : matchList) {
            if (match.getScore() == 0) noMatches = true;
        }

        if (noMatches) noMatchesView.setVisibility(View.VISIBLE);
        else noMatchesView.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);

        matchesRecyclerView.setLayoutManager(layoutManager);
        matchesRecyclerView.setLayoutAnimation(animation);

        matchesAdapter = new MatchAdapter(getActivity(), matchList);
        matchesAdapter.notifyDataSetChanged();
        matchesRecyclerView.setAdapter(matchesAdapter);
        matchesAdapter.setItemClickCallbacks(this);
    }

    protected void refreshBadgeCounter() {
        Integer newMatches = 0;

        for (Match match : matchList)
            if (match.isNew()) newMatches++;

        activity.setBadge(1, newMatches > 0 ? newMatches.toString() : "");
    }

    private void showMatchUserProfile(int position) {
        /*
        TODO Activity profilo utente
        Intent userIntent = new Intent(activity, ShowUserActivity.class);
        User matchedUser = match.getHost();
        userIntent.putExtra("userID", matchedUser.getId());
        activity.startActivity(userIntent);*/
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

                    App.home2WorkClient.editMatch(matchItem, match -> {
                        matchList.remove(position);
                        matchesAdapter.remove(position);
                        Toasty.success(activity, "Match nascosto").show();
                    }, e -> Toasty.success(activity, "Non è possibile nascondere il match al momento").show());


                })
                .build();

        hideDialog.show();
    }


}
