package it.gruppoinfor.home2work.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2work.utils.DateFormatUtils
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Share
import kotlinx.android.synthetic.main.item_match.view.*
import kotlinx.android.synthetic.main.item_share.view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

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

        holder.textShareDatetime.text = DateFormatUtils.formatDate(share.date)

        val guestSize = share.guests.size

        holder.textShareGuests.text = guestSize.toString()

        if (share.type == Share.Type.DRIVER) {

            holder.textShareInfo.text = "Hai condiviso la tua auto"

            holder.textShareType.text = "Driver"
            holder.textShareType.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))

            val totalMt = share.guests.sumBy { it.distance }

            val totalKm = totalMt / 1000.0
            holder.textShareDistance.text = mDf.format(totalKm)
            holder.textShareXp.text = (totalKm.toInt() * 10).toString()


        } else {

            holder.textShareInfo.text = "Hai condiviso l'auto di ${share.host}"

            holder.textShareType.text = "Guest"
            holder.textShareType.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent))

            for (guest in share.guests) {
                if (guest.user == HomeToWorkClient.user) {
                    val totalKm = guest.distance / 1000.0
                    holder.textShareDistance.text = mDf.format(totalKm)
                    holder.textShareXp.text = (totalKm.toInt() * 10).toString()
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return mShares.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textShareDatetime: TextView = itemView.text_share_datetime
        val textShareGuests: TextView = itemView.text_share_guests
        val textShareInfo: TextView = itemView.text_share_info
        val textShareType: TextView = itemView.text_share_type
        val textShareDistance: TextView = itemView.text_share_distance
        val textShareXp: TextView = itemView.text_share_xp
    }
}
