package it.gruppoinfor.home2work.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.ShareActivity;
import it.gruppoinfor.home2work.adapters.AchievementAdapter;
import it.gruppoinfor.home2work.adapters.MatchAdapter;
import it.gruppoinfor.home2work.adapters.SharesAdapter;
import it.gruppoinfor.home2work.utils.Tools;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Match;
import it.gruppoinfor.home2workapi.model.Share;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SharesFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;

    @BindView(R.id.shares_recycler_view)
    RecyclerView sharesRecyclerView;
    @BindView(R.id.ongoing_share_layout)
    RelativeLayout ongoingShareLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.container)
    SwipeRefreshLayout container;
    @BindView(R.id.empty_view)
    TextView emptyView;
    private Unbinder unbinder;

    private List<Share> mShareList = new ArrayList<>();
    private SharesAdapter mSharesAdapter;

    public SharesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shares, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        refreshData();
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            joinShare();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String[] stringData = scanResult.getContents().split(",");
            Long shareId = Long.parseLong(stringData[0]);
            LatLng latLng = new LatLng(Double.parseDouble(stringData[1]), Double.parseDouble(stringData[2]));
            checkShareCode(shareId, latLng);
        } else
            Toasty.error(getActivity(), "QR Code non valido");
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    private void refreshData() {
        container.setRefreshing(true);

        Client.getAPI().getShares(Client.User.getId()).enqueue(new Callback<List<Share>>() {
            @Override
            public void onResponse(Call<List<Share>> call, Response<List<Share>> response) {
                if (response.code() == 200) {
                    mShareList = response.body();
                    initUI();
                }
            }

            @Override
            public void onFailure(Call<List<Share>> call, Throwable t) {
                Toasty.error(getContext(), "Impossibile ottenere lista condivsioni al momento").show();
                initUI();
            }
        });
    }

    private void initUI() {
        container.setRefreshing(false);

        if (ProfileFragment.AchievementList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            sharesRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            sharesRecyclerView.setVisibility(View.VISIBLE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);

            sharesRecyclerView.setLayoutManager(layoutManager);
            sharesRecyclerView.setLayoutAnimation(animation);

            mSharesAdapter = new SharesAdapter(getActivity(), mShareList);
            //mSharesAdapter.setItemClickCallbacks(this);
            sharesRecyclerView.setAdapter(mSharesAdapter);
        }
    }


    public void joinShare() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.initiateScan();
        }
    }

    private void checkShareCode(Long shareID, LatLng hostLocation) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            mFusedLocationClient.getLastLocation().addOnSuccessListener(guestLocation -> {

                if (guestLocation == null) {
                    Toasty.error(getActivity(), "Impossibile verificare codice al momento").show();
                    return;
                }

                if (Tools.getDistance(guestLocation, hostLocation) > 500) {
                    Toasty.error(getActivity(), "Codice non valido").show();
                    return;
                }

                String locationString = guestLocation.getLatitude() + "," + guestLocation.getLongitude();

                Client.getAPI().joinShare(shareID, Client.User.getId(), locationString).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });


            });

        }
    }


    @OnClick(R.id.fab)
    public void onFabClicked() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_new_share, false)
                .show();

        Button newShareButton = (Button) materialDialog.findViewById(R.id.new_share_button);
        Button joinShareButton = (Button) materialDialog.findViewById(R.id.join_share_button);

        newShareButton.setOnClickListener(view -> {
            materialDialog.dismiss();
            startActivity(new Intent(getActivity(), ShareActivity.class));
        });

        joinShareButton.setOnClickListener(view -> {
            materialDialog.dismiss();
            joinShare();
        });


    }
}
