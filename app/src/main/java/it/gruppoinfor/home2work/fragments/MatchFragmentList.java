package it.gruppoinfor.home2work.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.MatchActivity;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2work.adapters.MatchAdapter;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Match;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchFragmentList extends Fragment {


    @BindView(R.id.matches_recycler_view)
    RecyclerView matchesRecyclerView;
    @BindView(R.id.empty_view)
    RelativeLayout emptyView;
    private Unbinder unbinder;
    private MatchAdapter matchesAdapter;

    public MatchFragmentList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout rootView = (FrameLayout) inflater.inflate(R.layout.fragment_match_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initUI();
        return rootView;
    }

    private void initUI() {
        if (Client.getUserMatches() != null && Client.getUserMatches().size() > 0) {
            emptyView.setVisibility(View.GONE);
            matchesRecyclerView.setVisibility(View.VISIBLE);
            initMatchList();
        } else {
            emptyView.setVisibility(View.VISIBLE);
            matchesRecyclerView.setVisibility(View.GONE);
        }
    }

    private void initMatchList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(matchesRecyclerView.getContext(), layoutManager.getOrientation());
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);

        matchesRecyclerView.setLayoutManager(layoutManager);
        matchesRecyclerView.addItemDecoration(dividerItemDecoration);
        matchesRecyclerView.setNestedScrollingEnabled(false);
        matchesRecyclerView.setLayoutAnimation(animation);

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

    private void refreshBadge() {
        Stream<Match> matchStream = Client.getUserMatches().stream();
        long newMatches = matchStream.filter(m -> !m.isHidden()).filter(Match::isNew).count();

        if (newMatches > 0) {
            ((MainActivity) getActivity()).bottomNavigation.setNotification(Long.toString(newMatches), 1);
        } else {
            ((MainActivity) getActivity()).bottomNavigation.setNotification("", 1);
        }
    }

    private void showMatchDetails(int position) {
        Match match = Client.getUserMatches().get(position);

        Intent matchIntent = new Intent(getContext(), MatchActivity.class);
        matchIntent.putExtra("matchID", match.getMatchID());
        startActivity(matchIntent);

    }

    private void showMatchUserProfile(int position) {
        /*
        TODO Activity info utente
        Intent userIntent = new Intent(activity, ShowUserActivity.class);
        User matchedUser = match.getHost();
        userIntent.putExtra("userID", matchedUser.getId());
        activity.startActivity(userIntent);*/
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

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
