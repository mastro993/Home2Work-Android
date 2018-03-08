package it.gruppoinfor.home2work.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class FileLoggingTree(var context: Context) : Timber.DebugTree() {

    private val logsFile: File = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/HomeToWork/Logs")

    init {

        if (!logsFile.exists()) {
            logsFile.mkdirs()
        }
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        val titleColor = when (priority) {
            Log.DEBUG -> {
                "#81C784"
            }
            Log.ERROR -> {
                "#E57373"
            }
            Log.WARN -> {
                "#FFF176"
            }
            else -> {
                "#4DD0E1"
            }
        }

        try {

            val fileNameTimeStamp = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            val logTimeStamp = SimpleDateFormat("hh:mm aaa", Locale.getDefault()).format(Date())

            val fileName = "$fileNameTimeStamp.html"

            val file = File(logsFile.toString() + File.separator + fileName)

            file.createNewFile()

            if (file.exists()) {

                val fileOutputStream = FileOutputStream(file, true)

                fileOutputStream.write(("<p>" +
                        "<strong style=\"background:#90A4AE;\">&nbsp&nbsp$logTimeStamp&nbsp&nbsp</strong>" +
                        "<strong style=\"background:$titleColor;\">&nbsp&nbsp$tag&nbsp&nbsp</strong><br/>" +
                        "<span style=\"background:#ECEFF1;\">&nbsp&nbsp$message</span>" +
                        "</p>").toByteArray())
                fileOutputStream.close()

            }

            //if (context != null)
            //MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);

        } catch (e: Exception) {
            Timber.e(FileLoggingTree::class.java.simpleName, "Error while logging into file : $e")
        }
    }
}