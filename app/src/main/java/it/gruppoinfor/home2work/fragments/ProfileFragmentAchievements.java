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

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.adapters.AchievementAdapter;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;

// TODO dialog informazioni obiettivo
public class ProfileFragmentAchievements extends Fragment implements ItemClickCallbacks {

    @BindView(R.id.achievement_recycler_view)
    RecyclerView achievementRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    private Unbinder unbinder;
    private AchievementAdapter achievementAdapter;

    public ProfileFragmentAchievements() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_achievements, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initUI();
        return rootView;
    }

    private void initUI() {

        if (ProfileFragment.AchievementList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            achievementRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            achievementRecyclerView.setVisibility(View.VISIBLE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);

            achievementRecyclerView.setLayoutManager(layoutManager);
            achievementRecyclerView.setLayoutAnimation(animation);

            Collections.sort(ProfileFragment.AchievementList, (A, B) -> {
                int result = B.getProgress().compareTo(A.getProgress());

                if (result == 0) {
                    result = A.getUnlockDate().compareTo(B.getUnlockDate());
                }

                if (result == 0) {
                    result = A.getName().compareTo(B.getName());
                }

                return result;
            });

            achievementAdapter = new AchievementAdapter(getActivity(), ProfileFragment.AchievementList);
            achievementAdapter.setItemClickCallbacks(this);
            achievementRecyclerView.setAdapter(achievementAdapter);
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
