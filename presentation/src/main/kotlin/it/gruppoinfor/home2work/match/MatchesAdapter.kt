package it.gruppoinfor.home2work.match

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseAdapter
import it.gruppoinfor.home2work.common.ImageLoader
import it.gruppoinfor.home2work.common.PicassoCircleTransform
import it.gruppoinfor.home2work.common.extensions.getScore
import it.gruppoinfor.home2work.common.extensions.remove
import it.gruppoinfor.home2work.common.extensions.show
import it.gruppoinfor.home2work.entities.Match
import it.gruppoinfor.home2work.user.UserActivityLauncher
import kotlinx.android.synthetic.main.item_match.view.*


class MatchesAdapter(
        private val imageLoader: ImageLoader,
        private val onMatchClick: (Match, Int) -> Unit,
        private val onMatchLongClick: (Match, Int) -> Boolean
) : BaseAdapter<Match>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        return ViewHolder(parent.context, layoutView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val match = items[position]
        (holder as ViewHolder).bind(match, position, onMatchClick, onMatchLongClick, imageLoader)
    }


    class ViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                match: Match,
                position: Int,
                onMatchClick: (Match, Int) -> Unit,
                onMatchLongClick: (Match, Int) -> Boolean,
                imageLoader: ImageLoader
        ) = with(itemView) {

            setOnClickListener {
                new_badge.remove()
                onMatchClick(match, position)
            }
            setOnLongClickListener { onMatchLongClick(match, position) }

            new_badge.apply {
                if (match.isNew) show() else remove()
            }

            imageLoader.load(
                    url = match.host.avatarUrl,
                    imageView = match_user_avatar,
                    transformation = PicassoCircleTransform(),
                    placeholder = R.drawable.ic_avatar_placeholder
            )

            text_match_score.remove()
            match.getScore()?.let {
                text_match_score.show()
                val color = getScoreColor(it)
                text_match_score.text = "$it%"
                text_match_score.setTextColor(color)
            }

            text_host_name.text = match.host.fullName
            text_host_job.text = match.host.company?.formattedName
            text_host_home.text = "Da ${match.host.address?.city}"

            match_user_avatar.setOnClickListener {

                val user = match.host

                UserActivityLauncher(
                        userId = user.id,
                        userName = user.fullName,
                        userAvatarUrl = user.avatarUrl,
                        userCompanyId = user.company!!.id,
                        userCompanyName = user.company!!.formattedName
                ).launch(context)

            }

        }

        private fun getScoreColor(score: Int): Int {

            return when {
                score == 0 -> ContextCompat.getColor(context, R.color.light_bg_dark_hint_text)
                score < 60 -> ContextCompat.getColor(context, R.color.red_500)
                score < 70 -> ContextCompat.getColor(context, R.color.orange_600)
                score < 80 -> ContextCompat.getColor(context, R.color.amber_500)
                score < 90 -> ContextCompat.getColor(context, R.color.light_green_500)
                else -> ContextCompat.getColor(context, R.color.green_500)
            }
        }

    }
}
