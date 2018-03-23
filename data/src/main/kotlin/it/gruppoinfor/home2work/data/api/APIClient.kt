package it.gruppoinfor.home2work.data.api


import it.gruppoinfor.home2work.domain.entities.ChatEntity
import it.gruppoinfor.home2work.data.api.services.*
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.entities.ShareEntity


object APIClient {

    var user: UserEntity? = null

    var ongoingShare: ShareEntity? = null

    var inbox: ArrayList<ChatEntity> = ArrayList()

    internal var sessionToken: String = ""


    fun getChatService(): ChatService {
        return APIServiceGenerator.createService(ChatService::class.java)
    }

    fun getCompanyService(): CompanyService {
        return APIServiceGenerator.createService(CompanyService::class.java)
    }

    fun getMatchService(): MatchService {
        return APIServiceGenerator.createService(MatchService::class.java)
    }

    fun getShareService(): ShareService {
        return APIServiceGenerator.createService(ShareService::class.java)
    }

    fun getUserService(): UserService {
        return APIServiceGenerator.createService(UserService::class.java)
    }



}
