package it.gruppoinfor.home2work.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.gruppoinfor.home2work.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgressFragment extends Fragment {


    public ProgressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    private void initUI() {

        /*
        Resources res = getResources();

        Statistics stats = user.getStatistics();

        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.CEILING);

        regdateTextView.setText(String.format(res.getString(R.string.profile_regdate), Converters.dateToString(user.getRegistrationDate(), "dd/MM/yyyy")));
        distanceTextView.setText(String.format(res.getString(R.string.profile_distance), df.format(stats.getDistance())));
        gasTextView.setText(String.format(res.getString(R.string.profile_gas), df.format(stats.getConsumption())));
        emissionsTextView.setText(String.format(res.getString(R.string.profile_emissions), df.format(stats.getEmission())));
        sharesTextView.setText(String.format(res.getString(R.string.profile_shares), stats.getShares()));
        sharedDistanceTextView.setText(String.format(res.getString(R.string.profile_shared_distance), df.format(stats.getSharedDistance())));
        savedGasTextView.setText(String.format(res.getString(R.string.profile_saved_gas), df.format(stats.getSavedConsumption())));
        savedEmissionsTextView.setText(String.format(res.getString(R.string.profile_saved_emissions), df.format(stats.getSavedEmission())));*/


    }

}
