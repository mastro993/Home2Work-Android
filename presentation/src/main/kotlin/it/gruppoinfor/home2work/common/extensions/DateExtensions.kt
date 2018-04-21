package it.gruppoinfor.home2work.common.extensions

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

fun Date.format(pattern: String): String {
    val simpleDate = SimpleDateFormat(pattern, Locale.ITALIAN)
    return simpleDate.format(this)
}

fun Date.formatElapsed(): String {

    var difference = (Date().time - time).absoluteValue

    val elapsedYears = difference / TimeUnit.DAYS.toMillis(365)
    difference %= TimeUnit.DAYS.toMillis(365)

    val elapsedDays = difference / TimeUnit.DAYS.toMillis(1)
    difference %= TimeUnit.DAYS.toMillis(1)

    val elapsedHours = difference / TimeUnit.HOURS.toMillis(1)
    difference %= TimeUnit.HOURS.toMillis(1)

    val elapsedMinutes = difference / TimeUnit.MINUTES.toMillis(1)
    difference %= TimeUnit.MINUTES.toMillis(1)

    return when {
        elapsedYears > 1 -> {
            val sdfDate = SimpleDateFormat("dd mmmm yyyy", Locale.ITALIAN)
            String.format("%1\$s", sdfDate.format(this)).capitalize()
        }
        elapsedDays > 7 -> {
            val sdfDate = SimpleDateFormat("dd mmmm", Locale.ITALIAN)
            String.format("%1\$s", sdfDate.format(this)).capitalize()
        }
        elapsedDays > 1 -> elapsedDays.toString() + " giorni fa"
        elapsedDays > 0 -> "Un giorno fa"
        elapsedHours > 1 -> elapsedHours.toString() + " ore fa"
        elapsedHours > 0 -> "Un ora fa"
        elapsedMinutes > 1 -> elapsedMinutes.toString() + " minuti fa"
        elapsedMinutes > 0 -> "Un minuto fa"
        else -> "Qualche istante fa"
    }
}