package it.gruppoinfor.home2work.match

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.chat.ChatActivityLauncher
import it.gruppoinfor.home2work.common.ImageLoader
import it.gruppoinfor.home2work.common.PicassoCircleTransform
import it.gruppoinfor.home2work.common.extensions.remove
import it.gruppoinfor.home2work.entities.Match
import it.gruppoinfor.home2work.user.UserActivityLancher
import kotlinx.android.synthetic.main.dialog_match_info.*
import kotlinx.android.synthetic.main.item_match.*
import java.text.SimpleDateFormat
import java.util.*


class MatchInfoDialog constructor(context: Context, private val imageLoader: ImageLoader, private val match: Match) : AlertDialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_match_info)
        text_match_score.remove()

        initUI()

    }

    private fun initUI() {

        val host = match.host


        imageLoader.load(
                url = host.avatarUrl,
                imageView = match_user_avatar,
                transformation = PicassoCircleTransform(),
                placeholder = R.drawable.ic_avatar_placeholder
        )

        text_host_name.text = host.fullName
        text_host_home.text = "Da ${host.address?.city}"
        text_host_job.text = host.company?.formattedName

        text_home_score.text = "${match.homeScore}%"
        text_home_score.setTextColor(getScoreColor(match.homeScore))

        text_job_score.text = "${match.jobScore}%"
        text_job_score.setTextColor(getScoreColor(match.jobScore))

        text_time_score.text = "${match.timeScore}%"
        text_time_score.setTextColor(getScoreColor(match.timeScore))

        val df = SimpleDateFormat("HH:mm", Locale.ITALY)


        text_matched_time.text = "${df.format(match.arrivalTime)} - ${df.format(match.departureTime)}"
        val distanceInKm = match.distance?.div(1000.0)
        text_matched_distance.text = "${String.format("%.2f", distanceInKm)} Km in comune"


        match_user_container.setOnClickListener {
            UserActivityLancher(
                    userId = host.id,
                    userName = host.toString(),
                    userAvatarUrl = host.avatarUrl,
                    userCompanyId = host.company!!.id,
                    userCompanyName = host.company!!.name
            ).launch(context)
        }


        button_send_message.setOnClickListener {
            ChatActivityLauncher(
                    chatId = 0L,
                    recipientId = host.id,
                    recipientName = host.name
            ).launch(context)
        }


    }

    private fun getScoreColor(score: Int?): Int {

        return score?.let {
            when {
                it == 0 -> ContextCompat.getColor(context, R.color.light_bg_dark_hint_text)
                it < 60 -> ContextCompat.getColor(context, R.color.red_500)
                it < 70 -> ContextCompat.getColor(context, R.color.orange_600)
                it < 80 -> ContextCompat.getColor(context, R.color.amber_500)
                it < 90 -> ContextCompat.getColor(context, R.color.light_green_500)
                else -> ContextCompat.getColor(context, R.color.green_500)
            }
        } ?: ContextCompat.getColor(context, R.color.light_bg_dark_hint_text)
    }


}