package it.gruppoinfor.home2work.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.MatchActivity;
import it.gruppoinfor.home2work.adapters.BookingAdapter;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2work.adapters.MatchAdapter;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.Mockup;
import it.gruppoinfor.home2workapi.model.Match;


public class MatchFragment extends Fragment {

    @BindView(R.id.matches_recycler_view)
    RecyclerView matchRecyclerView;
    @BindView(R.id.booked_matches_recycler_view)
    RecyclerView bookedMatchRecyclerView;
    private SwipeRefreshLayout rootView;
    private Unbinder unbinder;
    private MatchAdapter matchesAdapter;
    private BookingAdapter bookedMatchAdapter;
    private boolean refreshingMatches = false;
    private boolean refreshingBookedMatches = false;

    public MatchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_match, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        initUI();
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (Client.getUserMatches() == null) refreshMatches();
            else populateMatchList();
            if (Client.getUserBookedMatches() == null) refreshBookedMatches();
            else populateBookedMatchList();
        }
    }

    private void initUI() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(matchRecyclerView.getContext(), layoutManager.getOrientation());
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);

        matchRecyclerView.setLayoutManager(layoutManager);
        matchRecyclerView.addItemDecoration(dividerItemDecoration);
        matchRecyclerView.setNestedScrollingEnabled(false);
        matchRecyclerView.setLayoutAnimation(animation);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(bookedMatchRecyclerView.getContext(), layoutManager.getOrientation());

        bookedMatchRecyclerView.setLayoutManager(layoutManager2);
        bookedMatchRecyclerView.addItemDecoration(dividerItemDecoration2);
        bookedMatchRecyclerView.setNestedScrollingEnabled(false);
        bookedMatchRecyclerView.setLayoutAnimation(animation);

        rootView.setOnRefreshListener(this::refreshMatches);
        rootView.setColorSchemeResources(R.color.colorAccent);
    }

    private void refreshMatches() {
        rootView.setRefreshing(true);
        refreshingMatches = true;

        // TODO fare refresh da web
        Mockup.refreshUserMatches(matchItems -> {
            Client.setUserMatches(matchItems);
            refreshBadge();
            populateMatchList();
            refreshingMatches = false;
            rootView.setRefreshing(refreshingBookedMatches || refreshingMatches);
        });

    }

    private void refreshBookedMatches() {
        rootView.setRefreshing(true);
        refreshingBookedMatches = true;

        // TODO fare refresh da web
        Mockup.refreshUserBookedMatches(bookedMatchItems -> {
            Client.setUserBookedMatches(bookedMatchItems);
            populateBookedMatchList();
            refreshingBookedMatches = false;
            rootView.setRefreshing(refreshingBookedMatches || refreshingMatches);
        });
    }

    private void refreshBadge() {
        Stream<Match> matchStream = Client.getUserMatches().stream();
        long newMatches = matchStream.filter(m -> !m.isHidden()).filter(Match::isNew).count();

        if (newMatches > 0) {
            ((MainActivity) getActivity()).bottomNavigation.setNotification(Long.toString(newMatches), 1);
        } else {
            ((MainActivity) getActivity()).bottomNavigation.setNotification("", 1);
        }
    }

    private void populateMatchList() {

        matchesAdapter = new MatchAdapter(getActivity(), Client.getUserMatches());
        matchesAdapter.setItemClickCallbacks(new ItemClickCallbacks() {
            @Override
            public void onItemClick(View view, int position) {
                Match matchItem = Client.getUserMatches().get(position);
                if (matchItem.isNew()) {
                    matchItem.setNew(false);
                    matchesAdapter.notifyItemChanged(position);
                    setMatchAsViewed(matchItem);
                    refreshBadge();
                }
                showMatchDetails(position);
            }

            @Override
            public boolean onLongItemClick(View view, int position) {
                PopupMenu popup = new PopupMenu(getActivity(), view);
                popup.getMenuInflater().inflate(R.menu.menu_match, popup.getMenu());
                popup.setOnMenuItemClickListener((item) -> {
                    switch (item.getItemId()) {
                        case R.id.show_match_details:
                            showMatchDetails(position);
                            break;
                        case R.id.show_match_profile:
                            showMatchUserProfile(position);
                            break;
                        case R.id.hide_match:
                            showHideMatchDialog(position);
                            break;
                        default:
                            break;
                    }
                    return true;
                });
                popup.show();
                return true;
            }
        });

        matchRecyclerView.setAdapter(matchesAdapter);

    }

    private void populateBookedMatchList() {
        bookedMatchAdapter = new BookingAdapter(getActivity(), Client.getUserBookedMatches());
        bookedMatchAdapter.setItemClickCallbacks(new ItemClickCallbacks() {
            @Override
            public void onItemClick(View view, int position) {
                showBookedMatchDetails(position);
            }

            @Override
            public boolean onLongItemClick(View view, int position) {
                PopupMenu popup = new PopupMenu(getActivity(), view);
                popup.getMenuInflater().inflate(R.menu.menu_booked_match, popup.getMenu());
                popup.setOnMenuItemClickListener((item) -> {
                    switch (item.getItemId()) {
                        case R.id.show_booked_match_details:
                            showMatchDetails(position);
                            break;
                        case R.id.show_booked_match_profile:
                            showBookedMatchUserProfile(position);
                            break;
                        case R.id.delete_booked_match:
                            showDeleteBookedMatchDialog(position);
                            break;
                        default:
                            break;
                    }
                    return true;
                });
                popup.show();
                return true;
            }
        });
        bookedMatchRecyclerView.setAdapter(bookedMatchAdapter);
    }

    private void setMatchAsViewed(Match match) {

        /*
        TODO match come visualizzato al server
        Client.getAPI().editMatch(matchItem).enqueue(new SessionManagerCallback<MatchInfo>() {
            @Override
            public void onResponse(Call<MatchInfo> call, Response<MatchInfo> response) {
                Stream<MatchInfo> matchStream = matches.stream();
                long newMatches = matchStream.filter(MatchInfo::isNew).count();

                if (newMatches > 0) {
                    activity.bottomNavigation.setNotification(Long.toString(newMatches), 1);
                }

                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MatchInfo> call, Throwable t) {

            }
        });*/

    }

    private void setMatchAsHidden(Match matchItem) {

        /*
        TODO nascondere match newl server

        Client.getAPI().editMatch(matchItem).enqueue(new SessionManagerCallback<MatchInfo>() {
            @Override
            public void onResponse(Call<MatchInfo> call, Response<MatchInfo> response) {
                matches.remove(position);
                notifyItemRemoved(position);
                Toasty.success(activity, res.getString(R.string.match_item_hided), Toast.LENGTH_SHORT, true).show();
            }

            @Override
            public void onFailure(Call<MatchInfo> call, Throwable t) {
                Toasty.error(activity, res.getString(R.string.match_item_hide_error)).show();
                t.printStackTrace();

            }
        });*/
    }

    private void showMatchDetails(int position) {
        Match match = Client.getUserMatches().get(position);

        Intent matchIntent = new Intent(getContext(), MatchActivity.class);
        matchIntent.putExtra("matchID", match.getMatchID());
        startActivity(matchIntent);

    }

    private void showBookedMatchDetails(int position) {
        /*
        TODO Activity info Booked Match
        Booking matchItem = matches.get(position);

        if (match.isNew()) setMatchAsViewed(position);
        Intent matchIntent = new Intent(activity, MatchActivity.class);
        matchIntent.putExtra("matchID", matchItem.getMatchID());
        activity.startActivity(matchIntent);*/

    }

    private void showHideMatchDialog(int position) {
        Match matchItem = Client.getUserMatches().get(position);
        MaterialDialog hideDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.match_item_hide_dialog_title)
                .content(R.string.match_item_hide_dialog_content)
                .positiveText(R.string.match_item_hide_dialog_confirm)
                .negativeText(R.string.match_item_hide_dialog_cancel)
                .onPositive((dialog, which) -> {
                    Client.getUserMatches().remove(position);
                    matchesAdapter.remove(position);
                    setMatchAsHidden(matchItem);
                    refreshBadge();
                })
                .build();

        hideDialog.show();
    }

    private void showDeleteBookedMatchDialog(int position) {
        /*
        TODO richiesta annullamento prenotazione
        Match matchItem = Client.getUserMatches().get(position);
        MaterialDialog hideDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.match_item_hide_dialog_title)
                .content(R.string.match_item_hide_dialog_content)
                .positiveText(R.string.match_item_hide_dialog_confirm)
                .negativeText(R.string.match_item_hide_dialog_cancel)
                .onPositive((dialog, which) -> {
                    Client.getUserMatches().remove(position);
                    matchesAdapter.remove(position);
                    setMatchAsHidden(matchItem);
                    refreshBadge();
                })
                .build();

        hideDialog.show();*/
    }

    private void showMatchUserProfile(int position) {
        /*
        TODO Activity info utente
        Intent userIntent = new Intent(activity, ShowUserActivity.class);
        User matchedUser = match.getHost();
        userIntent.putExtra("userID", matchedUser.getId());
        activity.startActivity(userIntent);*/
    }

    private void showBookedMatchUserProfile(int position) {
        /*
        TODO Activity info utente
        Intent userIntent = new Intent(activity, ShowUserActivity.class);
        User matchedUser = match.getHost();
        userIntent.putExtra("userID", matchedUser.getId());
        activity.startActivity(userIntent);*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
