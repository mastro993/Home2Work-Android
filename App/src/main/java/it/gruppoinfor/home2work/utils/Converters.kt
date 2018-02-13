package it.gruppoinfor.home2work.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask

import com.arasthel.asyncjob.AsyncJob

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

import it.gruppoinfor.home2workapi.model.LatLng

/**
 * Created by Federico on 04/02/2017.
 *
 *
 * Funzioni per la conversione di dati
 */

object Converters {

    fun addressToLatLng(context: Context, addr: String): LatLng? {

        val geocoder = Geocoder(context, Locale.ITALY)
        var lat = 0.0
        var lon = 0.0

        try {
            val addressList = geocoder.getFromLocationName(addr, 1)
            if (addressList.size > 0) {
                val address = addressList[0]
                lat = address.latitude
                lon = address.longitude
            } else {
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return LatLng(lat, lon)
    }

    fun dateToString(date: Date, template: String): String {
        val df = SimpleDateFormat(template, Locale.ITALY)
        return df.format(date)
    }

    fun bitmapToFile(context: Context, bitmap: Bitmap): File {
        val filesDir = context.cacheDir
        val imageFile = File(filesDir, "avatar.png")
        val fos: FileOutputStream

        try {
            fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return imageFile
    }

    fun dateToString(date: Date): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.ITALY)
        sdf.timeZone = TimeZone.getTimeZone("GMT+1")
        return sdf.format(date)
    }

}
