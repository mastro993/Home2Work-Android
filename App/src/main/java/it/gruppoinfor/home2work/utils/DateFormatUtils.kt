package it.gruppoinfor.home2work.utils


import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


object DateFormatUtils {

    fun formatDate(timestamp: Timestamp): String {

        val endDate = Date()
        val startDate = Date(timestamp.time)

        //milliseconds
        var difference = endDate.time - startDate.time

        val secondsInMillis: Long = 1000
        val minutesInMillis = secondsInMillis * 60
        val hoursInMillis = minutesInMillis * 60
        val daysInMillis = hoursInMillis * 24

        val elapsedDays = difference / daysInMillis
        difference %= daysInMillis

        if (elapsedDays > 6) {
            val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN)
            val sdfTime = SimpleDateFormat("HH:mm", Locale.ITALIAN)
            return String.format("%1\$s alle %2\$s", sdfDate.format(startDate), sdfTime.format(startDate))
        } else if (elapsedDays > 1) {
            return elapsedDays.toString() + " giorni fa"
        } else if (elapsedDays > 0) {
            return elapsedDays.toString() + " giorno fa"
        }


        val elapsedHours = difference / hoursInMillis
        difference %= hoursInMillis

        if (elapsedHours > 1) {
            return elapsedHours.toString() + " ore fa"
        } else if (elapsedHours > 0) {
            return elapsedHours.toString() + " ora fa"
        }

        val elapsedMinutes = difference / minutesInMillis
        difference %= minutesInMillis

        if (elapsedMinutes > 1) {
            return elapsedMinutes.toString() + " minuti fa"
        } else if (elapsedMinutes > 0) {
            return elapsedMinutes.toString() + " minuto fa"
        }


        return "qualche istante fa"


    }


}
