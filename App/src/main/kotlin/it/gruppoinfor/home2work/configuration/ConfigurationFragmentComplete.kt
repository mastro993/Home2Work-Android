package it.gruppoinfor.home2work.configuration

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.api.HomeToWorkClient

class ConfigurationFragmentComplete : Fragment(), BlockingStep {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_conf_completed, container, false)
    }

    override fun verifyStep(): VerificationError? {

        return null
    }

    override fun onSelected() {

    }

    override fun onError(error: VerificationError) {

    }

    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback) {

    }

    @SuppressLint("CheckResult")
    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback) {

        callback.stepperLayout.showProgress(getString(R.string.activity_configuration_wait))

        HomeToWorkClient.user?.configured = true

        HomeToWorkClient.getUserService().edit(HomeToWorkClient.user!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn {
                    null
                }
                .subscribe({
                    callback.complete()
                }, {
                    callback.stepperLayout.hideProgress()
                })


    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {

        callback.goToPrevStep()

    }
}