package it.gruppoinfor.home2work.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2workapi.model.Company


class CompanySpinnerAdapter(activity: Activity, companies: List<Company>) : ArrayAdapter<Company>(activity, R.layout.item_company_spinner, R.id.company_name, companies) {

    private val mCompanies: ArrayList<Company>
    private val mInflater: LayoutInflater

    init {
        mCompanies = ArrayList(companies)
        mInflater = activity.layoutInflater
    }

    override fun getDropDownView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        val holder: ViewHolder
        val company = mCompanies[position]

        if (view == null) {
            view = mInflater.inflate(R.layout.item_company_spinner, parent, false)
            holder = ViewHolder(view)
            view!!.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        holder.companyName!!.text = company.name
        holder.companyAddress!!.text = company.address.city

        return view
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        val holder: ViewHolder
        val company = mCompanies[position]

        if (view == null) {
            view = mInflater.inflate(R.layout.item_company_spinner, parent, false)
            holder = ViewHolder(view)
            view!!.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        holder.companyName!!.text = company.name
        holder.companyAddress!!.text = company.address.city

        return view
    }

    override fun getCount(): Int {
        return mCompanies.size - 1
    }

    internal class ViewHolder(view: View) {
        @BindView(R.id.company_name)
        var companyName: TextView? = null
        @BindView(R.id.company_address)
        var companyAddress: TextView? = null

        init {
            ButterKnife.bind(this, view)
        }
    }
}
