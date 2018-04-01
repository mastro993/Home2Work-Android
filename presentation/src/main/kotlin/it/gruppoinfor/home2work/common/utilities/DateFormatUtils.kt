package it.gruppoinfor.home2work.common.utilities


import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


object DateFormatUtils {

    fun formatDate(startDate: Date?): String {

        if (startDate == null) return ""

        var difference = Date().time - startDate.time

        val elapsedDays = difference / TimeUnit.DAYS.toMillis(1)
        difference %= TimeUnit.DAYS.toMillis(1)

        when {
            elapsedDays > 6 -> {

                val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN)
                val sdfTime = SimpleDateFormat("HH:mm", Locale.ITALIAN)
                return String.format("%1\$s alle %2\$s", sdfDate.format(startDate), sdfTime.format(startDate))

            }
            elapsedDays > 1 -> return elapsedDays.toString() + " giorni fa"
            elapsedDays > 0 -> return elapsedDays.toString() + " giorno fa"
            else -> {

                val elapsedHours = difference / TimeUnit.HOURS.toMillis(1)
                difference %= TimeUnit.HOURS.toMillis(1)

                if (elapsedHours > 1) {
                    return elapsedHours.toString() + " ore fa"
                } else if (elapsedHours > 0) {
                    return elapsedHours.toString() + " ora fa"
                }

                val elapsedMinutes = difference / TimeUnit.MINUTES.toMillis(1)
                difference %= TimeUnit.MINUTES.toMillis(1)

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
