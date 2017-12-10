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
