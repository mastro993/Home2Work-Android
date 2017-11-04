package it.gruppoinfor.home2work.fragments;


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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2work.adapters.RequestAdapter;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.Mockup;
import it.gruppoinfor.home2workapi.model.Booking;


public class MatchFragmentRequest extends Fragment {

    @BindView(R.id.requests_recycler_view)
    RecyclerView requestsRecyclerView;
    private SwipeRefreshLayout rootView;
    private Unbinder unbinder;
    private RequestAdapter requestAdapter;

    public MatchFragmentRequest() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_match_request, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initUI();
        //refresh();
        rootView.setOnRefreshListener(this::refresh);
        rootView.setColorSchemeResources(R.color.colorAccent);
        return rootView;
    }

    private void initUI() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requestsRecyclerView.getContext(), layoutManager.getOrientation());
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);

        requestsRecyclerView.setLayoutManager(layoutManager);
        requestsRecyclerView.addItemDecoration(dividerItemDecoration);
        requestsRecyclerView.setLayoutAnimation(animation);

        requestAdapter = new RequestAdapter(getActivity(), Client.getUserRequests());
        requestAdapter.setItemClickCallbacks(new ItemClickCallbacks() {
            @Override
            public void onItemClick(View view, int position) {
                showRequestDetails(position);
            }

            @Override
            public boolean onLongItemClick(View view, int position) {
                return false;
            }
        });

        requestsRecyclerView.setAdapter(requestAdapter);
    }

    public void refresh() {
        rootView.setRefreshing(true);
        // TODO refresh da web
        Mockup.refreshUserBookings(requests -> {
            Client.setUserRequests(requests);
            requestAdapter.notifyDataSetChanged();
            rootView.setRefreshing(false);
        });
    }

    private void showRequestDetails(int position) {

        Booking booking = Client.getUserRequests().get(position);

        // TODO booking activity
        /*Intent bookingIntent = new Intent(getActivity(), BookingActivity.class);
        bookingIntent.putExtra("bookingID", booking.getBookingID());
        startActivity(bookingIntent);*/

    }

    private void showRequestUserProfile(int position) {
        /*
        TODO Activity info utente
        Intent userIntent = new Intent(activity, ShowUserActivity.class);
        User matchedUser = match.getHost();
        userIntent.putExtra("userID", matchedUser.getId());
        activity.startActivity(userIntent);*/
    }


    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
