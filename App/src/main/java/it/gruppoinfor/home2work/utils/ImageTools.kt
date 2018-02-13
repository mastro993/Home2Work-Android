package it.gruppoinfor.home2work.utils

import android.graphics.Bitmap
import android.location.Location
import android.os.Environment
import android.webkit.MimeTypeMap

import com.google.android.gms.maps.model.LatLng

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

/**
 * Created by Federico on 04/02/2017.
 *
 *
 * Altre funzioni
 */

object ImageTools {

    fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun decodeFile(path: String): String {
        var strMyImagePath: String? = null
        val scaledBitmap: Bitmap

        try {
            // Part 1: Decode image
            val unscaledBitmap = ScalingUtilities.decodeFile(path, 300, 300, ScalingUtilities.ScalingLogic.FIT)

            if (!(unscaledBitmap.width <= 300 && unscaledBitmap.height <= 300)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, 300, 300, ScalingUtilities.ScalingLogic.FIT)
            } else {
                unscaledBitmap.recycle()
                return path
            }

            // Store to tmp file

            val extr = Environment.getExternalStorageDirectory().toString()
            val mFolder = File(extr + "/TMMFOLDER")
            if (!mFolder.exists()) {
                val dirCreated = mFolder.mkdir()
            }

            val s = "tmp.png"

            val f = File(mFolder.absolutePath, s)

            strMyImagePath = f.absolutePath
            val fos: FileOutputStream
            try {
                fos = FileOutputStream(f)
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos)
                fos.flush()
                fos.close()
            } catch (e: FileNotFoundException) {

                e.printStackTrace()
            }

            scaledBitmap.recycle()
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return if (strMyImagePath == null) {
            path
        } else strMyImagePath

    }

    fun shrinkBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(image, width, height, true)
    }

}
