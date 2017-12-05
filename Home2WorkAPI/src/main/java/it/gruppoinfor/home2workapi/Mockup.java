package it.gruppoinfor.home2workapi;

import com.arasthel.asyncjob.AsyncJob;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import it.gruppoinfor.home2workapi.model.Achievement;
import it.gruppoinfor.home2workapi.model.Booking;
import it.gruppoinfor.home2workapi.model.Profile;
import it.gruppoinfor.home2workapi.model.ProfileStats;
import it.gruppoinfor.home2workapi.model.Share;
import it.gruppoinfor.home2workapi.model.User;

import static it.gruppoinfor.home2workapi.Converters.stringToDate;

public class Mockup {

    private static Long unixTime = System.currentTimeMillis();
    private static Long dayInMillis = 24L * 60L * 60L * 1000L;

    private static User user1 = new User(47L, "Mario", "Rossi");
    private static User user2 = new User(48L, "Giovanni", "Bianchi");
    private static User user3 = new User(49L, "Luca", "Esposito");
    private static User user4 = new User(50L, "Paolo", "Verdi");

    private static Achievement achievement1;
    private static Achievement achievement2;
    private static Achievement achievement3;
    private static Achievement achievement4;
    private static Achievement achievement5;
    private static Achievement achievement6;
    private static Achievement achievement7;
    private static Achievement achievement8;
    private static Achievement achievement9;
    private static Achievement achievement10;

    private static Booking booking1;
    private static Booking booking2;
    private static Booking booking3;
    private static Booking booking4;
    private static Booking booking5;
    private static Booking booking6;

    private static Share share1;
    private static Share share2;
    private static Share share3;
    private static Share share4;

    private static int karmaValue = 55;
    private static int expValue = 780;





    public static void getUserProfileAsync(AsyncJob.AsyncResultAction<Profile> asyncResultAction) {
        new AsyncJob.AsyncJobBuilder<Profile>()
                .doInBackground(() -> {
                    lag();

                    expValue *= 2;

                    ProfileStats profileStats = new ProfileStats(new Date(), 132.7, 11, 218.4);

                    achievement1 = new Achievement(1L, "Obiettivo 1", "Descrizione dell'obiettivo", 10, 100, new Date(), 50.0, 50.0);
                    achievement2 = new Achievement(2L, "Obiettivo 2", "Descrizione dell'obiettivo", 15, 150, new Date(), 100.0, 77.0);
                    achievement3 = new Achievement(3L, "Obiettivo 3", "Descrizione dell'obiettivo", 70, 700, new Date(), 1.0, 0.0);
                    achievement4 = new Achievement(4L, "Obiettivo 4", "Descrizione dell'obiettivo", 20, 200, new Date(), 200.0, 133.0);
                    achievement5 = new Achievement(5L, "Obiettivo 5", "Descrizione dell'obiettivo", 5, 50, new Date(), 5.0, 0.0);
                    achievement6 = new Achievement(6L, "Obiettivo 6", "Descrizione dell'obiettivo", 10, 100, new Date(), 70.0, 50.0);
                    achievement7 = new Achievement(7L, "Obiettivo 7", "Descrizione dell'obiettivo", 15, 150, new Date(), 80.0, 77.0);
                    achievement8 = new Achievement(8L, "Obiettivo 8", "Descrizione dell'obiettivo", 70, 700, new Date(), 10.0, 0.0);
                    achievement9 = new Achievement(9L, "Obiettivo 9", "Descrizione dell'obiettivo", 20, 200, new Date(), 500.0, 133.0);
                    achievement10 = new Achievement(10L, "Obiettivo 10", "Descrizione dell'obiettivo", 5, 50, new Date(), 15.0, 0.0);


                    List<Achievement> achievements = new ArrayList<>();
                    achievements.add(achievement1);
                    achievements.add(achievement2);
                    achievements.add(achievement3);
                    achievements.add(achievement4);
                    achievements.add(achievement5);
                    achievements.add(achievement6);
                    achievements.add(achievement7);
                    achievements.add(achievement8);
                    achievements.add(achievement9);
                    achievements.add(achievement10);

                    share1 = new Share(1L, user1, null, 16.4, 16, 164, new Date());
                    share2 = new Share(2L, user2, null, 11.2, 11, 1112, new Date());
                    share3 = new Share(3L, null, user1, 8.9, 8, 89, new Date());
                    share4 = new Share(4L, user4, null, 21.5, 21, 215, new Date());

                    List<Share> shares = new ArrayList<>();
                    shares.add(share1);
                    shares.add(share2);
                    shares.add(share3);
                    shares.add(share4);


                    return new Profile(expValue, karmaValue, profileStats, achievements, shares);
                })
                .doWhenFinished(asyncResultAction)
                .create()
                .start();

    }

    private static void lag() {
        // Simula ritardo connessione dati
        int randomTime = new Random().nextInt(500) + new Random().nextInt(500);
        try {
            Thread.sleep(randomTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
