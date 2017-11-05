package it.gruppoinfor.home2work.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;


public class ProgressFragmentAchievements extends Fragment {

    @BindView(R.id.achievement_recycler_view)
    RecyclerView achievementRecyclerView;
    private Unbinder unbinder;

    public ProgressFragmentAchievements() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_progress_achievements, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initUI();
        return rootView;
    }

    private void initUI() {
        // TODO lista achievements
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
