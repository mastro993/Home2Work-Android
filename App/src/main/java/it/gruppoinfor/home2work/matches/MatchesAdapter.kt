package it.gruppoinfor.home2work.matches

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.transition.Explode
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import it.gruppoinfor.home2work.MainActivity
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.user.UserActivityArgs
import it.gruppoinfor.home2workapi.match.Match
import kotlinx.android.synthetic.main.item_match.view.*


class MatchesAdapter(private val mContext: Context, private var matchesView: MatchesView) : RecyclerView.Adapter<MatchesAdapter.ViewHolder>() {

    private val matches: ArrayList<Match> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)

        return ViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val match = matches[position]

        if (!match.isNew) {
            holder.newBadge.visibility = View.INVISIBLE
        } else {
            holder.newBadge.visibility = View.VISIBLE
            val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    holder.newBadge,
                    PropertyValuesHolder.ofFloat("scaleX", 0.8f),
                    PropertyValuesHolder.ofFloat("scaleY", 0.8f))
            scaleDown.duration = 150

            scaleDown.repeatCount = 1
            scaleDown.repeatMode = ObjectAnimator.REVERSE

            scaleDown.start()
        }

        val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder)

        Glide.with(mContext)
                .load(match.host?.avatarURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(holder.userAvatar)


        val color = getScoreColor(match.getScore())
        holder.matchScore.text = "${match.getScore()}%"
        holder.matchScore.setTextColor(color)

        if(match.getScore() == 0) holder.matchScore.visibility = View.GONE

        holder.nameText.text = match.host.toString()
        holder.jobText.text = match.host?.company.toString()
        holder.homeText.text = "Da ${match.host?.address?.city}"

        holder.userAvatar.setOnClickListener {

            val user = match.host!!

            UserActivityArgs(
                    userId = user.id,
                    userName = user.toString(),
                    userAvatarUrl = user.avatarURL,
                    userCompanyId = user.company.id,
                    userCompanyName = user.company.name
            ).launch(mContext)
        }

        holder.container.setOnClickListener({

            if (match.isNew) {
                match.isNew = false
                notifyItemChanged(position)
            }

            matchesView.onMatchClick(position, match)

        })
        holder.container.setOnLongClickListener {
            matchesView.onMatchLongClick(position, match)
            true
        }


    }

    override fun getItemCount(): Int {

        return matches.size
    }

    fun setItems(matches: List<Match>) {
        this.matches.clear()
        this.matches.addAll(matches)
        notifyDataSetChanged()
    }

    private fun getScoreColor(score: Int): Int {

        return when {
            score == 0 -> ContextCompat.getColor(mContext, R.color.light_bg_dark_hint_text)
            score < 60 -> ContextCompat.getColor(mContext, R.color.red_500)
            score < 70 -> ContextCompat.getColor(mContext, R.color.orange_600)
            score < 80 -> ContextCompat.getColor(mContext, R.color.amber_500)
            score < 90 -> ContextCompat.getColor(mContext, R.color.light_green_500)
            else -> ContextCompat.getColor(mContext, R.color.green_500)
        }
    }

    fun remove(position: Int) {

        matches.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, matches.size)

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newBadge: View = itemView.new_badge
        val userAvatar: ImageView = itemView.match_user_avatar
        val nameText: TextView = itemView.text_host_name
        val jobText: TextView = itemView.text_host_job
        val homeText: TextView = itemView.text_host_home
        val matchScore: TextView = itemView.text_match_score
        val container: View = itemView.match_item_container
        val divider: View = itemView.item_match_divider
    }
}
