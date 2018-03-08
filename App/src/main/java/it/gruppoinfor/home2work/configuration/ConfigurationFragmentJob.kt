package it.gruppoinfor.home2work.configuration

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.OnSuccessListener
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.company.Company
import kotlinx.android.synthetic.main.fragment_conf_job.*

class ConfigurationFragmentJob : Fragment(), Step {

    private lateinit var mCompanies: ArrayList<Company>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_conf_job, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        HomeToWorkClient.getCompanyList(OnSuccessListener {
            mCompanies = it
            val companySpinnerAdapter = CompanySpinnerAdapter(activity as Activity, mCompanies)
            companySpinner.adapter = companySpinnerAdapter
            loadingView.visibility = View.GONE
        })

        companySpinner.requestFocus()

    }

    override fun verifyStep(): VerificationError? {

        if (companySpinner!!.selectedItem.toString() == getString(R.string.company))
            return VerificationError(getString(R.string.activity_configuration_company_step_warning))

        HomeToWorkClient.user?.company = companySpinner!!.selectedItem as Company

        return null
    }

    override fun onSelected() {

    }

    override fun onError(error: VerificationError) {

    }

}