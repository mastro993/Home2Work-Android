package it.gruppoinfor.home2work.configuration

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.api.HomeToWorkClient
import kotlinx.android.synthetic.main.fragment_conf_name.*

class ConfigurationFragmentName : Fragment(), Step {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_conf_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun verifyStep(): VerificationError? {

        if (input_name.text.isEmpty()) {
            input_name.error = getString(R.string.activity_configuration_name_warning)
            return VerificationError(getString(R.string.activity_configuration_name_error))
        }

        if (input_surname.text.isEmpty()) {
            input_surname.error = getString(R.string.activity_configuration_surname_warning)
            return VerificationError(getString(R.string.activity_configuration_surname_error))
        }

        HomeToWorkClient.user?.name = input_name.text.toString().trim { it <= ' ' }
        HomeToWorkClient.user?.surname = input_surname.text.toString().trim { it <= ' ' }

        return null
    }

    override fun onSelected() {

    }

    override fun onError(error: VerificationError) {

    }

    private fun initUI() {

        input_name.setText(HomeToWorkClient.user?.name)
        input_surname.setText(HomeToWorkClient.user?.surname)

    }

}