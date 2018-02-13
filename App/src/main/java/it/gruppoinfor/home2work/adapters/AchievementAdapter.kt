package it.gruppoinfor.home2work.adapters

import android.app.Activity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Locale

import butterknife.BindView
import butterknife.ButterKnife
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.MainActivity
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2workapi.model.Achievement
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

class AchievementAdapter(activity: Activity, values: List<Achievement>) : RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {

    private val activity: MainActivity
    private val achievements: ArrayList<Achievement>
    private var itemClickCallbacks: ItemClickCallbacks? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_achievement, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val achievement = achievements[position]

        val requestOptions = RequestOptions().placeholder(R.color.grey_200).dontAnimate()

        Glide.with(activity)
                .load(achievement.achievementID)
                .apply(requestOptions)
                .into(holder.achievementIcon!!)

        holder.achievementName!!.text = achievement.name
        holder.achievementDescription!!.text = achievement.description
        holder.progressText!!.text = String.format("%1\$s/%2\$s", achievement.current, achievement.goal)
        holder.progressBar!!.progress = achievement.progress!!
        holder.expView!!.text = String.format("+%1\$s", achievement.exp)

        val color = ContextCompat.getColor(activity, R.color.colorPrimary)

        if (achievement.progress == 100) {
            holder.unlockDate!!.visibility = View.VISIBLE
            holder.progressPercentile!!.visibility = View.GONE
            holder.expView!!.setTextColor(color)
            val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.ITALIAN)
            val dateString = dateFormat.format(achievement.unlockDate)
            holder.unlockDate!!.text = dateString
        } else {
            holder.unlockDate!!.visibility = View.GONE
            holder.progressPercentile!!.visibility = View.VISIBLE
            holder.progressPercentile!!.text = String.format("%1\$s%%", holder.progressBar!!.progress)
        }

    }

    override fun getItemCount(): Int {
        return achievements.size
    }

    init {
        this.activity = activity as MainActivity
        this.achievements = ArrayList(values)
    }

    fun setItemClickCallbacks(itemClickCallbacks: ItemClickCallbacks) {
        this.itemClickCallbacks = itemClickCallbacks
    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @BindView(R.id.achievement_icon)
        var achievementIcon: ImageView? = null
        @BindView(R.id.achievement_name)
        var achievementName: TextView? = null
        @BindView(R.id.achievement_description)
        var achievementDescription: TextView? = null
        @BindView(R.id.unlock_date)
        var unlockDate: TextView? = null
        @BindView(R.id.progress_text)
        var progressText: TextView? = null
        @BindView(R.id.container)
        var container: LinearLayout? = null
        @BindView(R.id.progress_bar)
        var progressBar: MaterialProgressBar? = null
        @BindView(R.id.progress_percentile)
        var progressPercentile: TextView? = null
        @BindView(R.id.achievement_exp)
        var expView: TextView? = null

        init {
            ButterKnife.bind(this, view)
        }
    }
}
