package it.gruppoinfor.home2work.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.App;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2work.adapters.SharesAdapter;
import it.gruppoinfor.home2work.custom.OngoinShareView;
import it.gruppoinfor.home2work.utils.Tools;
import it.gruppoinfor.home2workapi.model.Share;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class SharesFragment extends Fragment implements ItemClickCallbacks {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;

    @BindView(R.id.shares_recycler_view)
    RecyclerView sharesRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.new_share_container)
    RelativeLayout newShareContainer;
    @BindView(R.id.ongoing_share_view)
    OngoinShareView ongoingShareView;
    @BindView(R.id.new_share_container_empty)
    ConstraintLayout newShareContainerEmpty;

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

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        sharesRecyclerView.setNestedScrollingEnabled(false);

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

    @Override
    public void onItemClick(View view, int position) {
        // TODO share click
        /*Share share = mShareList.get(position);
        Intent intent = new Intent(getActivity(), OngoingShareActivity.class);
        intent.putExtra("SHARE_ID", share.getId());
        getActivity().startActivity(intent);*/
    }

    @Override
    public boolean onLongItemClick(View view, int position) {
        // TODO share long click
        /*MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_match_item_options, false)
                .show();

        Button showUserProfileButton = (Button) materialDialog.findViewById(R.id.show_user_profile_button);
        Button hideMatchButton = (Button) materialDialog.findViewById(R.id.hide_match_button);

        showUserProfileButton.setOnClickListener(view1 -> {
            materialDialog.dismiss();
            showMatchUserProfile(position);
        });

        hideMatchButton.setOnClickListener(view12 -> {
            materialDialog.dismiss();
            showHideMatchDialog(position);
        });*/

        return true;
    }

    @OnClick({R.id.new_share_container, R.id.new_share_container_empty})
    public void onNewShareClicked() {
        String[] options = new String[]{"Crea nuova condivisione", "Unisciti a condivisione"};

        new MaterialDialog.Builder(getActivity())
                .title("Nuova condivisione")
                .items(options)
                .itemsCallback((dialog, itemView, position, text) -> {
                    switch (position) {
                        case 0:
                            createShare();
                            break;
                        case 1:
                            joinShare();
                            break;
                    }
                })
                .show();
    }

    private void initUI() {
        swipeRefreshLayout.setRefreshing(false);

        newShareContainer.setVisibility(View.VISIBLE);
        ongoingShareView.setVisibility(View.GONE);

        for (Share share : mShareList) {
            if (share.getStatus() == Share.Status.CREATED) {
                ongoingShareView.setShare(share);
                mShareList.remove(share);
                newShareContainer.setVisibility(View.GONE);
                ongoingShareView.setVisibility(View.VISIBLE);
                break;
            }
        }

        if (mShareList.size() == 0) {
            sharesRecyclerView.setVisibility(View.GONE);
            newShareContainerEmpty.setVisibility(View.VISIBLE);
        } else {
            sharesRecyclerView.setVisibility(View.VISIBLE);
            newShareContainerEmpty.setVisibility(View.GONE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);

            sharesRecyclerView.setLayoutManager(layoutManager);
            sharesRecyclerView.setLayoutAnimation(animation);

            mSharesAdapter = new SharesAdapter(getActivity(), mShareList);
            mSharesAdapter.setItemClickCallbacks(this);
            sharesRecyclerView.setAdapter(mSharesAdapter);
        }
    }

    private void refreshData() {
        swipeRefreshLayout.setRefreshing(true);

        App.home2WorkClient.getUserShares(shares -> {
            mShareList.clear();
            mShareList.addAll(shares);
            initUI();
        }, e -> {
            Toasty.error(getContext(), "Impossibile ottenere lista condivsioni al momento").show();
            initUI();
        });

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
            mFusedLocationClient.getLastLocation().addOnSuccessListener(joinLocation -> {

                if (joinLocation == null) {
                    Toasty.error(getActivity(), "Impossibile verificare codice al momento").show();
                    return;
                }

                if (Tools.getDistance(joinLocation, hostLocation) > 500) {
                    Toasty.error(getActivity(), "Codice non valido").show();
                    return;
                }

                App.home2WorkClient.joinShare(shareID, joinLocation, new OnSuccessListener<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        // TODO unione share
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // TODO errore unione
                    }
                });

            });

        }
    }

    private void createShare() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .title("Nuova condivisione")
                .content("Creazione di una condivisione in corso.")
                .contentGravity(GravityEnum.CENTER)
                .progress(false, 150, true)
                .show();

        App.home2WorkClient.createShare(share -> {
            materialDialog.dismiss();
            newShareContainer.setVisibility(View.GONE);
            mShareList.add(0, share);
            mSharesAdapter.notifyDataSetChanged();
            Toasty.success(getContext(), "Condivisione creata con successo").show();

        }, e -> {
            materialDialog.dismiss();
            Toasty.error(getContext(), "Impossibile creare nuova condivisione").show();
        });


    }

}
