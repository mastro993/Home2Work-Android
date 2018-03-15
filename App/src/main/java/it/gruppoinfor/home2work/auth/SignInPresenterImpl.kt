package it.gruppoinfor.home2work.auth

import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.api.HomeToWorkClient
import it.gruppoinfor.home2work.api.RetrofitException
import it.gruppoinfor.home2work.firebase.FirebaseTokenService

class SignInPresenterImpl constructor(private val signInView: SignInView) : SignInPresenter {

    private lateinit var mCompositeDisposable: CompositeDisposable

    override fun onPause() {
        mCompositeDisposable.clear()
    }

    override fun onResume() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun login(email: String, password: String) {
        mCompositeDisposable.add(HomeToWorkClient.getAuthService().login(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn { null }
                .subscribe({
                    FirebaseTokenService.sync(FirebaseInstanceId.getInstance().token)
                    signInView.onLoginSuccess()
                }, {
                    loginError(it as RetrofitException)
                }))


    }

    fun loginError(retrofitException: RetrofitException) {
        val errorMessage = "Login non avvenuto: " + when (retrofitException.kind) {
            RetrofitException.Kind.NETWORK -> "nessuna connessione ad internet"
            RetrofitException.Kind.HTTP -> "dati inseriti errati"
            RetrofitException.Kind.UNEXPECTED -> "errore sconosciuto"
        }
        signInView.showErrorMessage(errorMessage)
    }
}