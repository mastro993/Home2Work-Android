package it.gruppoinfor.home2work.domain.entities

/**
 * Created by feder on 16/03/2018.
 */
class Optional<out T>(val value: T? = null) {

    companion object {

        fun <T> of(value: T?): Optional<T> {
            return Optional(value)
        }

        fun <T> empty(): Optional<T> {
            return Optional()
        }
    }

    fun hasValue(): Boolean {
        return value != null
    }

}