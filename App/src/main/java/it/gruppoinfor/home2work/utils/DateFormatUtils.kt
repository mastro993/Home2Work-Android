package it.gruppoinfor.home2work.utils


import java.text.SimpleDateFormat
import java.util.*


object DateFormatUtils {

    fun formatDate(startDate: Date?): String {

        if (startDate == null) return ""

        val endDate = Date()

        //milliseconds
        var difference = endDate.time - startDate.time

        val secondsInMillis: Long = 1000
        val minutesInMillis = secondsInMillis * 60
        val hoursInMillis = minutesInMillis * 60
        val daysInMillis = hoursInMillis * 24

        val elapsedDays = difference / daysInMillis
        difference %= daysInMillis

        when {
            elapsedDays > 6 -> {

                val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN)
                val sdfTime = SimpleDateFormat("HH:mm", Locale.ITALIAN)
                return String.format("%1\$s alle %2\$s", sdfDate.format(startDate), sdfTime.format(startDate))

            }
            elapsedDays > 1 -> return elapsedDays.toString() + " giorni fa"
            elapsedDays > 0 -> return elapsedDays.toString() + " giorno fa"
            else -> {

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

    }

    fun dateToString(date: Date?): String {

        val sdf = SimpleDateFormat("HH:mm", Locale.ITALY)
        sdf.timeZone = TimeZone.getTimeZone("GMT+1")

        return sdf.format(date)
    }


}
