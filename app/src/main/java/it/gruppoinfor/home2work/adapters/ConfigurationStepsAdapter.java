package it.gruppoinfor.home2work.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;

import it.gruppoinfor.home2work.fragments.ConfigurationFragmentAvatar;
import it.gruppoinfor.home2work.fragments.ConfigurationFragmentComplete;
import it.gruppoinfor.home2work.fragments.ConfigurationFragmentHome;
import it.gruppoinfor.home2work.fragments.ConfigurationFragmentJob;
import it.gruppoinfor.home2work.fragments.ConfigurationFragmentName;
import it.gruppoinfor.home2work.fragments.ConfigurationFragmentStart;

public class ConfigurationStepsAdapter extends AbstractFragmentStepAdapter {

    public static final String CURRENT_STEP_POSITION_KEY = "current_step";

    public ConfigurationStepsAdapter(FragmentManager fm, Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {

        Fragment fragment;
        Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_STEP_POSITION_KEY, position);

        switch (position) {
            case 0:
                fragment = new ConfigurationFragmentStart();
                break;
            case 1:
                fragment = new ConfigurationFragmentName();
                break;
            case 2:
                fragment = new ConfigurationFragmentHome();
                break;
            case 3:
                fragment = new ConfigurationFragmentJob();
                break;
            case 4:
                fragment = new ConfigurationFragmentAvatar();
                break;
            case 5:
                fragment = new ConfigurationFragmentComplete();
                break;
            default:
                fragment = new ConfigurationFragmentStart();
        }

        fragment.setArguments(bundle);
        return (Step) fragment;
    }

    @Override
    public int getCount() {
        return 6;
    }


}
