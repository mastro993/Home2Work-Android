package it.gruppoinfor.home2work.adapters

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.ShowUserActivity
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2workapi.model.Match
import kotlinx.android.synthetic.main.item_match.view.*

class MatchAdapter(private val mContext: Context, private val matches: ArrayList<Match>) : RecyclerView.Adapter<MatchAdapter.ViewHolder>() {

    private var mItemClickCallbacks: ItemClickCallbacks? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchAdapter.ViewHolder {

        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)

        return MatchAdapter.ViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: MatchAdapter.ViewHolder, position: Int) {

        val match = matches[position]

        if (!match.isNew) {
            holder.newBadge.visibility = View.INVISIBLE
        } else {
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
                .load(match.host.avatarURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(holder.userAvatar)

        val animator = ValueAnimator.ofInt(0, match.score)
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { valueAnimator -> holder.scoreText.text = "${valueAnimator.animatedValue}" }
        animator.start()

        val color = getScoreColor(match.score)
        holder.scoreText.setTextColor(color)

        holder.nameText.text = match.host.toString()
        holder.jobText.text = match.host.company.toString()
        holder.homeText.text = match.host.address.city

        holder.container.setOnClickListener { v -> mItemClickCallbacks!!.onItemClick(v, position) }
        holder.container.setOnLongClickListener { v -> mItemClickCallbacks!!.onLongItemClick(v, position) }

        if (match.score == 0) holder.scoreText.visibility = View.INVISIBLE

        holder.userAvatar.setOnClickListener {
            val userIntent = Intent(mContext, ShowUserActivity::class.java)
            userIntent.putExtra("user", match.host)
            mContext.startActivity(userIntent)
        }

    }

    override fun getItemCount(): Int {

        return matches.size
    }

    private fun getScoreColor(score: Int): Int {

        return when {
            score < 60 -> ContextCompat.getColor(mContext, R.color.red_500)
            score < 70 -> ContextCompat.getColor(mContext, R.color.orange_600)
            score < 80 -> ContextCompat.getColor(mContext, R.color.amber_400)
            score < 90 -> ContextCompat.getColor(mContext, R.color.light_green_500)
            else -> ContextCompat.getColor(mContext, R.color.green_500)
        }
    }

    fun remove(position: Int) {

        matches.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, matches.size)

    }

    fun setItemClickCallbacks(itemClickCallbacks: ItemClickCallbacks) {

        mItemClickCallbacks = itemClickCallbacks

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newBadge: View = itemView.new_badge
        val userAvatar: ImageView = itemView.match_user_avatar
        val scoreText: TextView = itemView.score_text
        val nameText: TextView = itemView.match_user_name_view
        val jobText: TextView = itemView.match_user_job_view
        val homeText: TextView = itemView.home_view
        val container: View = itemView.item_match_container
        val divider: View = itemView.item_match_divider
    }
}
