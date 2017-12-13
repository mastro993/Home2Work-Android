package it.gruppoinfor.home2work.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.RequestActivity;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2work.adapters.RequestAdapter;
import it.gruppoinfor.home2workapi.model.Booking;


public class MatchFragmentRequest extends Fragment {

    @BindView(R.id.requests_recycler_view)
    RecyclerView requestsRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    private Unbinder unbinder;
    private RequestAdapter requestAdapter;

    public MatchFragmentRequest() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match_request, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initUI();
        return rootView;
    }

    private void initUI() {
        if (MatchFragment.RequestList.size() == 0) {
            requestsRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requestsRecyclerView.getContext(), layoutManager.getOrientation());
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);

            requestsRecyclerView.setLayoutManager(layoutManager);
            //requestsRecyclerView.addItemDecoration(dividerItemDecoration);
            requestsRecyclerView.setLayoutAnimation(animation);

            requestAdapter = new RequestAdapter(getActivity(), MatchFragment.RequestList);
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
    }

    private void showRequestDetails(int position) {

        Booking booking = MatchFragment.RequestList.get(position);

        Intent bookingIntent = new Intent(getActivity(), RequestActivity.class);
        bookingIntent.putExtra("bookingID", booking.getBookingID());
        getActivity().startActivityForResult(bookingIntent, RequestActivity.REQUEST_RESPONSE_CODE);

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
