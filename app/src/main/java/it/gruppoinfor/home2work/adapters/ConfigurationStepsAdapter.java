package it.gruppoinfor.home2work.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;

import it.gruppoinfor.home2work.fragments.ConfigurationAvatarFragment;
import it.gruppoinfor.home2work.fragments.ConfigurationCompleteFragment;
import it.gruppoinfor.home2work.fragments.ConfigurationHomeFragment;
import it.gruppoinfor.home2work.fragments.ConfigurationJobFragment;
import it.gruppoinfor.home2work.fragments.ConfigurationNameFragment;
import it.gruppoinfor.home2work.fragments.ConfigurationStartFragment;

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
                fragment = new ConfigurationStartFragment();
                break;
            case 1:
                fragment = new ConfigurationNameFragment();
                break;
            case 2:
                fragment = new ConfigurationHomeFragment();
                break;
            case 3:
                fragment = new ConfigurationJobFragment();
                break;
            case 4:
                fragment = new ConfigurationAvatarFragment();
                break;
            case 5:
                fragment = new ConfigurationCompleteFragment();
                break;
            default:
                fragment = new ConfigurationStartFragment();
        }

        fragment.setArguments(bundle);
        return (Step) fragment;
    }

    @Override
    public int getCount() {
        return 6;
    }


}
