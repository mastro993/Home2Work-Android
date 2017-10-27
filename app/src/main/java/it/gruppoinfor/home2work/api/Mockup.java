package it.gruppoinfor.home2work.api;

import com.arasthel.asyncjob.AsyncJob;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import it.gruppoinfor.home2work.models.BookingItem;
import it.gruppoinfor.home2work.models.MatchItem;
import it.gruppoinfor.home2work.models.User;

import static it.gruppoinfor.home2work.utils.Converters.stringToDate;

public class Mockup {

    private static Long unixTime = System.currentTimeMillis();
    private static Long dayInMillis = 24L * 60L * 60L * 1000L;
    private static User user1 = new User(47L, "Mario", "Rossi");
    private static User user2 = new User(48L, "Giovanni", "Bianchi");
    private static User user3 = new User(49L, "Luca", "Esposito");
    private static User user4 = new User(50L, "Paolo", "Verdi");
    private static MatchItem matchItem1 = new MatchItem(1L, null, user1, 15.7, stringToDate("8:30"), stringToDate("17:30"), 100, true, false);
    private static MatchItem matchItem2 = new MatchItem(2L, null, user2, 13.2, stringToDate("8:30"), stringToDate("18:00"), 94, true, false);
    private static MatchItem matchItem3 = new MatchItem(3L, null, user3, 12.3, stringToDate("8:20"), stringToDate("18:30"), 83, true, false);
    private static MatchItem matchItem4 = new MatchItem(4L, null, user4, 5.3, stringToDate("8:00"), stringToDate("18:00"), 77, true, false);
    private static MatchItem matchItem5 = new MatchItem(5L, null, user1, 8.1, stringToDate("8:30"), stringToDate("17:30"), 70, true, false);
    private static MatchItem matchItem6 = new MatchItem(6L, null, user3, 2.4, stringToDate("8:30"), stringToDate("17:30"), 64, true, false);
    private static MatchItem matchItem7 = new MatchItem(7L, null, user2, 15.5, stringToDate("8:30"), stringToDate("17:30"), 51, true, false);
    private static BookingItem bookedMatchItem1;
    private static BookingItem bookedMatchItem2;


    public static void refreshUserMatches(AsyncJob.AsyncResultAction<List<MatchItem>> asyncResultAction) {
        new AsyncJob.AsyncJobBuilder<List<MatchItem>>()
                .doInBackground(() -> {

                    lag();

                    List<MatchItem> matches = new ArrayList<>();
                    matches.add(matchItem1);
                    matches.add(matchItem2);
                    matches.add(matchItem3);
                    matches.add(matchItem4);
                    matches.add(matchItem5);
                    matches.add(matchItem6);
                    matches.add(matchItem7);
                    return matches;

                })
                .doWhenFinished(asyncResultAction)
                .create()
                .start();
    }

    public static void refreshUserBookedMatches(AsyncJob.AsyncResultAction<List<BookingItem>> asyncResultAction) {
        new AsyncJob.AsyncJobBuilder<List<BookingItem>>()
                .doInBackground(() -> {

                    lag();

                    bookedMatchItem1 = new BookingItem(1L, matchItem1, new Date(unixTime + (5L * dayInMillis)), null);
                    bookedMatchItem2 = new BookingItem(2L, matchItem2, new Date(unixTime + (3L * dayInMillis)), "Ho in macchina il cane che lo lascio dal veterinario");

                    List<BookingItem> bookedMatches = new ArrayList<>();
                    bookedMatches.add(bookedMatchItem1);
                    bookedMatches.add(bookedMatchItem2);

                    return bookedMatches;

                })
                .doWhenFinished(asyncResultAction)
                .create()
                .start();
    }

    private static void lag() {
        // Simula ritardo connessione dati
        int randomTime = new Random().nextInt(2000);
        try {
            Thread.sleep(randomTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
