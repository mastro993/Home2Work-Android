package it.gruppoinfor.home2work.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2work.utils.DateFormatUtils
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Guest
import it.gruppoinfor.home2workapi.model.Share

class SharesAdapter(private val mContext: Context, values: List<Share>) : RecyclerView.Adapter<SharesAdapter.ViewHolder>() {
    private val mShares: ArrayList<Share>
    private var mItemCallbacks: ItemClickCallbacks? = null

    private val mDf: DecimalFormat

    init {
        mShares = ArrayList(values)
        mDf = DecimalFormat("#.##")
        mDf.roundingMode = RoundingMode.CEILING
    }

    fun setItemClickCallbacks(itemClickCallbacks: ItemClickCallbacks) {
        mItemCallbacks = itemClickCallbacks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_share, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val share = mShares[position]

        holder.textShareDatetime!!.text = DateFormatUtils.formatDate(share.date)

        val guestSize = share.guests.size

        holder.textShareGuests!!.text = guestSize.toString()

        if (share.type == Share.Type.DRIVER) {

            holder.textShareInfo!!.text = "Hai condiviso la tua auto"

            holder.textShareType!!.text = "Driver"
            holder.textShareType!!.setTextColor(mContext.resources.getColor(R.color.colorPrimary))

            var totalMt = 0
            for (shareguest in share.guests) {
                totalMt += shareguest.distance
            }

            val totalKm = totalMt / 1000.0
            holder.textShareDistance!!.text = mDf.format(totalKm)
            holder.textShareXp!!.text = (totalKm.toInt() * 10).toString()


        } else {

            holder.textShareInfo!!.text = "Hai condiviso l'auto di " + share.host.toString()

            holder.textShareType!!.text = "Guest"
            holder.textShareType!!.setTextColor(mContext.resources.getColor(R.color.colorAccent))

            for (guest in share.guests) {
                if (guest.user == HomeToWorkClient.getUser()) {
                    val totalKm = guest.distance / 1000.0
                    holder.textShareDistance!!.text = mDf.format(totalKm)
                    holder.textShareXp!!.text = (totalKm.toInt() * 10).toString()
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return mShares.size
    }


    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @BindView(R.id.text_share_type)
        var textShareType: TextView? = null
        @BindView(R.id.text_share_datetime)
        var textShareDatetime: TextView? = null
        @BindView(R.id.text_share_guests)
        var textShareGuests: TextView? = null
        @BindView(R.id.text_share_distance)
        var textShareDistance: TextView? = null
        @BindView(R.id.text_share_xp)
        var textShareXp: TextView? = null
        @BindView(R.id.text_share_info)
        var textShareInfo: TextView? = null

        init {
            ButterKnife.bind(this, view)
        }
    }
}
