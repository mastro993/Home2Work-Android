package it.gruppoinfor.home2work.leaderboards

import android.content.Context
import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseAdapter
import it.gruppoinfor.home2work.common.ImageLoader
import it.gruppoinfor.home2work.common.PicassoCircleTransform
import it.gruppoinfor.home2work.common.extensions.remove
import it.gruppoinfor.home2work.entities.UserRanking
import kotlinx.android.synthetic.main.item_leaderboard_user_large.view.*

class LeaderboardsAdapter(
        private val userId: Long,
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
            TOP_3 -> (holder as Top3ViewHolder).bind(userRanking, position, onClick, imageLoader, userId)
            TOP_10 -> (holder as Top10ViewHolder).bind(userRanking, position, onClick, imageLoader, userId)
            else -> (holder as Top100ViewHolder).bind(userRanking, position, onClick, userId)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            in 0..2 -> TOP_3
            in 3..9 -> TOP_10
            else -> TOP_100
        }
    }

    class Top3ViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                userRanking: UserRanking,
                position: Int,
                onClick: (UserRanking, Int) -> Unit,
                imageLoader: ImageLoader,
                userId: Long
        ) = with(itemView) {

            val container = findViewById<ConstraintLayout>(R.id.container)
            val userAvatar = findViewById<ImageView>(R.id.img_user_avatar)
            val usernameText = findViewById<TextView>(R.id.text_user_name)
            val companyNameText = findViewById<TextView>(R.id.text_company_name)
            val valueText = findViewById<TextView>(R.id.text_value)
            val divider = findViewById<View>(R.id.item_divider)

            when (userRanking.position) {
                1 -> img_prize.setImageDrawable(context.getDrawable(R.drawable.ic_leaderboard_first_place))
                2 -> img_prize.setImageDrawable(context.getDrawable(R.drawable.ic_leaderboard_second_place))
                else -> img_prize.setImageDrawable(context.getDrawable(R.drawable.ic_leaderboard_third_place))
            }

            imageLoader.load(
                    url = userRanking.avatarUrl,
                    imageView = userAvatar,
                    transformation = PicassoCircleTransform(),
                    placeholder = R.drawable.ic_avatar_placeholder)

            usernameText.text = userRanking.userName
            companyNameText.text = userRanking.companyName
            valueText.text = userRanking.amount.toString()


            if (userRanking.userId == userId) {
                elevation = 8f
                divider.remove()
                //setBackgroundColor(ContextCompat.getColor(context, R.color.grey_100))
                container.isClickable = false
                container.isFocusable = false
                usernameText.typeface = Typeface.DEFAULT_BOLD
            } else {
                container.setOnClickListener { onClick(userRanking, position) }
            }


        }

    }

    class Top10ViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                userRanking: UserRanking,
                position: Int,
                onClick: (UserRanking, Int) -> Unit,
                imageLoader: ImageLoader,
                userId: Long
        ) = with(itemView) {

            val container = findViewById<ConstraintLayout>(R.id.container)
            val userAvatar = findViewById<ImageView>(R.id.img_user_avatar)
            val usernameText = findViewById<TextView>(R.id.text_user_name)
            val companyNameText = findViewById<TextView>(R.id.text_company_name)
            val valueText = findViewById<TextView>(R.id.text_value)
            val divider = findViewById<View>(R.id.item_divider)
            val positionText = findViewById<TextView>(R.id.text_position)

            imageLoader.load(
                    url = userRanking.avatarUrl,
                    imageView = userAvatar,
                    transformation = PicassoCircleTransform(),
                    placeholder = R.drawable.ic_avatar_placeholder)

            positionText.text = userRanking.position.toString()
            usernameText.text = userRanking.userName
            companyNameText.text = userRanking.companyName
            valueText.text = userRanking.amount.toString()

            if (userRanking.userId == userId) {
                elevation = 8f
                divider.remove()
                //setBackgroundColor(ContextCompat.getColor(context, R.color.grey_100))
                container.isClickable = false
                container.isFocusable = false
                usernameText.typeface = Typeface.DEFAULT_BOLD
            } else {
                container.setOnClickListener { onClick(userRanking, position) }
            }

        }

    }

    class Top100ViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                userRanking: UserRanking,
                position: Int,
                onClick: (UserRanking, Int) -> Unit,
                userId: Long
        ) = with(itemView) {

            val container = findViewById<ConstraintLayout>(R.id.container)
            val usernameText = findViewById<TextView>(R.id.text_user_name)
            val companyNameText = findViewById<TextView>(R.id.text_company_name)
            val valueText = findViewById<TextView>(R.id.text_value)
            val divider = findViewById<View>(R.id.item_divider)
            val positionText = findViewById<TextView>(R.id.text_position)

            positionText.text = userRanking.position.toString()
            usernameText.text = userRanking.userName
            companyNameText.text = userRanking.companyName
            valueText.text = userRanking.amount.toString()

            if (userRanking.userId == userId) {
                elevation = 8f
                divider.remove()
                //setBackgroundColor(ContextCompat.getColor(context, R.color.grey_100))
                container.isClickable = false
                container.isFocusable = false
                usernameText.typeface = Typeface.DEFAULT_BOLD
            } else {
                container.setOnClickListener { onClick(userRanking, position) }
            }

        }

    }
}