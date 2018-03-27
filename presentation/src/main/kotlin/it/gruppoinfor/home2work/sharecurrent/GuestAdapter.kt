package it.gruppoinfor.home2work.sharecurrent

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.ImageLoader
import it.gruppoinfor.home2work.common.PicassoCircleTransform
import it.gruppoinfor.home2work.entities.Guest
import it.gruppoinfor.home2work.entities.ShareStatus
import kotlinx.android.synthetic.main.item_share_guest.view.*

class GuestAdapter(
        private val imageLoader: ImageLoader,
        private val onGuestClick: (Guest, Int) -> Unit,
        private val onGuestLongClick: (Guest, Int) -> Boolean
) : RecyclerView.Adapter<GuestAdapter.ViewHolder>() {

    private val guests: MutableList<Guest> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_share_guest, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val guest = guests[position]
        holder.bind(guest, position, onGuestClick, onGuestLongClick, imageLoader)
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

        fun bind(
                guest: Guest,
                position: Int,
                onGuesthClick: (Guest, Int) -> Unit,
                onGuestLongClick: (Guest, Int) -> Boolean,
                imageLoader: ImageLoader
        ) = with(itemView) {

            imageLoader.load(
                    url = guest.user.avatarUrl,
                    imageView = share_guest_user_avatar,
                    transformation = PicassoCircleTransform(),
                    placeholder = R.drawable.ic_avatar_placeholder
            )


            share_guest_name_view.text = guest.user.toString()
            share_guest_job_view.text = guest.user.company.toString()

            setOnClickListener {
                onGuesthClick(guest, position)
            }
            setOnLongClickListener {
                onGuestLongClick(guest, position)
            }

            when {
                guest.status == ShareStatus.COMPLETED -> {
                    guest_status_completed.visibility = View.VISIBLE
                    guest_status_leaved.visibility = View.GONE
                    alpha = 1.0f
                }
                guest.status == ShareStatus.CANCELED -> {
                    guest_status_completed.visibility = View.GONE
                    guest_status_leaved.visibility = View.VISIBLE
                    alpha = 0.6f
                }
                else -> {
                    guest_status_completed.visibility = View.GONE
                    guest_status_leaved.visibility = View.GONE
                    alpha = 1.0f
                }
            }


        }


    }
}
