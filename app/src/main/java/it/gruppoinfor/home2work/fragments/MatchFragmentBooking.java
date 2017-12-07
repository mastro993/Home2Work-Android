package it.gruppoinfor.home2work.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.BookingActivity;
import it.gruppoinfor.home2work.activities.MatchActivity;
import it.gruppoinfor.home2work.adapters.BookingAdapter;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.Mockup;
import it.gruppoinfor.home2workapi.enums.BookingStatus;
import it.gruppoinfor.home2workapi.model.Booking;
import it.gruppoinfor.home2workapi.model.Match;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchFragmentBooking extends Fragment implements ItemClickCallbacks {

    @BindView(R.id.bookings_recycler_view)
    RecyclerView bookingsRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    private Unbinder unbinder;
    private BookingAdapter bookingAdapter;

    public MatchFragmentBooking() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match_booking, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initUI();
        return rootView;
    }

    private void initUI() {

        if(Client.getUserBookings().size() == 0){
            bookingsRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else{
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(bookingsRecyclerView.getContext(), layoutManager.getOrientation());
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);

            bookingsRecyclerView.setLayoutManager(layoutManager);
            //bookingsRecyclerView.addItemDecoration(dividerItemDecoration);
            bookingsRecyclerView.setLayoutAnimation(animation);

            bookingAdapter = new BookingAdapter(getActivity(), Client.getUserBookings());
            bookingAdapter.setItemClickCallbacks(this);
            bookingsRecyclerView.setAdapter(bookingAdapter);
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        showBookingDetails(position);
    }

    @Override
    public boolean onLongItemClick(View view, int position) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.getMenuInflater().inflate(R.menu.menu_booked_match, popup.getMenu());
        popup.setOnMenuItemClickListener((item) -> {
            switch (item.getItemId()) {
                case R.id.show_booked_match_profile:
                    showBookedMatchUserProfile(position);
                    break;
                case R.id.delete_booked_match:
                    showDeleteBookingDialog(position);
                    break;
                default:
                    break;
            }
            return true;
        });
        popup.show();
        return true;
    }

    private void showBookingDetails(int position) {

        Booking booking = Client.getUserBookings().get(position);

        if(booking.getBookingStatus() == 0){
            removeBookingDialog(position);
        } else {
            Intent bookingIntent = new Intent(getActivity(), BookingActivity.class);
            bookingIntent.putExtra("bookingID", booking.getBookingID());
            startActivity(bookingIntent);
        }

    }

    private void showMatchDetails(int position) {
        Match match = Client.getUserMatches().get(position);

        Intent matchIntent = new Intent(getContext(), MatchActivity.class);
        matchIntent.putExtra("matchID", match.getMatchID());
        startActivity(matchIntent);

    }

    private void showBookedMatchUserProfile(int position) {
        /*
        TODO Activity info utente
        Intent userIntent = new Intent(activity, ShowUserActivity.class);
        User matchedUser = match.getHost();
        userIntent.putExtra("userID", matchedUser.getId());
        activity.startActivity(userIntent);*/
    }

    private void showDeleteBookingDialog(int position) {
        MaterialDialog hideDialog = new MaterialDialog.Builder(getActivity())
                .title("Annulla prenotazione")
                .content("Sei sicuro di voler annullare la prenotazione? Protesti non essere più in grado di rieffettuare la stessa prenotazione")
                .positiveText("Conferma annullamento")
                .negativeText("Indietro")
                .onPositive((dialog, which) -> {
                    Booking booking = Client.getUserBookings().get(position);
                    booking.setBookingStatus(BookingActivity.BOOKING_CANCELED);
                    Client.getAPI().editBooking(booking).enqueue(new Callback<Booking>() {
                        @Override
                        public void onResponse(Call<Booking> call, Response<Booking> response) {
                           if(response.code() == 200){
                               Client.getUserBookings().remove(position);
                               bookingAdapter.remove(position);
                           }
                        }

                        @Override
                        public void onFailure(Call<Booking> call, Throwable t) {

                        }
                    });
                })
                .build();

        hideDialog.show();
    }

    private void removeBookingDialog(int position) {
        MaterialDialog hideDialog = new MaterialDialog.Builder(getActivity())
                .title("Prenotazione rifiutata")
                .content("Questa prenotazione, purtroppo, non è stata accettata.")
                .positiveText("Rimuovi")
                .onPositive((dialog, which) -> {
                    Booking booking = Client.getUserBookings().get(position);
                    booking.setHidden(true);
                    Client.getAPI().editBooking(booking).enqueue(new Callback<Booking>() {
                        @Override
                        public void onResponse(Call<Booking> call, Response<Booking> response) {
                            bookingAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<Booking> call, Throwable t) {

                        }
                    });

                })
                .build();

        hideDialog.show();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
