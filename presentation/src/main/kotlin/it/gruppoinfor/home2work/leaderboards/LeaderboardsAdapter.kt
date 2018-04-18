package it.gruppoinfor.home2work.leaderboards

import android.content.Context
import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
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
import it.gruppoinfor.home2work.entities.UserRanking

class LeaderboardsAdapter(
        private val userId: Long,
        private val imageLoader: ImageLoader,
        private val onClick: (UserRanking, Int) -> Unit
) : BaseAdapter<UserRanking>() {

    companion object {
        const val TOP_10 = 0
        const val TOP_100 = 1
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            TOP_10 -> {
                val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard_user_large, parent, false)
                Top10ViewHolder(parent.context, layoutView)
            }
            TOP_100 -> {
                val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard_user_normal, parent, false)
                Top100ViewHolder(parent.context, layoutView)
            }
            else -> {
                val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard_user_small, parent, false)
                ViewHolder(parent.context, layoutView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val userRanking = items[position]
        when (holder.itemViewType) {
            TOP_10 -> (holder as Top10ViewHolder).bind(userRanking, position, onClick, imageLoader, userId)
            TOP_100 -> (holder as Top100ViewHolder).bind(userRanking, position, onClick, imageLoader, userId)
            else -> (holder as ViewHolder).bind(userRanking, position, onClick, userId)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            in 0..9 -> TOP_10
            in 10..99 -> TOP_100
            else -> 3
        }
    }

    class Top10ViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                userRanking: UserRanking,
                position: Int,
                onClick: (UserRanking, Int) -> Unit,
                imageLoader: ImageLoader,
                userId: Long
        ) = with(itemView as CardView) {

            val container = findViewById<ConstraintLayout>(R.id.container)
            val positionText = findViewById<TextView>(R.id.text_position)
            val userAvatar = findViewById<ImageView>(R.id.img_user_avatar)
            val usernameText = findViewById<TextView>(R.id.text_user_name)
            val companyNameText = findViewById<TextView>(R.id.text_company_name)
            val valueText = findViewById<TextView>(R.id.text_value)

            positionText.text = userRanking.position.toString()

            imageLoader.load(
                    url = userRanking.avatarUrl,
                    imageView = userAvatar,
                    transformation = PicassoCircleTransform(),
                    placeholder = R.drawable.ic_avatar_placeholder)

            usernameText.text = userRanking.userName
            companyNameText.text = userRanking.companyName
            valueText.text = userRanking.amount.toString()


            if (userRanking.userId == userId) {
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
                imageLoader: ImageLoader,
                userId: Long
        ) = with(itemView as CardView) {

            val container = findViewById<ConstraintLayout>(R.id.container)
            val positionText = findViewById<TextView>(R.id.text_position)
            val userAvatar = findViewById<ImageView>(R.id.img_user_avatar)
            val usernameText = findViewById<TextView>(R.id.text_user_name)
            val companyNameText = findViewById<TextView>(R.id.text_company_name)
            val valueText = findViewById<TextView>(R.id.text_value)

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
                container.isClickable = false
                container.isFocusable = false
                usernameText.typeface = Typeface.DEFAULT_BOLD
            } else {
                container.setOnClickListener { onClick(userRanking, position) }
            }

        }

    }

    class ViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                userRanking: UserRanking,
                position: Int,
                onClick: (UserRanking, Int) -> Unit,
                userId: Long
        ) = with(itemView as CardView) {

            val container = findViewById<ConstraintLayout>(R.id.container)
            val positionText = findViewById<TextView>(R.id.text_position)
            val usernameText = findViewById<TextView>(R.id.text_user_name)
            val companyNameText = findViewById<TextView>(R.id.text_company_name)
            val valueText = findViewById<TextView>(R.id.text_value)

            positionText.text = userRanking.position.toString()
            usernameText.text = userRanking.userName
            companyNameText.text = userRanking.companyName
            valueText.text = userRanking.amount.toString()

            if (userRanking.userId == userId) {
                cardElevation = 8f
                container.isClickable = false
                container.isFocusable = false
                usernameText.typeface = Typeface.DEFAULT_BOLD
            } else {
                container.setOnClickListener { onClick(userRanking, position) }
            }

        }

    }
}