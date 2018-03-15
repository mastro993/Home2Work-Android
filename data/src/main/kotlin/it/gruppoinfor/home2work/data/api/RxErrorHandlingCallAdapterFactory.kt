package it.gruppoinfor.home2work.api

import io.reactivex.Observable
import io.reactivex.functions.Function
import retrofit2.*
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.lang.reflect.Type

@SuppressWarnings("unchecked")
class RxErrorHandlingCallAdapterFactory : CallAdapter.Factory() {

    companion object {
        fun create(): CallAdapter.Factory {
            return RxErrorHandlingCallAdapterFactory()
        }
    }

    private var original: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

    override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<Any, Observable<Any>>? {
        return RxCallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit) as CallAdapter<Any, Observable<Any>>)
    }

    class RxCallAdapterWrapper constructor(private val retrofit: Retrofit, private val wrapped: CallAdapter<Any, Observable<Any>>) : CallAdapter<Any, Observable<Any>> {
        override fun responseType(): Type {
            return wrapped.responseType()
        }

        override fun adapt(call: Call<Any>?): Observable<Any> {
            return (wrapped.adapt(call) as Observable<Any>).onErrorResumeNext(Function {
                Observable.error(asRetrofitException(it))
            })
        }

        fun asRetrofitException(throwable: Throwable): RetrofitException {
            if (throwable is HttpException) {
                val response: Response<Any> = throwable.response() as Response<Any>
                return RetrofitException.httpError(response.raw().request().url().toString(), response, retrofit)
            }
            // A network error happened
            if (throwable is IOException) {
                return RetrofitException.networkError(throwable)
            }

            // We don't know what happened. We need to simply convert to an unknown error

            return RetrofitException.unexpectedError(throwable)
        }
    }


}