package it.gruppoinfor.home2work.sharehistory

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.ImageLoader
import it.gruppoinfor.home2work.common.utilities.DateFormatUtils
import it.gruppoinfor.home2work.entities.Share
import it.gruppoinfor.home2work.entities.ShareType
import kotlinx.android.synthetic.main.item_share.view.*
import java.math.RoundingMode
import java.text.DecimalFormat

class ShareHistoryAdapter(
        private val imageLoader: ImageLoader,
        private val onShareClick: (Share, Int) -> Unit
) : RecyclerView.Adapter<ShareHistoryAdapter.ViewHolder>() {

    private val shares: MutableList<Share> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_share, parent, false)
        return ViewHolder(parent.context, v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val share = shares[position]
        holder.bind(share, position, onShareClick, imageLoader)
    }

    override fun getItemCount(): Int {
        return shares.size
    }

    fun setItems(list: List<Share>) {
        shares.clear()
        shares.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val mDf: DecimalFormat = DecimalFormat("#.##")

        init {
            mDf.roundingMode = RoundingMode.CEILING
        }

        fun bind(
                share: Share,
                position: Int,
                onShareClick: (Share, Int) -> Unit,
                imageLoader: ImageLoader
        ) = with(itemView) {

            text_share_datetime.text = DateFormatUtils.formatDate(share.date)

            val guestSize = share.guests.size

            text_share_guests.text = guestSize.toString()

            when (share.type) {
                ShareType.HOST -> {
                    text_share_info.text = "Hai condiviso la tua auto"

                    text_share_type.text = "Driver"
                    text_share_type.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))

                    val totalMt = share.guests.sumBy { it.distance }

                    val totalKm = totalMt / 1000.0
                    text_share_distance.text = mDf.format(totalKm)
                    text_share_xp.text = (totalKm.toInt() * 10).toString()
                }
                ShareType.GUEST -> {
                    text_share_info.text = "Hai condiviso l'auto di ${share.host}"

                    text_share_type.text = "Guest"
                    text_share_type.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))

                    for (guest in share.guests) {
                        /* if (guest.user == HomeToWorkClient.user) {
                             val totalKm = guest.distance / 1000.0
                             holder.textShareDistance.text = mDf.format(totalKm)
                             holder.textShareXp.text = (totalKm.toInt() * 10).toString()
                         }*/
                    }
                }
            }


        }


    }
}
