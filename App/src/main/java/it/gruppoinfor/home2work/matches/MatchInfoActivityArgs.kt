package it.gruppoinfor.home2work.matches

import android.content.Context
import android.content.Intent
import it.gruppoinfor.home2work.common.ActivityArgs
import it.gruppoinfor.home2workapi.match.Match
import org.jetbrains.anko.intentFor

class MatchInfoActivityArgs(
        val match: Match
) : ActivityArgs {


    override fun intent(activity: Context): Intent = activity.intentFor<MatchInfoActivity>()
            .apply {
                putExtra(KEY_MATCH, match)
            }

    companion object {

        private const val KEY_MATCH: String = "match"

        fun deserializeFrom(intent: Intent): MatchInfoActivityArgs {
            return MatchInfoActivityArgs(
                    match = intent.getSerializableExtra(KEY_MATCH) as Match
            )
        }
    }

}