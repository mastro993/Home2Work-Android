package it.gruppoinfor.home2work.api


import it.gruppoinfor.home2work.domain.entities.ChatEntity
import it.gruppoinfor.home2work.data.api.ServiceGenerator
import it.gruppoinfor.home2work.data.api.services.*
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.entities.ShareEntity

object HomeToWorkClient {

    var user: UserEntity? = null

    var ongoingShare: ShareEntity? = null

    var inbox: ArrayList<ChatEntity> = ArrayList()

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
