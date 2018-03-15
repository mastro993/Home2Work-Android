package it.gruppoinfor.home2work.match

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.chat.ChatActivityArgs
import it.gruppoinfor.home2work.user.UserActivityArgs
import kotlinx.android.synthetic.main.dialog_match_info.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*


class MatchInfoDialog constructor(context: Context, private val match: Match) : AlertDialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_match_info)
        text_container.visibility = View.GONE

        doAsync {
            Thread.sleep(500)
            uiThread {
                initUI()
            }
        }

    }

    private fun initUI() {
        text_container.visibility = View.VISIBLE

        val host = match.host!!


        val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder)

        Glide.with(context)
                .load(match.host?.avatarURL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(match_user_avatar)

        text_host_name.text = host.toString()
        text_host_home.text = "Da ${host.address.city}"
        text_host_job.text = host.company.toString()

        text_home_score.text = "${match.homeScore}%"
        text_home_score.setTextColor(getScoreColor(match.homeScore))

        text_job_score.text = "${match.jobScore}%"
        text_job_score.setTextColor(getScoreColor(match.jobScore))

        text_time_score.text = "${match.timeScore}%"
        text_time_score.setTextColor(getScoreColor(match.timeScore))

        val df = SimpleDateFormat("HH:mm", Locale.ITALY)


        text_matched_time.text = "${df.format(match.arrivalTime)} - ${df.format(match.departureTime)}"
        val distanceInKm = match.distance.div(1000.0)
        text_matched_distance.text = "${String.format("%.2f", distanceInKm)} Km in comune"


        user_container.setOnClickListener {
            UserActivityArgs(
                    userId = host.id,
                    userName = host.toString(),
                    userAvatarUrl = host.avatarURL,
                    userCompanyId = host.company.id,
                    userCompanyName = host.company.name
            ).launch(context)
        }

        button_send_message.setOnClickListener {
            ChatActivityArgs(
                    chatId = 0L,
                    recipientId = host.id,
                    recipientName = host.name
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