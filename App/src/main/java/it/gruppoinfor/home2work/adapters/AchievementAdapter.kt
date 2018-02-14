package it.gruppoinfor.home2work.adapters

import android.app.Activity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.MainActivity
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2workapi.model.Achievement
import kotlinx.android.synthetic.main.item_achievement.view.*
import java.text.SimpleDateFormat
import java.util.*

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
                .into(holder.achievementIcon)

        holder.achievementName.text = achievement.name
        holder.achievementDescription.text = achievement.description
        holder.progressText.text = String.format("%1\$s/%2\$s", achievement.current, achievement.goal)
        holder.progressBar.progress = achievement.progress
        holder.achievementExp.text = "${achievement.exp}"

        val color = ContextCompat.getColor(activity, R.color.colorPrimary)

        if (achievement.progress == 100) {
            holder.unlockDate.visibility = View.VISIBLE
            holder.progressPercentile.visibility = View.GONE
            holder.achievementExp.setTextColor(color)
            val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.ITALIAN)
            val dateString = dateFormat.format(achievement.unlockDate)
            holder.unlockDate.text = dateString
        } else {
            holder.unlockDate.visibility = View.GONE
            holder.progressPercentile.visibility = View.VISIBLE
            holder.progressPercentile.text = String.format("%1\$s%%", holder.progressBar.progress)
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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val achievementIcon: ImageView = itemView.achievement_icon
        val achievementName: TextView = itemView.achievement_name
        val achievementDescription: TextView = itemView.achievement_description
        val progressText: TextView = itemView.progress_text
        val progressBar: ProgressBar = itemView.progress_bar
        val achievementExp: TextView = itemView.achievement_exp
        val unlockDate: TextView = itemView.unlock_date
        val progressPercentile: TextView = itemView.progress_percentile
    }
}
