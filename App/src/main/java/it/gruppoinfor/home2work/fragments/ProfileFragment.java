package it.gruppoinfor.home2work.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.activities.EditProfileActivity;
import it.gruppoinfor.home2work.activities.MainActivity;
import it.gruppoinfor.home2work.activities.SettingsActivity;
import it.gruppoinfor.home2work.activities.SignInActivity;
import it.gruppoinfor.home2work.custom.AppBarStateChangeListener;
import it.gruppoinfor.home2work.custom.AvatarView;
import it.gruppoinfor.home2work.utils.Converters;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2work.utils.Tools;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.model.User;
import it.gruppoinfor.home2workapi.model.UserProfile;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private final int REQ_CODE_AVATAR = 1;
    private final int REQ_CODE_EXTERNAL_STORAGE = 2;

    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.job_text_view)
    TextView jobTextView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.avatar_view)
    AvatarView avatarView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.text_name_small)
    TextView textNameSmall;
    @BindView(R.id.toolbar_layout)
    View toolbarLayout;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    private Unbinder mUnbinder;
    private Context mContext;
    private UserProfile mProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        setHasOptionsMenu(true);
        initUI();
        refreshData();
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && mProfile == null) refreshData();
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_AVATAR && resultCode == RESULT_OK) {
            try {

                Uri selectedImageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selectedImageUri);
                Bitmap propic = Tools.shrinkBitmap(bitmap, 300);

                File file = Converters.bitmapToFile(mContext, propic);
                String decodedAvatar = Tools.decodeFile(file.getPath());
                File decodedFile = new File(decodedAvatar);

                String mime = Tools.getMimeType(decodedFile.getPath());
                MediaType mediaType = MediaType.parse(mime);

                RequestBody requestFile = RequestBody.create(mediaType, decodedFile);

                String filename = HomeToWorkClient.getUser().getId() + ".jpg";

                MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", filename, requestFile);

                MaterialDialog materialDialog = new MaterialDialog.Builder(mContext)
                        .content(R.string.fragment_profile_avatar_upload)
                        .contentGravity(GravityEnum.CENTER)
                        .progress(true, 150, true)
                        .show();

                HomeToWorkClient.getInstance().uploadAvatar(body,
                        responseBody -> {
                            avatarView.setAvatarURL(HomeToWorkClient.getUser().getAvatarURL());
                            materialDialog.dismiss();
                        },
                        e -> {
                            Toasty.error(mContext, getString(R.string.activity_edit_profile_avatar_upload_error)).show();
                            materialDialog.dismiss();
                        });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.profile_options_button)
    public void onProfileOptionsClicked() {

        new MaterialDialog.Builder(mContext)
                .items(mContext.getResources().getStringArray(R.array.fragment_profile_options))
                .itemsCallback((dialog, itemView, position, text) -> {
                    switch (position) {
                        case 0:
                            startActivity(new Intent(getActivity(), EditProfileActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(getActivity(), SettingsActivity.class));
                            break;
                        case 2:
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.dialog_logout_title);
                            builder.setMessage(R.string.dialog_logout_content);
                            builder.setPositiveButton(R.string.dialog_logout_confirm, ((dialogInterface, i) -> logout()));
                            builder.setNegativeButton(R.string.dialog_logout_decline, null);
                            builder.show();
                            break;
                    }
                })
                .show();

    }

    @OnClick(R.id.avatar_view)
    public void onAvatarClicked() {

        new MaterialDialog.Builder(mContext)
                .title(R.string.fragment_profile_avatar_title)
                .items(mContext.getResources().getStringArray(R.array.fragment_profile_avatar_options))
                .itemsCallback((dialog, itemView, position, text) -> {
                    switch (position) {
                        case 0:
                            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((MainActivity) mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_CODE_EXTERNAL_STORAGE);
                            } else {
                                selectImageIntent();
                            }
                            break;
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_CODE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                selectImageIntent();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void selectImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.activity_configuration_avatar_selection)), REQ_CODE_AVATAR);
    }

    private void initUI() {
        appBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                switch (state) {
                    case COLLAPSED:
                        if (toolbarLayout.getAlpha() < 1.0f) {
                            toolbarLayout.setVisibility(View.VISIBLE);
                            toolbarLayout.animate()
                                    //.translationY(toolbarLayout.getHeight())
                                    .alpha(1.0f)
                                    .setListener(null);
                        }
                        break;
                    case IDLE:
                        if (toolbarLayout.getAlpha() > 0.0f) {
                            toolbarLayout.animate()
                                    .translationY(0)
                                    .alpha(0.0f)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            toolbarLayout.setVisibility(View.GONE);
                                        }
                                    });
                        }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            refreshData();
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        User user = HomeToWorkClient.getUser();
        avatarView.setAvatarURL(user.getAvatarURL());
        nameTextView.setText(user.toString());
        jobTextView.setText(user.getCompany().toString());
        textNameSmall.setText(user.toString());

    }

    private void refreshData() {
        HomeToWorkClient.getInstance().getUserProfile(userProfile -> {
            swipeRefreshLayout.setRefreshing(false);
            mProfile = userProfile;
            refreshUI();
        }, e -> {
            Toasty.error(mContext, "Impossibile ottenere informazioni del profilo al momento").show();
            swipeRefreshLayout.setRefreshing(false);
        });

    }

    private void refreshUI() {
        avatarView.setExp(mProfile.getExp());
    }


    private void logout() {
        SessionManager.clearSession(getContext());

        // Avvio Activity di login
        Intent i = new Intent(getContext(), SignInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

}


