package it.gruppoinfor.home2work.user

import android.content.Context
import android.content.Intent
import it.gruppoinfor.home2work.common.ActivityLancher
import org.jetbrains.anko.intentFor


class UserActivityLancher(
        val userId: Long,
        val userName: String,
        val userAvatarUrl: String,
        val userCompanyId: Long,
        val userCompanyName: String
) : ActivityLancher {


    override fun intent(activity: Context): Intent = activity.intentFor<UserActivity>()
            .apply {
                putExtra(KEY_USER_ID, userId)
                putExtra(KEY_USER_NAME, userName)
                putExtra(KEY_USER_AVATAR, userAvatarUrl)
                putExtra(KEY_USER_COMPANY_ID, userCompanyId)
                putExtra(KEY_USER_COMPANY_NAME, userCompanyName)
            }

    companion object {

        private const val KEY_USER_ID: String = "user"
        private const val KEY_USER_NAME: String = "user_name"
        private const val KEY_USER_AVATAR: String = "user_avatar"
        private const val KEY_USER_COMPANY_ID: String = "user_company_id"
        private const val KEY_USER_COMPANY_NAME: String = "user_company_name"

        fun deserializeFrom(intent: Intent): UserActivityLancher {
            return UserActivityLancher(
                    userId = intent.getLongExtra(KEY_USER_ID, 0L),
                    userName = intent.getStringExtra(KEY_USER_NAME),
                    userAvatarUrl = intent.getStringExtra(KEY_USER_AVATAR),
                    userCompanyId = intent.getLongExtra(KEY_USER_COMPANY_ID, 0L),
                    userCompanyName = intent.getStringExtra(KEY_USER_COMPANY_NAME)
            )
        }
    }

}