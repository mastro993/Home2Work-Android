package it.gruppoinfor.home2workapi


import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2workapi.callback.LoginCallback
import it.gruppoinfor.home2workapi.inbox.Chat
import it.gruppoinfor.home2workapi.inbox.Message
import it.gruppoinfor.home2workapi.model.*
import it.gruppoinfor.home2workapi.service.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

object HomeToWorkClient {

    const val AVATAR_BASE_URL = "http://home2workapi.azurewebsites.net/images/avatar/"
    const val COMPANIES_BASE_URL = "http://home2workapi.azurewebsites.net/images/companies/"
    const val ACHIEVEMENTS_BASE_URL = "http://home2workapi.azurewebsites.net/images/achievements/"
    const val HEADER_SESSION_TOKEN = "X-User-Session-Token"

    var user: ClientUser? = null
        private set

    var chatList: List<Chat>? = null
        private set

    private var sessionToken: String = ""

    /**
     * Effettua il login di un utente con le credenziali passate
     */
    fun login(email: String, password: String, loginCallback: LoginCallback) {

        val service = ServiceGenerator.createService(AuthService::class.java)

        service.login(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    when (response.code()) {
                        404 -> loginCallback.onInvalidCredential()
                        200 -> {

                            val sToken = response.headers()[HEADER_SESSION_TOKEN]
                            if (sToken == null)
                                loginCallback.onError(null)
                            else {
                                sessionToken = sToken
                                user = response.body()!!
                                loginCallback.onLoginSuccess()
                            }
                        }
                        else -> loginCallback.onError(null)
                    }
                }, {
                    loginCallback.onError(it)
                })

    }

    /**
     * Effettua il login di un utente tramite l'Access Token
     */
    fun login(token: String, loginCallback: LoginCallback) {

        val service = ServiceGenerator.createService(AuthService::class.java)

        service.login(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    when (response.code()) {
                        404 -> loginCallback.onInvalidCredential()
                        200 -> {
                            
                            val sToken = response.headers()[HEADER_SESSION_TOKEN]
                            if (sToken == null)
                                loginCallback.onError(null)
                            else {
                                sessionToken = sToken
                                user = response.body()!!
                                loginCallback.onLoginSuccess()
                            }
                            
                        }
                        else -> loginCallback.onError(null)
                    }
                }, {
                    loginCallback.onError(it)
                })

    }

    /**
     * Imposta nel server il token unico per la FMC Platform
     */
    fun updateFcmToken(fcmToken: String?) {

        val service = ServiceGenerator.createService(UserService::class.java, sessionToken)

        if (fcmToken == null) return
        service.updateFCMToken(fcmToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ -> }, { it.printStackTrace() })

    }

    /**
     * Carica l'avatar sul server
     */
    fun uploadAvatar(avatarBody: MultipartBody.Part, onSuccessListener: OnSuccessListener<ResponseBody>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(UserService::class.java, sessionToken)

        service.uploadAvatar(avatarBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->

                    if (response.code() == 201)
                        onSuccessListener.onSuccess(response.body())
                    else
                        onFailureListener.onFailure(Exception("Response code " + response.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Aggiorna le informazioni dell'utente collegato.
     */
    fun updateUser(onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(UserService::class.java, sessionToken)

        service.edit(user!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->

                    if (response.code() == 200) {
                        user = response.body()!!
                        onSuccessListener.onSuccess(null)
                    } else
                        onFailureListener.onFailure(Exception("Response code " + response.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Aggiorna l'utente collegato.
     */
    fun refreshUser(onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(UserService::class.java, sessionToken)

        service.get()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->

                    if (response.code() == 200) {
                        user = response.body()!!
                        onSuccessListener.onSuccess(null)
                    } else
                        onFailureListener.onFailure(Exception("Response code: " + response.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Ottiene il profilo dell'utente collegato
     */
    fun getProfile(onSuccessListener: OnSuccessListener<UserProfile>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(UserService::class.java, sessionToken)

        service.getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userResponse ->

                    if (userResponse.code() == 200) {
                        onSuccessListener.onSuccess(userResponse.body())
                    } else
                        onFailureListener.onFailure(Exception("Response code: " + userResponse.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Ottiene la lista dei match dell'utente
     */
    fun getMatchList(onSuccessListener: OnSuccessListener<ArrayList<Match>>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(UserService::class.java, sessionToken)

        service.getMatchList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ listResponse ->

                    if (listResponse.code() == 200)
                        onSuccessListener.onSuccess(listResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code: " + listResponse.code()))

                }, { throwable ->
                    onFailureListener.onFailure(Exception(throwable))
                })

    }

    /**
     * Modifica le informazioni di un match
     */
    fun editMatch(match: Match, onSuccessListener: OnSuccessListener<Match>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(MatchService::class.java, sessionToken)

        service.editMatch(match)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ matchResponse ->

                    if (matchResponse.code() == 200)
                        onSuccessListener.onSuccess(matchResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code: " + matchResponse.code()))

                }, { it.printStackTrace() })
    }

    /**
     * Modifica le informazioni di un match (Metodo senza callbacks)
     */
    fun editMatch(match: Match) {

        val service = ServiceGenerator.createService(MatchService::class.java, sessionToken)

        service.editMatch(match)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

    }

    /**
     * Crea una nuova condivisione sul server e la avvia
     */
    fun createNewShare(onSuccessListener: OnSuccessListener<Share>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(ShareService::class.java, sessionToken)

        service.createNewShare(user!!.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ shareResponse ->

                    if (shareResponse.code() == 200)
                        onSuccessListener.onSuccess(shareResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code " + shareResponse.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Annulla una condivisione
     */
    fun cancelShare(shareId: Long, onSuccessListener: OnSuccessListener<ResponseBody>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(ShareService::class.java, sessionToken)

        service.cancelShare(shareId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ shareResponse ->

                    if (shareResponse.code() == 200)
                        onSuccessListener.onSuccess(shareResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code " + shareResponse.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Abbandona una condivisione alla quale si sta partecipando come ospiti.
     */
    fun leaveShare(share: Share, onSuccessListener: OnSuccessListener<Share>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(ShareService::class.java, sessionToken)

        service.leaveShare(share.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ shareResponse ->

                    if (shareResponse.code() == 200)
                        onSuccessListener.onSuccess(shareResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code " + shareResponse.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Espelle l'utente dalla condivisione nella quale si è host
     */
    fun banGuestFromShare(shareId: Long?, guestId: Long?, onSuccessListener: OnSuccessListener<Share>, onFailureListener: OnFailureListener) {

        if (shareId == null || guestId == null)
            onFailureListener.onFailure(IllegalArgumentException())
        else {
            val service = ServiceGenerator.createService(ShareService::class.java, sessionToken)

            service.banGuestFromShare(shareId, guestId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ shareResponse ->

                        if (shareResponse.code() == 200)
                            onSuccessListener.onSuccess(shareResponse.body())
                        else
                            onFailureListener.onFailure(Exception("Response code " + shareResponse.code()))

                    }, { it.printStackTrace() })
        }

    }

    /**
     * Ottiene informazioni su una condivisione con l'ID fornito
     */
    fun getShare(shareId: Long?, onSuccessListener: OnSuccessListener<Share>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(ShareService::class.java, sessionToken)

        service.getShareById(shareId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ shareResponse ->

                    if (shareResponse.code() == 200)
                        onSuccessListener.onSuccess(shareResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code " + shareResponse.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Ottiene la lista delle aziende presenti nel sistema.
     */
    fun getCompanyList(onSuccessListener: OnSuccessListener<ArrayList<Company>>) {

        val service = ServiceGenerator.createService(CompanyService::class.java, sessionToken)

        service.companies
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ companiesResponse ->

                    if (companiesResponse.code() == 200)
                        onSuccessListener.onSuccess(companiesResponse.body())

                }, { it.printStackTrace() })

    }

    /**
     * Ottiene la lista delle condivisioni dell'utente, in corso e completate.
     */
    fun getShareList(onSuccessListener: OnSuccessListener<ArrayList<Share>>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(UserService::class.java, sessionToken)

        service.getShareList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ listResponse ->

                    if (listResponse.code() == 200)
                        onSuccessListener.onSuccess(listResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code: " + listResponse.code()))

                }, { it.printStackTrace() })
    }

    /**
     * Unisce ad una condivisione l'utente collegato
     */
    fun joinShare(shareId: Long?, joinLocation: android.location.Location, onSuccessListener: OnSuccessListener<Share>, onFailureListener: OnFailureListener) {

        if (shareId == null)
            onFailureListener.onFailure(IllegalArgumentException())
        else {
            val locationString = joinLocation.latitude.toString() + "," + joinLocation.longitude

            val service = ServiceGenerator.createService(ShareService::class.java, sessionToken)

            service.joinShare(shareId, locationString)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ responseBodyResponse ->

                        if (responseBodyResponse.code() == 200)
                            onSuccessListener.onSuccess(responseBodyResponse.body())
                        else
                            onFailureListener.onFailure(Exception("Response code: " + responseBodyResponse.code()))

                    }, { it.printStackTrace() })
        }

    }

    /**
     * Imposta come completata una condivisione
     */
    fun completeShare(share: Share, joinLocation: android.location.Location, onSuccessListener: OnSuccessListener<Share>, onFailureListener: OnFailureListener) {

        val locationString = joinLocation.latitude.toString() + "," + joinLocation.longitude

        val service = ServiceGenerator.createService(ShareService::class.java, sessionToken)

        service.completeShare(share.id, locationString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ responseBodyResponse ->

                    if (responseBodyResponse.code() == 200)
                        onSuccessListener.onSuccess(responseBodyResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code: " + responseBodyResponse.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Completa una condivsione da parte di un ospite
     */
    fun finishShare(share: Share, onSuccessListener: OnSuccessListener<Share>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(ShareService::class.java, sessionToken)

        service.finishShare(share.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ responseBodyResponse ->

                    if (responseBodyResponse.code() == 200)
                        onSuccessListener.onSuccess(responseBodyResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code: " + responseBodyResponse.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Effettua la sincronizzazione delle posizioni dell'utente
     */
    fun uploadLocations(userId: Long, routeLocationList: List<RouteLocation>, onSuccessListener: OnSuccessListener<List<RouteLocation>>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(UserService::class.java, sessionToken)

        service.uploadLocations(routeLocationList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ listResponse ->

                    if (listResponse.code() == 200)
                        onSuccessListener.onSuccess(listResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code" + listResponse.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Ottiene l'utente con l'id fornito
     */
    fun getUserById(userId: Long?, onSuccessListener: OnSuccessListener<User>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(UserService::class.java, sessionToken)

        service.getUserById(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->

                    if (response.code() == 200) {
                        onSuccessListener.onSuccess(response.body())
                    } else
                        onFailureListener.onFailure(Exception("Response code: " + response.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Ottiene il profilo dell'utente con l'id fornito
     */
    fun getUserProfileById(userId: Long?, onSuccessListener: OnSuccessListener<UserProfile>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(UserService::class.java, sessionToken)

        service.getUserProfileById(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userResponse ->

                    if (userResponse.code() == 200) {
                        onSuccessListener.onSuccess(userResponse.body())
                    } else
                        onFailureListener.onFailure(Exception("Response code: " + userResponse.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Ottiene la lista delel chat dell'utente collegato
     */
    fun getChatList(onSuccessListener: OnSuccessListener<List<Chat>>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(UserService::class.java, sessionToken)

        service.getChatList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->

                    if (response.code() == 200) {
                        chatList = response.body()
                        onSuccessListener.onSuccess(response.body())
                    } else
                        onFailureListener.onFailure(Exception("Response code: " + response.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Ottiene i messaggi della chat con l'id fornito
     */
    fun getChatMessageList(chatId: String, onSuccessListener: OnSuccessListener<List<Message>>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(ChatService::class.java, sessionToken)

        service.getChatMessageList(chatId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->

                    if (response.code() == 200) {
                        onSuccessListener.onSuccess(response.body())
                    } else
                        onFailureListener.onFailure(Exception("Response code: " + response.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Invia un messaggio alla chat con l'id fornito
     */
    fun sendMessageToChat(chatId: String, text: String, onSuccessListener: OnSuccessListener<Response<ResponseBody>>, onFailureListener: OnFailureListener) {

        val service = ServiceGenerator.createService(ChatService::class.java, sessionToken)

        service.sendMessageToChat(chatId, text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->

                    if (response.code() == 200) {
                        onSuccessListener.onSuccess(response)
                    } else
                        onFailureListener.onFailure(Exception("Response code: " + response.code()))

                }, { it.printStackTrace() })

    }


}
