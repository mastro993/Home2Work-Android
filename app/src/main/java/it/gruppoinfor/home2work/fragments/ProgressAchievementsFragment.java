package it.gruppoinfor.home2work.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgressAchievementsFragment extends Fragment {

    @BindView(R.id.achievement_recycler_view)
    RecyclerView achievementRecyclerView;
    @BindView(R.id.empty_view)
    RelativeLayout emptyView;
    private Unbinder unbinder;

    public ProgressAchievementsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout root = (FrameLayout) inflater.inflate(R.layout.fragment_progress_achievements, container, false);
        unbinder = ButterKnife.bind(this, root);
        initUI();
        return root;
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
