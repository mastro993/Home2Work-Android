package it.gruppoinfor.home2work.fragments;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.UserStatistics;


public class ProgressFragmentStats extends Fragment {

    Resources res;
    Unbinder unbinder;
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

    public ProgressFragmentStats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NestedScrollView root = (NestedScrollView) inflater.inflate(R.layout.fragment_progress_stats, container, false);
        unbinder = ButterKnife.bind(this, root);
        res = getResources();
        initUI();
        return root;
    }

    private void initUI() {

        UserStatistics statistics = ProgressFragment.Profile.getStats();

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.ITALIAN);
        String dateString = dateFormat.format(Client.User.getRegistrationDate());

        regdateTextView.setText(dateString);
        sharesTextView.setText(statistics.getShares().toString());
        sharedDistanceTextView.setText(df.format(statistics.getSharedKilometers()));
        savedGasTextView.setText(df.format(statistics.getGasSaved()));
        savedEmissionsTextView.setText(df.format(statistics.getEmissionSaved()));
    }


    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
