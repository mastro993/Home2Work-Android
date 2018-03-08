package it.gruppoinfor.home2work.configuration

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.stepstone.stepper.Step
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter

class ConfigurationStepsAdapter internal constructor(fm: FragmentManager, context: Context) : AbstractFragmentStepAdapter(fm, context) {

    override fun createStep(position: Int): Step {

        val fragment: Fragment = when (position) {
            0 -> ConfigurationFragmentStart()
            1 -> ConfigurationFragmentName()
            2 -> ConfigurationFragmentHome()
            3 -> ConfigurationFragmentJob()
            4 -> ConfigurationFragmentAvatar()
            5 -> ConfigurationFragmentComplete()
            else -> ConfigurationFragmentStart()
        }

        val bundle = Bundle()
        bundle.putInt(CURRENT_STEP_POSITION_KEY, position)
        fragment.arguments = bundle

        return fragment as Step

    }

    override fun getCount(): Int {
        return 6
    }

    companion object {
        private const val CURRENT_STEP_POSITION_KEY = "current_step"
    }

}