package it.gruppoinfor.home2work.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2workapi.model.Company;


public class CompanySpinnerAdapter extends ArrayAdapter<Company> {

    private ArrayList<Company> mCompanies;
    private LayoutInflater mInflater;

    public CompanySpinnerAdapter(Activity activity, List<Company> companies) {
        super(activity, R.layout.item_company_spinner, R.id.company_name, companies);
        mCompanies = new ArrayList<>(companies);
        mInflater = activity.getLayoutInflater();
    }

    @Override
    public View getDropDownView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ViewHolder holder;
        Company company = mCompanies.get(position);

        if (view == null) {
            view = mInflater.inflate(R.layout.item_company_spinner, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.companyName.setText(company.getName());
        holder.companyAddress.setText(company.getAddress().getCity());

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ViewHolder holder;
        Company company = mCompanies.get(position);

        if (view == null) {
            view = mInflater.inflate(R.layout.item_company_spinner, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.companyName.setText(company.getName());
        holder.companyAddress.setText(company.getAddress().getCity());

        return view;
    }

    @Override
    public int getCount() {
        return mCompanies.size() - 1;
    }

    static class ViewHolder {
        @BindView(R.id.company_name)
        TextView companyName;
        @BindView(R.id.company_address)
        TextView companyAddress;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
