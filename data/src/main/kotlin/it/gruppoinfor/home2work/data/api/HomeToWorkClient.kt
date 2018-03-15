package it.gruppoinfor.home2work.api


import it.gruppoinfor.home2work.chat.Chat
import it.gruppoinfor.home2work.data.api.services.*
import it.gruppoinfor.home2work.domain.entities.ClientUser
import it.gruppoinfor.home2work.domain.entities.Share

object HomeToWorkClient {

    var user: ClientUser? = null

    var ongoingShare: Share? = null

    var inbox: ArrayList<Chat> = ArrayList()

    internal var sessionToken: String = ""

    fun getAuthService(): AuthService {
        return it.gruppoinfor.home2work.data.api.ServiceGenerator.createService(AuthService::class.java)
    }

    fun getChatService(): ChatService {
        return it.gruppoinfor.home2work.data.api.ServiceGenerator.createService(ChatService::class.java)
    }

    fun getCompanyService(): CompanyService {
        return it.gruppoinfor.home2work.data.api.ServiceGenerator.createService(CompanyService::class.java)
    }

    fun getMatchService(): MatchService {
        return it.gruppoinfor.home2work.data.api.ServiceGenerator.createService(MatchService::class.java)
    }

    fun getShareService(): ShareService {
        return it.gruppoinfor.home2work.data.api.ServiceGenerator.createService(ShareService::class.java)
    }

    fun getUserService(): UserService {
        return it.gruppoinfor.home2work.data.api.ServiceGenerator.createService(UserService::class.java)
    }


}
