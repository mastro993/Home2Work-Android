package it.gruppoinfor.home2work.fragments;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import it.gruppoinfor.home2workapi.model.Profile;
import it.gruppoinfor.home2workapi.model.ProfileStats;


public class ProgressFragmentStats extends Fragment {

    Profile profile;
    ProfileStats profileStats;
    Resources res;
    Unbinder unbinder;
    @BindView(R.id.regdate_text_view)
    TextView regdateTextView;
    @BindView(R.id.distance_text_view)
    TextView distanceTextView;
    @BindView(R.id.gas_text_view)
    TextView gasTextView;
    @BindView(R.id.emissions_text_view)
    TextView emissionsTextView;
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

        profile = Client.getUserProfile();

        ProfileStats profileStats = profile.getProfileStats();
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.ITALIAN);
        String dateString = dateFormat.format(profileStats.getRegDate());

        regdateTextView.setText(dateString);
        distanceTextView.setText(df.format(profileStats.getTotalKilometers()));
        gasTextView.setText(df.format(profileStats.getTotalGas()));
        emissionsTextView.setText(df.format(profileStats.getTotalEmissions()));
        sharesTextView.setText(profileStats.getTotalShares().toString());
        sharedDistanceTextView.setText(df.format(profileStats.getTotalSharedKilometers()));
        savedGasTextView.setText(df.format(profileStats.getTotalGasSaved()));
        savedEmissionsTextView.setText(df.format(profileStats.getTotalEmissionSaved()));
    }


    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
