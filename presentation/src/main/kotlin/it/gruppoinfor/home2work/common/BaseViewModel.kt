package it.gruppoinfor.home2work.common

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import it.gruppoinfor.home2work.splash.SplashViewState


open class BaseViewModel<VS: BaseViewState> : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    var viewState: MutableLiveData<VS> = MutableLiveData()

    protected fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private fun clearDisposables() {
        compositeDisposable.clear()
    }

    override fun onCleared() {
        clearDisposables()
    }
}