package it.gruppoinfor.home2work.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.MatchActivity;
import it.gruppoinfor.home2work.activities.SettingsActivity;
import it.gruppoinfor.home2work.activities.ShowUserActivity;
import it.gruppoinfor.home2work.adapters.MatchAdapter;
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.model.Match;
import it.gruppoinfor.home2workapi.model.User;

public class MatchFragment extends Fragment implements ItemClickCallbacks {

    @BindView(R.id.matches_recycler_view)
    RecyclerView matchesRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.no_matches_view)
    TextView noMatchesView;
    private Unbinder mUnbinder;
    private MatchAdapter matchesAdapter;
    private Context mContext;

    private List<Match> matchList = new ArrayList<>();

    public MatchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            refreshData();
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
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

        new MaterialDialog.Builder(mContext)
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
        mUnbinder.unbind();
    }

    private void refreshData() {
        HomeToWorkClient.getInstance().getUserMatches(matches -> {
            matchList = matches;
            refreshBadgeCounter();
            refreshList();
            swipeRefreshLayout.setRefreshing(false);
        }, e -> {
            //Toasty.error(activity, "Impossibile ottenere i match").show();
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

        ((MainActivity) mContext).setBadge(1, newMatches > 0 ? newMatches.toString() : "");
    }

    private void showMatchUserProfile(int position) {
        Intent userIntent = new Intent(getActivity(), ShowUserActivity.class);
        User matchedUser = matchList.get(position).getHost();
        userIntent.putExtra("user", matchedUser);
        startActivity(userIntent);
    }

    private void showHideMatchDialog(int position) {
        Match matchItem = matchList.get(position);
        MaterialDialog hideDialog = new MaterialDialog.Builder(mContext)
                .title(R.string.item_match_dialog_hide_title)
                .content(R.string.item_match_dialog_hide_content)
                .positiveText(R.string.item_match_dialog_hide_confirm)
                .negativeText(R.string.item_match_dialog_hide_cancel)
                .onPositive((dialog, which) -> {

                    matchItem.setHidden(true);

                    HomeToWorkClient.getInstance().editMatch(matchItem, match -> {
                        matchList.remove(position);
                        matchesAdapter.remove(position);
                    }, e -> Toasty.success(mContext, mContext.getString(R.string.item_match_dialog_hide_error)).show());


                })
                .build();

        hideDialog.show();
    }


}
