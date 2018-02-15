package it.gruppoinfor.home2workapi


import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2workapi.interfaces.LoginCallback
import it.gruppoinfor.home2workapi.model.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class HomeToWorkClient private constructor() {

    private val mAPI: EndpointInterface

    init {
        ++myInstancesCount

        val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()

        val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        mAPI = retrofit.create(EndpointInterface::class.java)
    }

    /**
     * Effettua il login di un utente con le credenziali passate
     *
     * @param email           String Email utente
     * @param password        String Pasword dell'utente
     * @param isPasswordToken boolean Flag per indicare se si sta accedendo con un token (true) oppure con la password (false)
     * @param loginCallback   LoginCallback Callback per il login
     */
    fun login(email: String, password: String, isPasswordToken: Boolean, loginCallback: LoginCallback) {

        mAPI.login(email, password, isPasswordToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userResponse ->
                    when (userResponse.code()) {
                        404 -> loginCallback.onInvalidCredential()
                        200 -> {
                            user = userResponse.body()
                            loginCallback.onLoginSuccess(user!!)
                        }
                        else -> loginCallback.onLoginError()
                    }
                }, { loginCallback.onError(it) })

    }

    /**
     * Imposta nel server il token unico per la FMC Platform
     *
     * @param fcmToken Token generato per Firebase Messaging Cloud platform
     */
    fun setFcmToken(fcmToken: String?) {

        if (fcmToken == null) return

        mAPI.setFCMToken(user!!.id, fcmToken)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ -> }, { it.printStackTrace() })

    }

    /**
     * Carica l'avatar sul server
     *
     * @param avatarBody        Immagine avatar
     * @param onSuccessListener Callback in caso di caricamento avvenuto con successo
     * @param onFailureListener Callback in caso di errori del server
     */
    fun uploadAvatar(avatarBody: MultipartBody.Part, onSuccessListener: OnSuccessListener<ResponseBody>, onFailureListener: OnFailureListener) {

        mAPI.uploadAvatar(user!!.id, avatarBody)
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
     * Ottiene la lista dei match dell'utente
     *
     * @param onSuccessListener Callback in caso di caricamento avvenuto con successo. Restituisce una lista di match
     * @param onFailureListener Callback in caso di errori del server
     */
    fun getUserMatches(onSuccessListener: OnSuccessListener<ArrayList<Match>>, onFailureListener: OnFailureListener) {

        mAPI.getMatches(user!!.id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ listResponse ->

                    if (listResponse.code() == 200)
                        onSuccessListener.onSuccess(listResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code: " + listResponse.code()))

                }, { throwable -> onFailureListener.onFailure(Exception(throwable)) })

    }

    /**
     * Modifica le informazioni di un match
     *
     * @param match             Match aggiornato da salvarne in cambiamenti sul server
     * @param onSuccessListener Callback in caso di modifica avvenuta con successo. Restituisce il match aggiornato
     * @param onFailureListener Callback in caso di errori del server
     */
    fun editMatch(match: Match, onSuccessListener: OnSuccessListener<Match>, onFailureListener: OnFailureListener) {

        mAPI.editMatch(match)
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
     *
     * @param match Match aggiornato da salvarne in cambiamenti sul server
     */
    fun editMatch(match: Match) {

        mAPI.editMatch(match)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

    }

    /**
     * Crea una nuova condivisione sul server e la avvia
     *
     * @param onSuccessListener Callback in caso di creazione avvenuta con successo. Restituisce la condivisione appena creata
     * @param onFailureListener Callback in caso di errori del server
     */
    fun createShare(onSuccessListener: OnSuccessListener<Share>, onFailureListener: OnFailureListener) {

        mAPI.createShare(user!!.id)
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
     *
     * @param share             Condivisione da annullare
     * @param onSuccessListener Callback in caso di eliminazione avvenuta con successo.
     * @param onFailureListener Callback in caso di errori del server.
     */
    fun cancelShare(share: Share, onSuccessListener: OnSuccessListener<ResponseBody>, onFailureListener: OnFailureListener) {

        mAPI.cancelShare(share.id, user!!.id)
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
     *
     * @param share             Condivisione da abbandonare
     * @param onSuccessListener Callback in caso di abbandono avvenuto con successo. Restituisce la condivisione aggiornata.
     * @param onFailureListener Callback in caso di errori del server.
     */
    fun leaveShare(share: Share, onSuccessListener: OnSuccessListener<Share>, onFailureListener: OnFailureListener) {

        mAPI.leaveShare(share.id, user!!.id)
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
     *
     * @param share             Condivisione
     * @param guest             Utente da espellere
     * @param onSuccessListener Callback in caso di espulsione avvenuta con successo. Restituisce la condivisione aggiornata
     * @param onFailureListener Callback in caso di errori del server.
     */
    fun expelGuest(share: Share, guest: Guest, onSuccessListener: OnSuccessListener<Share>, onFailureListener: OnFailureListener) {

        mAPI.leaveShare(share.id, guest.user.id)
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
     * Ottiene informazioni su una condivisione con l'ID fornito
     *
     * @param sID               ID della condivisione
     * @param onSuccessListener Callback in caso di caricamento avvenuto con successo. Restituisce la condivisione ottenuta
     * @param onFailureListener Callback in caso di errori del server.
     */
    fun getShare(sID: Long?, onSuccessListener: OnSuccessListener<Share>, onFailureListener: OnFailureListener) {

        mAPI.getShare(sID)
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
     * Aggiorna le informazioni dell'utente collegato.
     *
     * @param onSuccessListener Callback in caso di aggiornato avvenuto con successo.
     * @param onFailureListener Callback in caso di errori del server.
     */
    fun updateUser(onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {

        mAPI.updateUser(user!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->

                    if (response.code() == 200) {
                        user = response.body()
                        onSuccessListener.onSuccess(null)
                    } else
                        onFailureListener.onFailure(Exception("Response code " + response.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Ottiene la lista delle aziende presenti nel sistema.
     *
     * @param onSuccessListener Callback in caso di caricamento avvenuto con successo. Restituisce la lista delle aziende
     */
    fun getCompanies(onSuccessListener: OnSuccessListener<ArrayList<Company>>) {

        mAPI.companies
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ companiesResponse ->

                    if (companiesResponse.code() == 200)
                        onSuccessListener.onSuccess(companiesResponse.body())

                }, { it.printStackTrace() })

    }

    /**
     * Aggiorna l'utente collegato.
     *
     * @param onSuccessListener Callback in caso di aggiornamento avvenuto con successo
     * @param onFailureListener Callback in caso di errori del server.
     */
    fun refreshUser(onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {

        mAPI.getUser(user!!.id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userResponse ->

                    if (userResponse.code() == 200) {
                        user = userResponse.body()
                        onSuccessListener.onSuccess(null)
                    } else
                        onFailureListener.onFailure(Exception("Response code: " + userResponse.code()))

                }, { it.printStackTrace() })

    }

    /**
     * Ottiene la lista delle condivisioni dell'utente, in corso e completate.
     *
     * @param onSuccessListener Callback in caso di caricamento avvenuto con successo. Restituisce la lista delle condivisioni.
     * @param onFailureListener Callback in caso di errori del server.
     */
    fun getUserShares(onSuccessListener: OnSuccessListener<ArrayList<Share>>, onFailureListener: OnFailureListener) {

        mAPI.getShares(user!!.id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ listResponse ->

                    if (listResponse.code() == 200)
                        onSuccessListener.onSuccess(listResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code: " + listResponse.code()))

                }, { it.printStackTrace() })
    }

    fun joinShare(shareId: Long?, joinLocation: android.location.Location, onSuccessListener: OnSuccessListener<Share>, onFailureListener: OnFailureListener) {

        val locationString = joinLocation.latitude.toString() + "," + joinLocation.longitude
        mAPI.joinShare(shareId, user!!.id, locationString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ responseBodyResponse ->

                    if (responseBodyResponse.code() == 200)
                        onSuccessListener.onSuccess(responseBodyResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code: " + responseBodyResponse.code()))

                }, { it.printStackTrace() })

    }

    fun completeShare(share: Share, joinLocation: android.location.Location, onSuccessListener: OnSuccessListener<Share>, onFailureListener: OnFailureListener) {

        val locationString = joinLocation.latitude.toString() + "," + joinLocation.longitude
        mAPI.completeShare(share.id, user!!.id, locationString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ responseBodyResponse ->

                    if (responseBodyResponse.code() == 200)
                        onSuccessListener.onSuccess(responseBodyResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code: " + responseBodyResponse.code()))

                }, { it.printStackTrace() })

    }

    fun finishShare(share: Share, onSuccessListener: OnSuccessListener<Share>, onFailureListener: OnFailureListener) {

        mAPI.finishShare(share.id, user!!.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ responseBodyResponse ->

                    if (responseBodyResponse.code() == 200)
                        onSuccessListener.onSuccess(responseBodyResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code: " + responseBodyResponse.code()))

                }, { it.printStackTrace() })

    }

    fun uploadLocation(userId: Long, routeLocationList: List<RouteLocation>, onSuccessListener: OnSuccessListener<List<RouteLocation>>, onFailureListener: OnFailureListener) {

        mAPI.uploadLocations(userId, routeLocationList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ listResponse ->

                    if (listResponse.code() == 200)
                        onSuccessListener.onSuccess(listResponse.body())
                    else
                        onFailureListener.onFailure(Exception("Response code" + listResponse.code()))

                }, { it.printStackTrace() })

    }

    fun getUser(uID: Long?, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {

        mAPI.getUser(uID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userResponse ->

                    if (userResponse.code() == 200) {
                        user = userResponse.body()
                        onSuccessListener.onSuccess(null)
                    } else
                        onFailureListener.onFailure(Exception("Response code: " + userResponse.code()))

                }, { it.printStackTrace() })

    }

    fun getUserProfile(onSuccessListener: OnSuccessListener<UserProfile>, onFailureListener: OnFailureListener) {

        getUserProfile(user!!.id, onSuccessListener, onFailureListener)

    }

    fun getUserProfile(userId: Long?, onSuccessListener: OnSuccessListener<UserProfile>, onFailureListener: OnFailureListener) {

        mAPI.getUserProfile(userId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userResponse ->

                    if (userResponse.code() == 200) {
                        onSuccessListener.onSuccess(userResponse.body())
                    } else
                        onFailureListener.onFailure(Exception("Response code: " + userResponse.code()))

                }, { it.printStackTrace() })

    }

    companion object {

        const val AVATAR_BASE_URL = "http://home2workapi.azurewebsites.net/images/avatar/"
        const val COMPANIES_BASE_URL = "http://home2workapi.azurewebsites.net/images/companies/"
        const val ACHIEVEMENTS_BASE_URL = "http://home2workapi.azurewebsites.net/images/achievements/"
        private const val API_BASE_URL = "http://home2workapi.azurewebsites.net/api/"

        var myInstancesCount = 0
        private val mInstance: HomeToWorkClient = HomeToWorkClient()

        @Synchronized
        fun getInstance(): HomeToWorkClient {
            return mInstance
        }

        var user: User? = null
            private set
    }

}
