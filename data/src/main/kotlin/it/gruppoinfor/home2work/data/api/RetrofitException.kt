package it.gruppoinfor.home2work.data.api

import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException


class RetrofitException internal constructor(message: String?,
                                             /** The request URL which produced the error.  */
                                             val url: String?,
                                             /** Response object containing status code, headers, body, etc.  */
                                             private val response: Response<*>?,
                                             /** The event kind which triggered this error.  */
                                             val kind: Kind,
                                             exception: Throwable?,
                                             /** The Retrofit this request was executed on  */
                                             private val retrofit: Retrofit?) : RuntimeException(message, exception) {

    /** Tipo di evento [RetrofitException].  */
    enum class Kind {
        /** Problemi di comunicazione con il server.  */
        NETWORK,
        /** Status code ricevuto diverso da 200.  */
        HTTP,
        CLIENT,
        SERVER,
        /** Altro errore inaspettato */
        UNEXPECTED
    }

    /**
     * HTTP response body converted to specified `type`. `null` if there is no
     * response.
     *
     * @throws IOException if unable to convert the body to the specified `type`.
     */
    fun <T> getErrorBodyAs(type: Class<T>): T? {

        if (response == null) {
            return null
        }

        if (response.errorBody() == null) {
            return null
        }

        val converter = retrofit?.responseBodyConverter<T>(type, arrayOfNulls<Annotation>(0))

        return converter?.convert(response.errorBody()!!)
    }

    companion object {
        fun httpError(url: String, response: Response<*>, retrofit: Retrofit): RetrofitException {
            val message = "${response.code()} ${response.message()}"

            if (response.code() in 400..499) {
                return RetrofitException(message, url, response, Kind.CLIENT, null, retrofit)
            }

            if (response.code() in 500..599) {
                return RetrofitException(message, url, response, Kind.SERVER, null, retrofit)
            }

            return RetrofitException(message, url, response, Kind.HTTP, null, retrofit)
        }

        fun authError(url: String, response: Response<Any>, retrofit: Retrofit): RetrofitException {
            val message = "${response.code()} ${response.message()}"
            return RetrofitException(message, url, response, Kind.HTTP, null, retrofit)
        }

        fun networkError(exception: IOException): RetrofitException {
            return RetrofitException(exception.message, null, null, Kind.NETWORK, exception, null)
        }

        fun unexpectedError(exception: Throwable): RetrofitException {
            return RetrofitException(exception.message, null, null, Kind.UNEXPECTED, exception, null)
        }
    }
}

