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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.adapters.AchievementAdapter;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Achievement;

// TODO dialog informazioni obiettivo
public class ProgressFragmentAchievements extends Fragment implements ItemClickCallbacks {

    @BindView(R.id.achievement_recycler_view)
    RecyclerView achievementRecyclerView;
    private Unbinder unbinder;
    private AchievementAdapter achievementAdapter;

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);

        achievementRecyclerView.setLayoutManager(layoutManager);
        achievementRecyclerView.setLayoutAnimation(animation);

        List<Achievement> achievements = Client.getUserProfile().getAchievements();

        Collections.sort(achievements, (A, B) -> {
            int result = B.getProgress().compareTo(A.getProgress());

            if (result == 0) {
                result = A.getUnlockDate().compareTo(B.getUnlockDate());
            }

            if (result == 0) {
                result = A.getName().compareTo(B.getName());
            }

            return result;
        });

        achievementAdapter = new AchievementAdapter(getActivity(), achievements);
        achievementAdapter.setItemClickCallbacks(this);
        achievementRecyclerView.setAdapter(achievementAdapter);
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
