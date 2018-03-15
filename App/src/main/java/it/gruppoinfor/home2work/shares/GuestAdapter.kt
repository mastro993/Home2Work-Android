package it.gruppoinfor.home2work.shares

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
import io.reactivex.Observable
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2workapi.share.Guest
import kotlinx.android.synthetic.main.item_share_guest.view.*

class GuestAdapter(private val context: Context, private val ongoingShareView: OngoingShareView) : RecyclerView.Adapter<GuestAdapter.ViewHolder>() {

    private val guests: ArrayList<Guest> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_share_guest, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val guest = guests[position]

        val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder)

        Glide.with(context)
                .load(guest.user?.avatarURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(holder.userAvatar)


        holder.nameView.text = guest.user.toString()
        holder.jobView.text = guest.user?.company.toString()

        holder.container.setOnClickListener {
            ongoingShareView.onGuestClick(position, guest)
        }
        holder.container.setOnLongClickListener {
            ongoingShareView.onGuestLongClick(position, guest)
            true
        }

        when {
            guest.status == Guest.Status.COMPLETED -> {
                holder.viewStatusCompleted.visibility = View.VISIBLE
                holder.viewStatusLeaved.visibility = View.GONE
                holder.itemView.alpha = 1.0f
            }
            guest.status == Guest.Status.CANCELED -> {
                holder.viewStatusCompleted.visibility = View.GONE
                holder.viewStatusLeaved.visibility = View.VISIBLE
                holder.itemView.alpha = 0.6f
            }
            else -> {
                holder.viewStatusCompleted.visibility = View.GONE
                holder.viewStatusLeaved.visibility = View.GONE
                holder.itemView.alpha = 1.0f
            }
        }

    }

    override fun getItemCount(): Int {

        return guests.size
    }

    fun setItems(guests: List<Guest>) {
        this.guests.clear()
        this.guests.addAll(guests)
        notifyDataSetChanged()
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
