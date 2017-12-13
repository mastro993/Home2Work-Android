package it.gruppoinfor.home2work.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2work.adapters.OngoingSharesAdapter;
import it.gruppoinfor.home2work.dialogs.OngoingShareInfoDialog;
import it.gruppoinfor.home2workapi.model.Share;

public class ProgressFragmentDashboard extends Fragment {


    @BindView(R.id.ongoing_shares_list)
    RecyclerView ongoingSharesList;
    private Unbinder unbinder;
    private OngoingSharesAdapter ongoingSharesAdapter;
    private ArrayList<Share> ongoingShares;

    public ProgressFragmentDashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout root = (FrameLayout) inflater.inflate(R.layout.fragment_progress_dashboard, container, false);
        unbinder = ButterKnife.bind(this, root);
        initUI();
        return root;
    }

    private void initUI() {
        initOngoingSharesList();

    }

    private void initOngoingSharesList() {
        ongoingShares = new ArrayList<>();
        for (Share s : ProgressFragment.ShareList)
            if (s.getStatus() != Share.COMPLETED) ongoingShares.add(s);

        if (ongoingShares.size() != 0) {
            ongoingSharesList.setVisibility(View.VISIBLE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);

            ongoingSharesList.setLayoutManager(layoutManager);
            ongoingSharesList.setLayoutAnimation(animation);

            ongoingSharesAdapter = new OngoingSharesAdapter(getActivity(), ongoingShares);
            ongoingSharesAdapter.setItemClickCallbacks(ongoingSharesClickCallbacks);
            ongoingSharesList.setAdapter(ongoingSharesAdapter);
        } else {
            ongoingSharesList.setVisibility(View.GONE);
        }
    }

    private ItemClickCallbacks ongoingSharesClickCallbacks = new ItemClickCallbacks() {
        @Override
        public void onItemClick(View view, int position) {
            Share share = ongoingShares.get(position);
            new OngoingShareInfoDialog(getActivity(), share).show();
        }

        @Override
        public boolean onLongItemClick(View view, int position) {
            return false;
        }
    };

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

}
