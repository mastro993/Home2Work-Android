package it.gruppoinfor.home2workapi;

import com.arasthel.asyncjob.AsyncJob;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import it.gruppoinfor.home2workapi.enums.RequestStatus;
import it.gruppoinfor.home2workapi.model.Booking;
import it.gruppoinfor.home2workapi.model.Match;
import it.gruppoinfor.home2workapi.model.MatchInfo;
import it.gruppoinfor.home2workapi.model.Request;
import it.gruppoinfor.home2workapi.model.User;

import static it.gruppoinfor.home2workapi.Converters.stringToDate;

public class Mockup {

    private static Long unixTime = System.currentTimeMillis();
    private static Long dayInMillis = 24L * 60L * 60L * 1000L;
    private static User user1 = new User(47L, "Mario", "Rossi");
    private static User user2 = new User(48L, "Giovanni", "Bianchi");
    private static User user3 = new User(49L, "Luca", "Esposito");
    private static User user4 = new User(50L, "Paolo", "Verdi");
    private static Match match1 = new Match(1L, null, user1, 15.7, stringToDate("8:30"), stringToDate("17:30"), 100, true, false);
    private static Match match2 = new Match(2L, null, user2, 13.2, stringToDate("8:30"), stringToDate("18:00"), 94, true, false);
    private static Match match3 = new Match(3L, null, user3, 12.3, stringToDate("8:20"), stringToDate("18:30"), 83, true, false);
    private static Match match4 = new Match(4L, null, user4, 5.3, stringToDate("8:00"), stringToDate("18:00"), 77, true, false);
    private static Match match5 = new Match(5L, null, user1, 8.1, stringToDate("8:30"), stringToDate("17:30"), 70, true, false);
    private static Match match6 = new Match(6L, null, user3, 2.4, stringToDate("8:30"), stringToDate("17:30"), 64, true, false);
    private static Match match7 = new Match(7L, null, user2, 15.5, stringToDate("8:30"), stringToDate("17:30"), 51, true, false);
    private static Booking bookedMatchItem1;
    private static Booking bookedMatchItem2;
    private static Request request1;
    private static Request request2;
    private static Request request3;


    public static void refreshUserMatches(AsyncJob.AsyncResultAction<List<Match>> asyncResultAction) {
        new AsyncJob.AsyncJobBuilder<List<Match>>()
                .doInBackground(() -> {

                    lag();

                    List<Match> matches = new ArrayList<>();
                    matches.add(match1);
                    matches.add(match2);
                    matches.add(match3);
                    matches.add(match4);
                    matches.add(match5);
                    matches.add(match6);
                    matches.add(match7);
                    return matches;

                })
                .doWhenFinished(asyncResultAction)
                .create()
                .start();
    }

    public static void refreshUserBookedMatches(AsyncJob.AsyncResultAction<List<Booking>> asyncResultAction) {
        new AsyncJob.AsyncJobBuilder<List<Booking>>()
                .doInBackground(() -> {

                    lag();

                    bookedMatchItem1 = new Booking(1L, match1, new Date(unixTime + (5L * dayInMillis)), null);
                    bookedMatchItem2 = new Booking(2L, match2, new Date(unixTime + (3L * dayInMillis)), "Ho in macchina il cane che lo lascio dal veterinario");

                    List<Booking> bookedMatches = new ArrayList<>();
                    bookedMatches.add(bookedMatchItem1);
                    bookedMatches.add(bookedMatchItem2);

                    return bookedMatches;

                })
                .doWhenFinished(asyncResultAction)
                .create()
                .start();
    }

    public static void refreshUserRequests(AsyncJob.AsyncResultAction<List<Request>> asyncResultAction) {
        new AsyncJob.AsyncJobBuilder<List<Request>>()
                .doInBackground(() -> {

                    lag();

                    request1 = new Request(1L, match1, new Date(unixTime + (1L * dayInMillis)), RequestStatus.PENDING);
                    request1 = new Request(1L, match2, new Date(unixTime + (9L * dayInMillis)), RequestStatus.REJECTED);

                    List<Request> requests = new ArrayList<>();
                    requests.add(request1);
                    requests.add(request2);
                    requests.add(request3);

                    return requests;

                })
                .doWhenFinished(asyncResultAction)
                .create()
                .start();
    }

    public static void getMatchInfo(AsyncJob.AsyncResultAction<MatchInfo> asyncResultAction) {
        new AsyncJob.AsyncJobBuilder<MatchInfo>()
                .doInBackground(() -> {

                    lag();

                    Match match = new Match(4L, null, user4, 5.3, stringToDate("8:00"), stringToDate("18:00"), 77, true, false);
                    MatchInfo matchInfo = new MatchInfo();
                    matchInfo.setMatchId(match.getMatchID());
                    matchInfo.setHost(match.getHost());
                    matchInfo.setScore(match.getScore());
                    matchInfo.setSharedDistance(match.getSharedDistance());
                    matchInfo.setStartLocation(new LatLng(44.17069120, 10.11676220));
                    matchInfo.setEndLocation(new LatLng(44.20258260, 10.08343070));
                    matchInfo.setDepartureTime(match.getDepartureTime());
                    matchInfo.setArrivalTime(match.getArrivalTime());
                    return matchInfo;

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
