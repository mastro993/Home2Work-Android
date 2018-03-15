package it.gruppoinfor.home2work.user

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.utils.ImageUtils
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.RetrofitException
import it.gruppoinfor.home2workapi.user.UserProfile
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class ProfilePresenterImpl constructor(private val profileView: ProfileView) : ProfilePresenter {

    private lateinit var mCompositeDisposable: CompositeDisposable

    private var userProfile: UserProfile? = null

    override fun onViewCreated() {

        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable.add(HomeToWorkClient.getUserService().getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    profileView.onLoading()
                }
                .doOnError {
                    loadingError(it as RetrofitException)
                    userProfile
                }
                .subscribe {
                    userProfile = it
                    profileView.setProfileData(it)
                })

    }

    override fun onRefresh() {
        mCompositeDisposable.add(HomeToWorkClient.getUserService().getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    profileView.onRefresh()
                }
                .doOnError {
                    refreshError(it as RetrofitException)
                    userProfile
                }
                .doFinally {
                    profileView.onRefreshDone()
                }
                .subscribe {
                    userProfile = it
                    profileView.setProfileData(it)
                })
    }

    override fun onPause() {
        mCompositeDisposable.clear()
    }

    override fun uploadAvatar(context: Context, uri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            val propic = ImageUtils.shrinkBitmap(bitmap, 300)

            val file = ImageUtils.bitmapToFile(context.cacheDir, propic)
            val decodedAvatar = ImageUtils.decodeFile(file.path)
            val decodedFile = File(decodedAvatar)

            val mime = ImageUtils.getMimeType(decodedFile.path)
            val mediaType = MediaType.parse(mime!!)

            val requestFile = RequestBody.create(mediaType, decodedFile)

            val filename = "${HomeToWorkClient.user?.id}.jpg"

            val body = MultipartBody.Part.createFormData("avatar", filename, requestFile)

            mCompositeDisposable.add(HomeToWorkClient.getUserService().uploadAvatar(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn {
                        uploadError(it as RetrofitException)
                        null
                    }
                    .subscribe {
                        profileView.onAvatarUploaded()
                    })

        } catch (e: Exception) {
            profileView.showErrorMessage("Impossibile caricare l'avatar")
            e.printStackTrace()
        }
    }

    private fun uploadError(exception: RetrofitException){
        val errorMessage = "Impossibile caricare l'avatar. " + when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
        }

        profileView.showErrorMessage(errorMessage)
    }

    private fun loadingError(exception: RetrofitException) {

        val errorMessage = when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
        }

        profileView.onLoadingError(errorMessage)

    }

    private fun refreshError(exception: RetrofitException) {

        val errorMessage = "Impossibile aggiornare il profilo. " + when (exception.kind) {
            RetrofitException.Kind.NETWORK -> "Nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "Impossibile contattare il server"
            RetrofitException.Kind.UNEXPECTED -> "Errore sconosciuto"
        }

        profileView.showErrorMessage(errorMessage)

    }

}