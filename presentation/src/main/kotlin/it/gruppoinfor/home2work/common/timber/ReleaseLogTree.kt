package it.gruppoinfor.home2work.common.timber

import android.util.Log
import timber.log.Timber


class ReleaseLogTree : Timber.Tree() {
    private val MAX_LOG_LENGTH = 4000

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        // Don't log VERBOSE, DEBUG and INFO
        return !(priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO)
        // Log only ERROR, WARN and WTF
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (isLoggable(tag, priority)) {

            // Message is short enough, doesn't need to be broken into chunks
            if (message.length < MAX_LOG_LENGTH) {
                if (priority == Log.ASSERT) {
                    Log.wtf(tag, message)
                } else {
                    Log.println(priority, tag, message)
                }
                return
            }

            // Split by line, then ensure each line can fit into Log's max length
            var i = 0
            val length = message.length
            while (i < length) {
                var newline = message.indexOf('\n', i)
                newline = if (newline != -1) newline else length
                do {
                    val end = Math.min(newline, i + MAX_LOG_LENGTH)
                    val part = message.substring(i, end)
                    if (priority == Log.ASSERT) {
                        Log.wtf(tag, part)
                    } else {
                        Log.println(priority, tag, part)
                    }
                    i = end
                } while (i < newline)
                i++
            }
        }
    }
}