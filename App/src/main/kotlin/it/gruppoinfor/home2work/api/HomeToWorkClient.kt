package it.gruppoinfor.home2work.api


import it.gruppoinfor.home2work.auth.AuthService
import it.gruppoinfor.home2work.auth.AuthUser
import it.gruppoinfor.home2work.chat.Chat
import it.gruppoinfor.home2work.chat.ChatService
import it.gruppoinfor.home2work.company.CompanyService
import it.gruppoinfor.home2work.match.MatchService
import it.gruppoinfor.home2work.share.Share
import it.gruppoinfor.home2work.share.ShareService
import it.gruppoinfor.home2work.user.UserService

object HomeToWorkClient {

    internal const val BASE_URL_OLD = "http://home2workapi.azurewebsites.net"
    internal const val BASE_URL = "https://hometoworkapi.azurewebsites.net"
    internal const val AVATAR_BASE_URL = "${BASE_URL_OLD}/images/avatar/"
    internal const val COMPANIES_BASE_URL = "${BASE_URL}/images/companies/"
    internal const val ACHIEVEMENTS_BASE_URL = "${BASE_URL}/images/achievements/"
    internal const val HEADER_SESSION_TOKEN = "X-User-Session-Token"

    var user: AuthUser? = null

    var ongoingShare: Share? = null

    var inbox: ArrayList<Chat> = ArrayList()

    internal var sessionToken: String = ""

    fun getAuthService(): AuthService {
        return ServiceGenerator.createService(AuthService::class.java)
    }

    fun getChatService(): ChatService {
        return ServiceGenerator.createService(ChatService::class.java)
    }

    fun getCompanyService(): CompanyService {
        return ServiceGenerator.createService(CompanyService::class.java)
    }

    fun getMatchService(): MatchService {
        return ServiceGenerator.createService(MatchService::class.java)
    }

    fun getShareService(): ShareService {
        return ServiceGenerator.createService(ShareService::class.java)
    }

    fun getUserService(): UserService {
        return ServiceGenerator.createService(UserService::class.java)
    }


}
