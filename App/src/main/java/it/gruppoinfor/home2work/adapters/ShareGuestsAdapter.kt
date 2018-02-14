package it.gruppoinfor.home2work.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2workapi.model.Guest
import kotlinx.android.synthetic.main.item_match.view.*
import kotlinx.android.synthetic.main.item_share_guest.view.*
import java.util.*

class ShareGuestsAdapter(private val mContext: Context, values: List<Guest>) : RecyclerView.Adapter<ShareGuestsAdapter.ViewHolder>() {
    private val mGuests: ArrayList<Guest>
    private var mItemClickCallbacks: ItemClickCallbacks? = null

    init {
        mGuests = ArrayList(values)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_share_guest, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val guest = mGuests[position]

        val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder)

        Glide.with(mContext)
                .load(guest.user.avatarURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(holder.userAvatar)


        holder.nameView.text = guest.user.toString()
        holder.jobView.text = guest.user.company.toString()

        holder.container.setOnClickListener { v -> mItemClickCallbacks!!.onItemClick(v, position) }
        holder.container.setOnLongClickListener { v -> mItemClickCallbacks!!.onLongItemClick(v, position) }

        if (guest.status == Guest.Status.COMPLETED) {
            holder.viewStatusCompleted.visibility = View.VISIBLE
        } else if (guest.status == Guest.Status.CANCELED) {
            holder.viewStatusLeaved.visibility = View.VISIBLE
            holder.itemView.alpha = 0.7f
        }

    }

    override fun getItemCount(): Int {
        return mGuests.size
    }

    fun setItemClickCallbacks(itemClickCallbacks: ItemClickCallbacks) {
        mItemClickCallbacks = itemClickCallbacks
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.share_guest_name_view
        val userAvatar: ImageView = itemView.share_guest_user_avatar
        val jobView: TextView = itemView.share_guest_job_view
        val viewStatusCompleted: View = itemView.guest_status_completed
        val viewStatusLeaved: View = itemView.guest_status_leaved
        val container: View = itemView.share_guest_container
    }
}
