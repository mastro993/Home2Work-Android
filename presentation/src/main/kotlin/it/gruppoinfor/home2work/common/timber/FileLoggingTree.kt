package it.gruppoinfor.home2work.common.timber

import android.os.Environment
import android.util.Log
import it.gruppoinfor.home2work.BuildConfig
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.io.*


class FileLoggingTree : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        val titleColor = when (priority) {
            Log.VERBOSE -> {
                "#B0BEC5"
            }
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


            val path = "Log"
            val fileNameTimeStamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val logTimeStamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val fileName = "$fileNameTimeStamp.html"

            val file = generateFile(path, fileName)

            //val file = File(logsFile.toString() + File.separator + fileName)
            //file.createNewFile()

            file?.let {
                if (it.exists()) {
                    val content = it.readText()

                    it.writeText("<p>" +
                            "<strong style=\"background:#90A4AE;\">&nbsp&nbsp$logTimeStamp&nbsp&nbsp</strong>" +
                            "<strong style=\"background:$titleColor;\">&nbsp&nbsp$tag&nbsp&nbsp</strong><br/>" +
                            "<span style=\"background:#ECEFF1;\">&nbsp&nbsp$message</span>" +
                            "</p>" + content)

                    /*val fileOutputStream = FileOutputStream(it, false)

                    fileOutputStream.write(("<p>" +
                            "<strong style=\"background:#90A4AE;\">&nbsp&nbsp$logTimeStamp&nbsp&nbsp</strong>" +
                            "<strong style=\"background:$titleColor;\">&nbsp&nbsp$tag&nbsp&nbsp</strong><br/>" +
                            "<span style=\"background:#ECEFF1;\">&nbsp&nbsp$message</span>" +
                            "</p>").toByteArray())

                    fileOutputStream.close()*/

                }
            }

            //if (context != null)
            //MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);

        } catch (e: Exception) {
            Log.e(FileLoggingTree::class.java.simpleName, "Error while logging into file : $e")
        }
    }

    override fun createStackElementTag(element: StackTraceElement): String? {
        return super.createStackElementTag(element) + " - " + element.lineNumber
    }

    private fun generateFile(path: String, fileName: String): File? {
        var file: File? = null
        if (isExternalStorageAvailable()) {
            val root = File(Environment.getExternalStorageDirectory().absolutePath,
                    BuildConfig.APPLICATION_ID + File.separator + path)

            var dirExists = true

            if (!root.exists()) {
                dirExists = root.mkdirs()
            }

            if (dirExists) {
                file = File(root, fileName)
            }

            file?.let {
                if (!it.exists()) {
                    it.createNewFile()
                }
            }
        }
        return file
    }

    private fun isExternalStorageAvailable(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }
}