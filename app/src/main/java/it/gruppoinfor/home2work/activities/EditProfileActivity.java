package it.gruppoinfor.home2work.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.utils.Converters;
import it.gruppoinfor.home2work.utils.Tools;
import it.gruppoinfor.home2workapi.Home2WorkClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditProfileActivity extends AppCompatActivity {

    private final int PHOTO_INTENT = 0;

    @BindView(R.id.avatar_view)
    ImageView avatarView;
    private Bitmap propic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_INTENT && resultCode == RESULT_OK) {
            try {

                Uri selectedImageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                propic = Tools.shrinkBitmap(bitmap, 300);

                File file = Converters.bitmapToFile(this, propic);
                String decodedAvatar = Tools.decodeFile(file.getPath(), 300, 300);
                File decodedFile = new File(decodedAvatar);

                String mime = Tools.getMimeType(decodedFile.getPath());
                MediaType mediaType = MediaType.parse(mime);

                RequestBody requestFile = RequestBody.create(mediaType, decodedFile);

                String filename = Home2WorkClient.User.getId() + ".jpg";

                MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", filename, requestFile);

                Home2WorkClient home2WorkClient = new Home2WorkClient();

                home2WorkClient.API.uploadAvatar(Home2WorkClient.User.getId(), body)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(responseBody -> {
                            if (responseBody.code() == 201) {
                                initUI();
                                Toasty.success(EditProfileActivity.this, "Immagine modificata con successo").show();
                            } else
                                Toasty.error(EditProfileActivity.this, "Impossibile caricare l'immagine al momento").show();
                        }, throwable -> Toasty.error(EditProfileActivity.this, "Impossibile caricare l'immagine al momento").show());


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.change_avatar_button)
    public void onViewClicked() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            selectImageIntent();
        }
    }

    private void initUI() {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder);
        Glide.with(this)
                .load(Home2WorkClient.User.getAvatarURL())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(avatarView);
    }

    private void selectImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.config_avatar_selection)), PHOTO_INTENT);
    }
}

