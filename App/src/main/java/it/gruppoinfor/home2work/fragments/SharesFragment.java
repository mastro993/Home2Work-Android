package it.gruppoinfor.home2work.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.OngoingShareActivity;
import it.gruppoinfor.home2work.adapters.SharesAdapter;
import it.gruppoinfor.home2work.custom.OngoinShareView;
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks;
import it.gruppoinfor.home2work.utils.Tools;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.model.Guest;
import it.gruppoinfor.home2workapi.model.Share;

import static it.gruppoinfor.home2work.activities.OngoingShareActivity.EXTRA_SHARE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SharesFragment extends Fragment implements ItemClickCallbacks {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;

    @BindView(R.id.shares_recycler_view)
    RecyclerView sharesRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.ongoing_share_view)
    OngoinShareView ongoingShareView;
    @BindView(R.id.new_share_container_empty)
    View newShareContainerEmpty;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    @BindView(R.id.fab_new_share)
    FloatingActionButton fabNewShare;

    private Unbinder unbinder;
    private Context mContext;
    private Share mOngoingShare = null;

    private List<Share> mShareList = new ArrayList<>();

    public SharesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shares, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            refreshData();
        });
        sharesRecyclerView.setNestedScrollingEnabled(false);

        newShareContainerEmpty.setVisibility(View.GONE);
        refreshData();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
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
            Toasty.error(mContext, mContext.getString(R.string.activity_ongoing_share_invalid_code));
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

        return true;
    }

    @OnClick({R.id.fab_new_share, R.id.button_first_share})
    public void onNewShareClicked() {

        new MaterialDialog.Builder(mContext)
                .title(R.string.fragment_share_dialog_new_title)
                .items(R.array.fragment_share_dialog_new_share_options)
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


        if (mShareList.size() == 0) {
            sharesRecyclerView.setVisibility(View.GONE);
            fabNewShare.setVisibility(View.GONE);
            newShareContainerEmpty.setVisibility(View.VISIBLE);
        } else {
            sharesRecyclerView.setVisibility(View.VISIBLE);
            fabNewShare.setVisibility(View.VISIBLE);
            newShareContainerEmpty.setVisibility(View.GONE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);

            sharesRecyclerView.setLayoutManager(layoutManager);
            sharesRecyclerView.setLayoutAnimation(animation);

            SharesAdapter mSharesAdapter = new SharesAdapter(getActivity(), mShareList);
            mSharesAdapter.setItemClickCallbacks(this);
            sharesRecyclerView.setAdapter(mSharesAdapter);
        }

        if (mOngoingShare != null) {
            newShareContainerEmpty.setVisibility(View.GONE);
            fabNewShare.setVisibility(View.GONE);
            ongoingShareView.setVisibility(View.VISIBLE);


            ongoingShareView.setShare(mOngoingShare);
            ((MainActivity) mContext).setBadge(2, "In corso");


        } else {
            ongoingShareView.setVisibility(View.GONE);
            ((MainActivity) mContext).setBadge(2, "");
        }

    }

    private void refreshData() {
        HomeToWorkClient.getInstance().getUserShares(shares -> {

            mOngoingShare = null;

            Optional<Share> ongoingShareOptional = Stream.of(shares)
                    .filter(value -> value.getStatus().equals(Share.Status.CREATED))
                    .findFirst();

            if (ongoingShareOptional.isPresent()) {

                Share ongoingShare = ongoingShareOptional.get();

                // Controllo se l'utente è host o guest della condivisione
                if (ongoingShare.getHost().equals(HomeToWorkClient.getUser())) {
                    mOngoingShare = ongoingShare;
                    shares.remove(mOngoingShare);
                } else {
                    // Se è guest controllo se ha completato la condivisione o è ancora in corso
                    Optional<Guest> shareGuestOptional = Stream.of(ongoingShare.getGuests())
                            .filter(value -> value.getUser().equals(HomeToWorkClient.getUser()) && value.getStatus().equals(Guest.Status.JOINED))
                            .findFirst();
                    if (shareGuestOptional.isPresent()) {
                        mOngoingShare = ongoingShare;
                        shares.remove(mOngoingShare);
                    }
                }


            }

            mShareList.clear();
            mShareList.addAll(shares);
            initUI();

        }, e -> {
            //Toasty.error(getContext(), "Impossibile ottenere lista condivsioni al momento").show();
            initUI();
        });

    }

    public void joinShare() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((MainActivity) mContext, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.initiateScan();
        }
    }

    private void checkShareCode(Long shareID, LatLng hostLocation) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(joinLocation -> {

                if (joinLocation == null) {
                    Toasty.error(mContext, mContext.getString(R.string.activity_ongoing_share_invalid_code)).show();
                    return;
                }

                if (Tools.getDistance(joinLocation, hostLocation) > 500) {
                    Toasty.error(mContext, mContext.getString(R.string.activity_ongoing_share_invalid_code)).show();
                    return;
                }

                HomeToWorkClient.getInstance().joinShare(shareID, joinLocation, share -> {
                    Intent intent = new Intent(getActivity(), OngoingShareActivity.class);
                    intent.putExtra(EXTRA_SHARE, share);
                    mContext.startActivity(intent);
                }, Throwable::printStackTrace);

            });

        }
    }

    private void createShare() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(mContext)
                .title(R.string.fragment_share_dialog_new_title)
                .content(R.string.fragment_share_dialog_new_content)
                .contentGravity(GravityEnum.CENTER)
                .progress(true, 150, true)
                .show();

        HomeToWorkClient.getInstance().createShare(share -> {
            materialDialog.dismiss();
            fabNewShare.setVisibility(View.GONE);
            mOngoingShare = share;
            initUI();
            Intent intent = new Intent(getActivity(), OngoingShareActivity.class);
            intent.putExtra(EXTRA_SHARE, share);
            mContext.startActivity(intent);
        }, e -> {
            materialDialog.dismiss();
            Toasty.error(mContext, mContext.getString(R.string.fragment_share_dialog_new_error)).show();
        });


    }

}
