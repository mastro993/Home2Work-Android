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

import java.util.ArrayList
import java.util.Locale

import butterknife.BindView
import butterknife.ButterKnife
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.ShowUserActivity
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2workapi.model.Match
import it.gruppoinfor.home2workapi.model.User

class MatchAdapter(private val mContext: Context, values: List<Match>) : RecyclerView.Adapter<MatchAdapter.ViewHolder>() {

    private var mItemClickCallbacks: ItemClickCallbacks? = null
    private val mMatches: ArrayList<Match>

    init {
        mMatches = ArrayList(values)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchAdapter.ViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        return MatchAdapter.ViewHolder(layoutView)

    }

    override fun onBindViewHolder(holder: MatchAdapter.ViewHolder, position: Int) {
        val match = mMatches[position]

        if (!match.isNew) {
            holder.newBadgeView!!.visibility = View.INVISIBLE
        } else {
            val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    holder.newBadgeView,
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
                .into(holder.userAvatar!!)

        val animator = ValueAnimator.ofInt(0, match.score)
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { valueAnimator -> holder.scoreText!!.text = String.format(Locale.ITALIAN, "%1\$d%%", valueAnimator.animatedValue as Int) }
        animator.start()

        val color = getScoreColor(match.score!!)
        holder.scoreText!!.setTextColor(color)

        holder.nameView!!.text = match.host.toString()
        holder.jobView!!.text = match.host.company.toString()
        holder.homeView!!.text = match.host.address.city

        holder.container!!.setOnClickListener { v -> mItemClickCallbacks!!.onItemClick(v, position) }
        holder.container!!.setOnLongClickListener { v -> mItemClickCallbacks!!.onLongItemClick(v, position) }

        if (match.score == 0) holder.scoreText!!.visibility = View.INVISIBLE

        if (position == 0) holder.divider!!.visibility = View.GONE

        holder.userAvatar!!.setOnClickListener { v ->
            val userIntent = Intent(mContext, ShowUserActivity::class.java)
            userIntent.putExtra("user", match.host)
            mContext.startActivity(userIntent)

        }
    }

    override fun getItemCount(): Int {
        return mMatches.size
    }

    private fun getScoreColor(score: Int): Int {
        return if (score < 60) {
            ContextCompat.getColor(mContext, R.color.red_500)
        } else if (score < 70) {
            ContextCompat.getColor(mContext, R.color.orange_600)
        } else if (score < 80) {
            ContextCompat.getColor(mContext, R.color.amber_400)
        } else if (score < 90) {
            ContextCompat.getColor(mContext, R.color.light_green_500)
        } else {
            ContextCompat.getColor(mContext, R.color.green_500)
        }
    }

    fun remove(position: Int) {
        mMatches.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mMatches.size)
    }

    fun setItemClickCallbacks(itemClickCallbacks: ItemClickCallbacks) {
        mItemClickCallbacks = itemClickCallbacks
    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @BindView(R.id.user_avatar)
        var userAvatar: ImageView? = null
        @BindView(R.id.score_text)
        var scoreText: TextView? = null
        @BindView(R.id.name_view)
        var nameView: TextView? = null
        @BindView(R.id.job_view)
        var jobView: TextView? = null
        @BindView(R.id.home_view)
        var homeView: TextView? = null
        @BindView(R.id.new_badge)
        var newBadgeView: View? = null
        @BindView(R.id.container)
        var container: View? = null
        @BindView(R.id.divider)
        var divider: View? = null

        init {
            ButterKnife.bind(this, view)
        }
    }
}
