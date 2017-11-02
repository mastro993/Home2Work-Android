package it.gruppoinfor.home2work.fragments;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arasthel.asyncjob.AsyncJob;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.Mockup;
import it.gruppoinfor.home2workapi.model.Profile;
import it.gruppoinfor.home2workapi.model.ProfileStats;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgressFragment extends Fragment {

    Profile profile;
    SwipeRefreshLayout rootView;
    Unbinder unbinder;
    Resources res;
    @BindView(R.id.karma_donut_progress)
    DonutProgress karmaDonutProgress;
    @BindView(R.id.user_propic)
    CircleImageView userPropic;
    @BindView(R.id.karma_level)
    TextView karmaLevel;
    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.job_text_view)
    TextView jobTextView;
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


    public ProgressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_progress, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        res = getResources();
        //setHasOptionsMenu(true);
        initUI();
        if (profile == null) {
            refreshProfile();
        }
        return rootView;
    }

    private void refreshProfile() {
        rootView.setRefreshing(true);

        // TODO refresh profilo dal server
        Mockup.getUserProfile(new AsyncJob.AsyncResultAction<Profile>() {
            @Override
            public void onResult(Profile p) {
                profile = p;
                rootView.setRefreshing(false);
                initProfileUI();
            }
        });
    }

    private void initUI() {

        nameTextView.setText(Client.getSignedUser().toString());
        jobTextView.setText(Client.getSignedUser().getJob().getCompany().toString());

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_avatar_placeholder).dontAnimate();

        Glide.with(getActivity())
                .load(Client.getSignedUser().getAvatarURL())
                .apply(requestOptions)
                .into(userPropic);


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

        rootView.setOnRefreshListener(this::refreshProfile);
        rootView.setColorSchemeResources(R.color.colorAccent);

    }

    private void initProfileUI() {
        ProfileStats profileStats = profile.getProfileStats();
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.ITALIAN);
        String dateString = dateFormat.format(profileStats.getRegDate());

        String regDateString = String.format(res.getString(R.string.profile_regdate), dateString);
        String distanceString = String.format(res.getString(R.string.profile_distance), df.format(profileStats.getTotalKilometers()));
        String gasString = String.format(res.getString(R.string.profile_gas), df.format(profileStats.getTotalGas()));
        String emissionString = String.format(res.getString(R.string.profile_emissions), df.format(profileStats.getTotalEmissions()));
        String sharesString = String.format(res.getString(R.string.profile_shares), profileStats.getTotalShares());
        String distanceSharedString = String.format(res.getString(R.string.profile_shared_distance), df.format(profileStats.getTotalSharedKilometers()));
        String savedGasString = String.format(res.getString(R.string.profile_saved_gas), df.format(profileStats.getTotalGasSaved()));
        String savedEmissionString = String.format(res.getString(R.string.profile_saved_emissions), df.format(profileStats.getTotalEmissionSaved()));

        karmaDonutProgress.setProgress(profile.getKarma().getLevelProgres());
        karmaLevel.setText(regDateString);
        regdateTextView.setText(regDateString);
        distanceTextView.setText(distanceString);
        gasTextView.setText(gasString);
        emissionsTextView.setText(emissionString);
        sharesTextView.setText(sharesString);
        sharedDistanceTextView.setText(distanceSharedString);
        savedGasTextView.setText(savedGasString);
        savedEmissionsTextView.setText(savedEmissionString);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
