package it.gruppoinfor.home2work.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.BookingActivity;
import it.gruppoinfor.home2work.activities.MatchActivity;
import it.gruppoinfor.home2work.adapters.BookingAdapter;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Booking;
import it.gruppoinfor.home2workapi.model.Match;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchBookingFragment extends Fragment {

    @BindView(R.id.bookings_recycler_view)
    RecyclerView bookingsRecyclerView;
    @BindView(R.id.empty_view)
    RelativeLayout emptyView;
    private Unbinder unbinder;
    private BookingAdapter bookingAdapter;

    public MatchBookingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout rootView = (FrameLayout) inflater.inflate(R.layout.fragment_match_booking, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initUI();
        return rootView;
    }

    private void initUI() {
        if (Client.getUserBookings() != null && Client.getUserBookings().size() > 0) {
            emptyView.setVisibility(View.GONE);
            bookingsRecyclerView.setVisibility(View.VISIBLE);
            initBookingList();
        } else {
            emptyView.setVisibility(View.VISIBLE);
            bookingsRecyclerView.setVisibility(View.GONE);
        }
    }

    private void initBookingList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(bookingsRecyclerView.getContext(), layoutManager.getOrientation());
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);

        bookingsRecyclerView.setLayoutManager(layoutManager);
        bookingsRecyclerView.addItemDecoration(dividerItemDecoration);
        bookingsRecyclerView.setNestedScrollingEnabled(false);
        bookingsRecyclerView.setLayoutAnimation(animation);


        bookingAdapter = new BookingAdapter(getActivity(), Client.getUserBookings());
        bookingAdapter.setItemClickCallbacks(new ItemClickCallbacks() {
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
                        case R.id.show_booked_match_details:
                            showMatchDetails(position);
                            break;
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
        });
    }

    private void showBookingDetails(int position) {

        Booking booking = Client.getUserBookings().get(position);

        Intent bookingIntent = new Intent(getActivity(), BookingActivity.class);
        bookingIntent.putExtra("bookingID", booking.getBookingID());
        startActivity(bookingIntent);

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
        /*
        TODO richiesta annullamento prenotazione
        Match matchItem = Client.getUserMatches().get(position);
        MaterialDialog hideDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.match_item_hide_dialog_title)
                .content(R.string.match_item_hide_dialog_content)
                .positiveText(R.string.match_item_hide_dialog_confirm)
                .negativeText(R.string.match_item_hide_dialog_cancel)
                .onPositive((dialog, which) -> {
                    Client.getUserMatches().remove(position);
                    matchesAdapter.remove(position);
                    setMatchAsHidden(matchItem);
                    refreshBadge();
                })
                .build();

        hideDialog.show();*/
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
