package it.gruppoinfor.home2work.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.MatchActivity;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2work.adapters.MatchAdapter;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Match;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchFragmentList extends Fragment implements ItemClickCallbacks {

    @BindView(R.id.matches_recycler_view)
    RecyclerView matchesRecyclerView;
    private Unbinder unbinder;
    private MatchAdapter matchesAdapter;

    public MatchFragmentList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initUI();
        return rootView;
    }

    private void initUI() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);

        matchesRecyclerView.setLayoutManager(layoutManager);
        matchesRecyclerView.setLayoutAnimation(animation);

        matchesAdapter = new MatchAdapter(getActivity(), Client.getUserMatches());
        matchesAdapter.setItemClickCallbacks(this);
        matchesRecyclerView.setAdapter(matchesAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Match matchItem = Client.getUserMatches().get(position);
        if (matchItem.isNew()) {
            matchItem.setNew(false);
            matchesAdapter.notifyItemChanged(position);
            ((MainActivity) getActivity()).refreshMatchTabBadge();
        }
        showMatchDetails(position);
    }

    @Override
    public boolean onLongItemClick(View view, int position) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.getMenuInflater().inflate(R.menu.menu_match, popup.getMenu());
        popup.setOnMenuItemClickListener((item) -> {
            switch (item.getItemId()) {
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

    private void showMatchDetails(int position) {
        Match match = Client.getUserMatches().get(position);

        Intent matchIntent = new Intent(getContext(), MatchActivity.class);
        matchIntent.putExtra("matchID", match.getMatchID());
        startActivityForResult(matchIntent, MatchActivity.NEW_BOOKIG_REQUEST);
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
        Match matchItem = Client.getUserMatches().get(position);
        MaterialDialog hideDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.match_item_hide_dialog_title)
                .content(R.string.match_item_hide_dialog_content)
                .positiveText(R.string.match_item_hide_dialog_confirm)
                .negativeText(R.string.match_item_hide_dialog_cancel)
                .onPositive((dialog, which) -> {

                    matchItem.setHidden(true);
                    Client.getAPI().editMatch(matchItem).enqueue(new Callback<Match>() {
                        @Override
                        public void onResponse(Call<Match> call, Response<Match> response) {
                            if (response.code() == 200) {
                                Client.getUserMatches().remove(position);
                                matchesAdapter.remove(position);
                                Toasty.success(getContext(), "Match nascosto").show();
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
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

}
