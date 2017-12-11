package it.gruppoinfor.home2work.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.adapters.ItemClickCallbacks;
import it.gruppoinfor.home2work.adapters.OngoingSharesAdapter;
import it.gruppoinfor.home2work.adapters.SharesAdapter;
import it.gruppoinfor.home2work.utils.QREncoder;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Share;

public class ProgressFragmentDashboard extends Fragment {


    @BindView(R.id.ongoing_shares_list)
    RecyclerView ongoingSharesList;
    private Unbinder unbinder;
    private OngoingSharesAdapter ongoingSharesAdapter;

    public ProgressFragmentDashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout root = (FrameLayout) inflater.inflate(R.layout.fragment_progress_dashboard, container, false);
        unbinder = ButterKnife.bind(this, root);
        initUI();
        return root;
    }

    private void initUI() {
        initOngoingSharesList();

    }

    private void initOngoingSharesList(){
        ArrayList<Share> ongoingShares = new ArrayList<>();
        for(Share s : Client.getUserShares())
            if(s.getStatus() == Share.ONGOING) ongoingShares.add(s);

        if(ongoingShares.size() != 0){
            ongoingSharesList.setVisibility(View.VISIBLE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);

            ongoingSharesList.setLayoutManager(layoutManager);
            ongoingSharesList.setLayoutAnimation(animation);

            ongoingSharesAdapter = new OngoingSharesAdapter(getActivity(), ongoingShares);
            ongoingSharesAdapter.setItemClickCallbacks(new ItemClickCallbacks() {
                @Override
                public void onItemClick(View view, int position) {
                    MaterialDialog qrCodeDialog = new MaterialDialog.Builder(getContext())
                            .customView(R.layout.dialog_share_qr_code, false)
                            .build();

                    ImageView qrCodeImage = (ImageView) qrCodeDialog.findViewById(R.id.qr_code_image_view);
                    FrameLayout loadingView = (FrameLayout) qrCodeDialog.findViewById(R.id.loading_view);

                    try {
                        Bitmap bitmap = QREncoder.EncodeText(ongoingShares.get(position).getCode());
                        qrCodeImage.setImageBitmap(bitmap);
                        loadingView.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        Toasty.error(getContext(), "Impossibile ottenere QR Code al momento").show();
                        e.printStackTrace();
                    }

                    MaterialDialog shareInfoDialog = new MaterialDialog.Builder(getContext())
                            .customView(R.layout.dialog_ongoing_share_details, false)
                            .build();

                    shareInfoDialog.show();
                }

                @Override
                public boolean onLongItemClick(View view, int position) {
                    return false;
                }
            });
            ongoingSharesList.setAdapter(ongoingSharesAdapter);
        } else {
            ongoingSharesList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

}
