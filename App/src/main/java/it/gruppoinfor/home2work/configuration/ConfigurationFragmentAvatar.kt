package it.gruppoinfor.home2work.configuration

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.api.HomeToWorkClient
import it.gruppoinfor.home2work.utils.ImageUtils
import kotlinx.android.synthetic.main.fragment_conf_propic.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ConfigurationFragmentAvatar : Fragment(), BlockingStep {

    val REQ_CAMERA = 3

    private var propic: Bitmap? = null
    private var uploaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_conf_propic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectPhotoButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                selectImageIntent()
            }
        }

    }

    private fun selectImageIntent() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.activity_configuration_avatar_selection)), REQ_CAMERA)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQ_CAMERA && resultCode == Activity.RESULT_OK) {
            try {

                val selectedImageUri = data?.data
                val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, selectedImageUri)
                propic = ImageUtils.shrinkBitmap(bitmap, 300)
                propicView!!.setImageBitmap(propic)
                uploaded = false

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImageIntent()
        }

    }

    override fun verifyStep(): VerificationError? {

        return null
    }

    override fun onSelected() {

    }

    override fun onError(error: VerificationError) {

    }

    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback) {

        if (propic == null || uploaded) {
            callback.goToNextStep()
        } else {
            callback.stepperLayout.showProgress(getString(R.string.activity_configuration_avatar_upload))

            val file = ImageUtils.bitmapToFile(context!!.cacheDir, propic!!)
            val decodedAvatar = ImageUtils.decodeFile(file.path)
            val decodedFile = File(decodedAvatar)

            val mime = ImageUtils.getMimeType(decodedFile.path)
            val mediaType = MediaType.parse(mime!!)

            val requestFile = RequestBody.create(mediaType, decodedFile)

            val filename = "${HomeToWorkClient.user?.id}.jpg"

            val body = MultipartBody.Part.createFormData("avatar", filename, requestFile)

            HomeToWorkClient.getUserService().uploadAvatar(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn { null }
                    .subscribe({
                        callback.stepperLayout.hideProgress()
                        callback.goToNextStep()
                    }, {
                        callback.stepperLayout.hideProgress()
                        Toast.makeText(context!!, R.string.activity_configuration_avatar_upload_error, Toast.LENGTH_SHORT).show()
                    })

        }
    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback) {

    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {

        callback.goToPrevStep()

    }
}