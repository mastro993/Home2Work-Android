package it.gruppoinfor.home2work.configuration

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2workapi.company.Company
import kotlinx.android.synthetic.main.item_company_spinner.view.*


@Suppress("NAME_SHADOWING")
class CompanySpinnerAdapter(private val activity: Activity, private val companies: List<Company>) : ArrayAdapter<Company>(activity, R.layout.item_company_spinner, R.id.company_name, companies) {

    override fun getDropDownView(position: Int, view: View?, parent: ViewGroup): View {

        var view = view
        val holder: ViewHolder
        val company = companies[position]

        if (view == null) {
            view = activity.layoutInflater.inflate(R.layout.item_company_spinner, parent, false)
            holder = ViewHolder(view)
            view!!.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        holder.companyName.text = company.name
        holder.companyAddress.text = company.address.city

        return view
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        val holder: ViewHolder
        val company = companies[position]

        if (view == null) {
            view = activity.layoutInflater.inflate(R.layout.item_company_spinner, parent, false)
            holder = ViewHolder(view)
            view!!.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        holder.companyName.text = company.name
        holder.companyAddress.text = company.address.city

        return view
    }

    override fun getCount(): Int {

        return companies.size - 1
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val companyName: TextView = itemView.company_name
        val companyAddress: TextView = itemView.company_address
    }
}
