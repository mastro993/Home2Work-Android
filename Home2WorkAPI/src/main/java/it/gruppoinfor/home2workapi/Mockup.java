package it.gruppoinfor.home2workapi;

import com.arasthel.asyncjob.AsyncJob;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import it.gruppoinfor.home2workapi.enums.BookingStatus;
import it.gruppoinfor.home2workapi.model.Booking;
import it.gruppoinfor.home2workapi.model.BookingInfo;
import it.gruppoinfor.home2workapi.model.Karma;
import it.gruppoinfor.home2workapi.model.Match;
import it.gruppoinfor.home2workapi.model.MatchInfo;
import it.gruppoinfor.home2workapi.model.Profile;
import it.gruppoinfor.home2workapi.model.ProfileStats;
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
    private static Match match8 = new Match(6L, user3, null, 2.4, stringToDate("8:30"), stringToDate("17:30"), 64, true, false);
    private static Match match9 = new Match(9L, user2, null, 15.5, stringToDate("8:30"), stringToDate("17:30"), 51, true, false);
    private static Match match10 = new Match(10L, user4, null, 15.5, stringToDate("8:30"), stringToDate("17:30"), 51, true, false);

    private static Booking booking1;
    private static Booking booking2;
    private static Booking booking3;
    private static Booking booking4;
    private static Booking booking5;
    private static Booking booking6;


    public static void refreshUserMatchesAsync(AsyncJob.AsyncResultAction<List<Match>> asyncResultAction) {
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

    public static List<Match> refreshUserMatches() {
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
    }

    public static void refreshUserBookingsAsync(AsyncJob.AsyncResultAction<List<Booking>> asyncResultAction) {
        new AsyncJob.AsyncJobBuilder<List<Booking>>()
                .doInBackground(() -> {

                    lag();

                    booking1 = new Booking(1L, match1, new Date(unixTime + (5L * dayInMillis)), new Date(), BookingStatus.CONFIRMED);
                    booking2 = new Booking(2L, match2, new Date(unixTime + (3L * dayInMillis)), new Date(), BookingStatus.PENDING);
                    booking3 = new Booking(3L, match3, new Date(unixTime + (3L * dayInMillis)), new Date(), BookingStatus.REJECTED);

                    List<Booking> bookedMatches = new ArrayList<>();
                    bookedMatches.add(booking1);
                    bookedMatches.add(booking2);
                    bookedMatches.add(booking3);

                    return bookedMatches;

                })
                .doWhenFinished(asyncResultAction)
                .create()
                .start();
    }

    public static List<Booking> refreshUserBookings() {
        lag();

        booking1 = new Booking(1L, match1, new Date(unixTime + (5L * dayInMillis)), new Date(), BookingStatus.CONFIRMED);
        booking2 = new Booking(2L, match2, new Date(unixTime + (3L * dayInMillis)), new Date(), BookingStatus.PENDING);
        booking3 = new Booking(3L, match3, new Date(unixTime + (3L * dayInMillis)), new Date(), BookingStatus.REJECTED);

        List<Booking> bookedMatches = new ArrayList<>();
        bookedMatches.add(booking1);
        bookedMatches.add(booking2);
        bookedMatches.add(booking3);

        return bookedMatches;
    }

    public static void refreshUserRequestsAsync(AsyncJob.AsyncResultAction<List<Booking>> asyncResultAction) {
        new AsyncJob.AsyncJobBuilder<List<Booking>>()
                .doInBackground(() -> {

                    lag();

                    booking4 = new Booking(4L, match8, new Date(unixTime + (5L * dayInMillis)), new Date(), BookingStatus.CONFIRMED);
                    booking5 = new Booking(5L, match9, new Date(unixTime + (3L * dayInMillis)), new Date(), BookingStatus.PENDING);
                    booking6 = new Booking(6L, match10, new Date(unixTime + (3L * dayInMillis)), new Date(), BookingStatus.PENDING);

                    List<Booking> requests = new ArrayList<>();
                    requests.add(booking4);
                    requests.add(booking5);
                    requests.add(booking6);

                    return requests;

                })
                .doWhenFinished(asyncResultAction)
                .create()
                .start();
    }

    public static List<Booking> refreshUserRequests() {
        lag();

        booking4 = new Booking(4L, match8, new Date(unixTime + (5L * dayInMillis)), new Date(), BookingStatus.CONFIRMED);
        booking5 = new Booking(5L, match9, new Date(unixTime + (3L * dayInMillis)), new Date(), BookingStatus.PENDING);
        booking6 = new Booking(6L, match10, new Date(unixTime + (3L * dayInMillis)), new Date(), BookingStatus.PENDING);

        List<Booking> requests = new ArrayList<>();
        requests.add(booking4);
        requests.add(booking5);
        requests.add(booking6);

        return requests;
    }

    public static void getMatchInfoAsync(final Long matchID, AsyncJob.AsyncResultAction<MatchInfo> asyncResultAction) {
        new AsyncJob.AsyncJobBuilder<MatchInfo>()
                .doInBackground(() -> {

                    lag();

                    Match match = Client.getUserMatches().stream()
                            .filter(m -> m.getMatchID() == matchID)
                            .findFirst()
                            .get();

                    MatchInfo matchInfo = new MatchInfo();
                    matchInfo.setMatchID(match.getMatchID());
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

    public static void getBookingInfoAsync(Long bookingID, AsyncJob.AsyncResultAction<BookingInfo> asyncResultAction) {
        new AsyncJob.AsyncJobBuilder<BookingInfo>()
                .doInBackground(() -> {

                    lag();

                    Booking booking = Client.getUserBookings().stream()
                            .filter(b -> b.getBookingID() == bookingID)
                            .findFirst()
                            .get();

                    Match match = booking.getBookedMatch();
                    MatchInfo matchInfo = new MatchInfo();
                    matchInfo.setMatchID(match.getMatchID());
                    matchInfo.setHost(match.getHost());
                    matchInfo.setScore(match.getScore());
                    matchInfo.setSharedDistance(match.getSharedDistance());
                    matchInfo.setCunsumption((match.getSharedDistance() / 100.0) * 7.5);
                    matchInfo.setEmission(matchInfo.getCunsumption() * 9.0);
                    matchInfo.setStartLocation(new LatLng(44.17069120, 10.11676220));
                    matchInfo.setEndLocation(new LatLng(44.20258260, 10.08343070));
                    matchInfo.setDepartureTime(match.getDepartureTime());
                    matchInfo.setArrivalTime(match.getArrivalTime());

                    BookingInfo bookingInfo = new BookingInfo(1L, matchInfo, booking.getBookedDate(), new Date(), booking.getBookingStatus(), "Devo portare il cane dal veterinario, allunghiamo di qualche minuto");

                    return bookingInfo;


                })
                .doWhenFinished(asyncResultAction)
                .create()
                .start();
    }

    public static void getRequestInfoAsync(Long bookingID, AsyncJob.AsyncResultAction<BookingInfo> asyncResultAction) {
        new AsyncJob.AsyncJobBuilder<BookingInfo>()
                .doInBackground(() -> {

                    lag();

                    Booking booking = Client.getUserRequests().stream()
                            .filter(b -> b.getBookingID() == bookingID)
                            .findFirst()
                            .get();

                    Match match = booking.getBookedMatch();
                    MatchInfo matchInfo = new MatchInfo();
                    matchInfo.setMatchID(match.getMatchID());
                    matchInfo.setGuest(match.getGuest());
                    matchInfo.setScore(match.getScore());
                    matchInfo.setSharedDistance(match.getSharedDistance());
                    matchInfo.setCunsumption((match.getSharedDistance() / 100.0) * 7.5);
                    matchInfo.setEmission(matchInfo.getCunsumption() * 9.0);
                    matchInfo.setStartLocation(new LatLng(44.17069120, 10.11676220));
                    matchInfo.setEndLocation(new LatLng(44.20258260, 10.08343070));
                    matchInfo.setDepartureTime(match.getDepartureTime());
                    matchInfo.setArrivalTime(match.getArrivalTime());

                    BookingInfo bookingInfo = new BookingInfo(1L, matchInfo, booking.getBookedDate(), new Date(), booking.getBookingStatus(), "Devo portare il cane dal veterinario, allunghiamo di qualche minuto");

                    return bookingInfo;


                })
                .doWhenFinished(asyncResultAction)
                .create()
                .start();
    }

    public static void getUserProfileAsync(AsyncJob.AsyncResultAction<Profile> asyncResultAction) {
        new AsyncJob.AsyncJobBuilder<Profile>()
                .doInBackground(() -> {
                    lag();

                    Karma karma = new Karma(460);
                    ProfileStats profileStats = new ProfileStats(new Date(), 132.7, 11, 218.4);

                    return new Profile(karma, profileStats);
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
