package it.gruppoinfor.home2work.utils;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DateFormatUtils {

    public static String formatDate(Timestamp timestamp) {

        Date endDate = new Date();
        Date startDate = new Date(timestamp.getTime());

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        if (elapsedDays > 6) {
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.ITALIAN);
            return String.format("%1$s alle %2$s", sdfDate.format(startDate), sdfTime.format(startDate));
        } else if (elapsedDays > 1) {
            return elapsedDays + " giorni fa";
        } else if (elapsedDays > 0) {
            return elapsedDays + " giorno fa";
        }


        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        if (elapsedHours > 1) {
            return elapsedHours + " ore fa";
        } else if (elapsedHours > 0) {
            return elapsedHours + " ora fa";
        }

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        if (elapsedMinutes > 1) {
            return elapsedMinutes + " minuti fa";
        } else if (elapsedMinutes > 0) {
            return elapsedMinutes + " minuto fa";
        }


        return "qualche istante fa";


    }


}
