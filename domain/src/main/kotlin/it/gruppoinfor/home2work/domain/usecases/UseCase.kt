package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import io.reactivex.ObservableTransformer


abstract class UseCase<T>(private val transformer: ObservableTransformer<T, T>) {

    abstract fun createObservable(data: Map<String, Any>? = null): Observable<T>

    fun observable(withData: Map<String, Any>? = null): Observable<T> {
        return createObservable(withData).compose(transformer)
    }
}