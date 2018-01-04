package it.gruppoinfor.home2work.utils;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


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
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(startDate);
        } else if (elapsedDays > 0)
            return elapsedDays + " giorni fa";

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        if (elapsedHours > 0) {
            return elapsedHours + " ore fa";
        }

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        if (elapsedMinutes > 0) {
            return elapsedMinutes + " minuti fa";
        }


        return "qualche istante fa";


    }


}
