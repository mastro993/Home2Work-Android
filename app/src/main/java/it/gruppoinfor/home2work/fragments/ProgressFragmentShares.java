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
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2work.adapters.SharesAdapter;
import it.gruppoinfor.home2workapi.Client;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgressFragmentShares extends Fragment implements ItemClickCallbacks {


    @BindView(R.id.shares_recycler_view)
    RecyclerView sharesRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    private Unbinder unbinder;
    private SharesAdapter sharesAdapter;

    public ProgressFragmentShares() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_progress_shares, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initUI();
        return rootView;
    }

    private void initUI() {
        if (Client.getUserShares().size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            sharesRecyclerView.setVisibility(View.GONE);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);

            sharesRecyclerView.setLayoutManager(layoutManager);
            sharesRecyclerView.setLayoutAnimation(animation);

            sharesAdapter = new SharesAdapter(getActivity(), Client.getUserShares());
            sharesAdapter.setItemClickCallbacks(this);
            sharesRecyclerView.setAdapter(sharesAdapter);
        }

    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public boolean onLongItemClick(View view, int position) {
        return false;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

}
