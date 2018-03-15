package it.gruppoinfor.home2work.configuration

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import it.gruppoinfor.home2work.R

class ConfigurationFragmentStart : Fragment(), Step {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_conf_start, container, false)
    }

    override fun verifyStep(): VerificationError? {

        Answers.getInstance().logCustom(CustomEvent("Inizio configurazione"))

        return null
    }

    override fun onSelected() {

    }

    override fun onError(error: VerificationError) {

    }
}