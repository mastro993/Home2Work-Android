package it.gruppoinfor.home2work.leaderboards

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseAdapter
import it.gruppoinfor.home2work.common.ImageLoader
import it.gruppoinfor.home2work.entities.UserRanking
import kotlinx.android.synthetic.main.item_leaderboard_user_large.view.*

class LeaderboardsAdapter(
        private val imageLoader: ImageLoader,
        private val onClick: (UserRanking, Int) -> Unit
) : BaseAdapter<UserRanking>() {

    companion object {
        const val TOP_3 = 0
        const val TOP_10 = 1
        const val TOP_100 = 2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            TOP_3 -> {
                val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard_user_large, parent, false)
                Top3ViewHolder(parent.context, layoutView)
            }
            TOP_10 -> {
                val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard_user_normal, parent, false)
                Top10ViewHolder(parent.context, layoutView)
            }
            else -> {
                val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard_user_small, parent, false)
                Top100ViewHolder(parent.context, layoutView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val userRanking = items[position]
        when (holder.itemViewType) {
            TOP_3 -> (holder as Top3ViewHolder).bind(userRanking, position, onClick, imageLoader)
            TOP_10 -> (holder as Top10ViewHolder).bind(userRanking, position, onClick, imageLoader)
            else -> (holder as Top100ViewHolder).bind(userRanking, position, onClick)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            in 1..3 -> TOP_3
            in 4..10 -> TOP_10
            else -> TOP_100
        }
    }

    class Top3ViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                userRanking: UserRanking,
                position: Int,
                onClick: (UserRanking, Int) -> Unit,
                imageLoader: ImageLoader
        ) = with(itemView) {

            when (userRanking.position) {
                1 -> img_prize.setImageDrawable(context.getDrawable(R.drawable.ic_leaderboard_first_place))
                2 -> img_prize.setImageDrawable(context.getDrawable(R.drawable.ic_leaderboard_second_place))
                else -> img_prize.setImageDrawable(context.getDrawable(R.drawable.ic_leaderboard_third_place))
            }

            imageLoader.load(userRanking.avatarUrl, findViewById(R.id.img_user_avatar))
            findViewById<TextView>(R.id.text_user_name).text = userRanking.userName
            findViewById<TextView>(R.id.text_company_name).text = userRanking.companyName
            findViewById<TextView>(R.id.text_value).text = userRanking.amount.toString()

        }

    }

    class Top10ViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                userRanking: UserRanking,
                position: Int,
                onClick: (UserRanking, Int) -> Unit,
                imageLoader: ImageLoader
        ) = with(itemView) {

            findViewById<TextView>(R.id.text_position).text = userRanking.position.toString()
            imageLoader.load(userRanking.avatarUrl, findViewById(R.id.img_user_avatar))
            findViewById<TextView>(R.id.text_user_name).text = userRanking.userName
            findViewById<TextView>(R.id.text_company_name).text = userRanking.companyName
            findViewById<TextView>(R.id.text_value).text = userRanking.amount.toString()

        }

    }

    class Top100ViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                userRanking: UserRanking,
                position: Int,
                onClick: (UserRanking, Int) -> Unit
        ) = with(itemView) {

            findViewById<TextView>(R.id.text_position).text = userRanking.position.toString()
            findViewById<TextView>(R.id.text_user_name).text = userRanking.userName
            findViewById<TextView>(R.id.text_company_name).text = userRanking.companyName
            findViewById<TextView>(R.id.text_value).text = userRanking.amount.toString()

        }

    }
}