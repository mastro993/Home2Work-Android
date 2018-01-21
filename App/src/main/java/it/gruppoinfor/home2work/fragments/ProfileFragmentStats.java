package it.gruppoinfor.home2work.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;


public class ProfileFragmentStats extends Fragment {

    @BindView(R.id.regdate_text_view)
    TextView regdateTextView;
    @BindView(R.id.shares_text_view)
    TextView sharesTextView;
    @BindView(R.id.shared_distance_text_view)
    TextView sharedDistanceTextView;
    @BindView(R.id.saved_gas_text_view)
    TextView savedGasTextView;
    @BindView(R.id.saved_emissions_text_view)
    TextView savedEmissionsTextView;
    private Unbinder mUnbinder;

    public ProfileFragmentStats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NestedScrollView root = (NestedScrollView) inflater.inflate(R.layout.fragment_profile_stats, container, false);
        mUnbinder = ButterKnife.bind(this, root);
        initUI();
        return root;
    }

    private void initUI() {

    }


    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }
}
