package it.gruppoinfor.home2work.sharehistory

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseAdapter
import it.gruppoinfor.home2work.common.ImageLoader
import it.gruppoinfor.home2work.common.utilities.DateFormatUtils
import it.gruppoinfor.home2work.common.utilities.StaticMapUriBuilder
import it.gruppoinfor.home2work.entities.Share
import it.gruppoinfor.home2work.entities.ShareType
import kotlinx.android.synthetic.main.item_share.view.*
import java.math.RoundingMode
import java.text.DecimalFormat


class ShareHistoryAdapter(
        private val imageLoader: ImageLoader,
        private val onShareClick: (Share, Int) -> Unit
) : BaseAdapter<Share>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_share, parent, false)
        return ViewHolder(parent.context, v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val share = items[position]
        (holder as ViewHolder).bind(share, position, onShareClick, imageLoader)
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

            val staticMapUrl = StaticMapUriBuilder.buildFor(share)
            imageLoader.load(url = staticMapUrl, imageView = share_route_image, fit = true)

            text_share_datetime.text = DateFormatUtils.formatDate(share.date)

            val guestSize = share.guests.size

            text_share_guests.text = guestSize.toString()

            when (share.type) {
                ShareType.HOST -> {
                    text_share_info.text = "Hai condiviso la tua auto"

                    text_share_type.text = "Driver"
                    text_share_type.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                }
                ShareType.GUEST -> {
                    text_share_info.text = "Hai condiviso l'auto di ${share.host.fullName}"

                    text_share_type.text = "Guest"
                    text_share_type.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                }
            }

            text_share_distance.text = mDf.format(share.sharedDistance.div(1000.0))
            text_share_xp.text = (share.sharedDistance.div(100)).toString()


        }


    }
}
