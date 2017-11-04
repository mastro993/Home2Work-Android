package it.gruppoinfor.home2work.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import it.gruppoinfor.home2work.utils.Converters;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.Tools;
import it.gruppoinfor.home2workapi.Client;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ConfigurationFragmentAvatar extends Fragment implements BlockingStep {

    private final int PHOTO_INTENT = 0;

    @BindView(R.id.propicView)
    CircleImageView propicView;

    private Bitmap propic;
    private boolean uploaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_conf_propic, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    @OnClick(R.id.selectPhotoButton)
    void selectPhoto() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            selectImageIntent();
        }
    }

    private void selectImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.config_avatar_selection)), PHOTO_INTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_INTENT && resultCode == RESULT_OK) {
            try {

                Uri selectedImageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                propic = Tools.shrinkBitmap(bitmap, 300);
                propicView.setImageBitmap(propic);
                uploaded = false;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImageIntent();
        } else {
            //requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public VerificationError verifyStep() {

        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {

        if (propic == null || uploaded) {
            callback.goToNextStep();
        } else {

            callback.getStepperLayout().showProgress("Upload immagine in corso...");

            File file = Converters.bitmapToFile(getContext(), propic);
            String decodedAvatar = Tools.decodeFile(file.getPath(), 300, 300);
            File decodedFile = new File(decodedAvatar);

            String mime = Tools.getMimeType(decodedFile.getPath());
            MediaType mediaType = MediaType.parse(mime);

            RequestBody requestFile = RequestBody.create(mediaType, decodedFile);

            String filename = Client.getSignedUser().getId() + ".jpg";

            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", filename, requestFile);

            Client.getAPI().uploadAvatar(Client.getSignedUser().getId(), body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                    callback.getStepperLayout().hideProgress();
                    callback.goToNextStep();
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                    callback.getStepperLayout().hideProgress();
                    Toasty.error(getContext(), "Impossibile caricare l'immagine al momento").show();
                }
            });
        }
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }
}
