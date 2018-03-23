package it.gruppoinfor.home2work.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.Toast


fun View.hide(){
    visibility = View.GONE
}
fun View.show(){
    visibility = View.VISIBLE
}






/**
 * Permette di mostrare un Toast con showToast(messaggio) oppure showToast(messaggio, lunghezzaToast)
 */
fun Activity.showToast(toastMessage: String, toastLenght: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, toastMessage, toastLenght).show()
}

fun Fragment.showToast(toastMessage: String, toastLenght: Int = Toast.LENGTH_LONG) {
    Toast.makeText(context!!, toastMessage, toastLenght).show()
}

/**
 * Stessa cosa, ma utilizzando una string resource
 */
fun Activity.showToast(toastMessageResource: Int, toastLenght: Int = Toast.LENGTH_LONG) {
    showToast(getString(toastMessageResource), toastLenght)
}

fun Fragment.showToast(toastMessageResource: Int, toastLenght: Int = Toast.LENGTH_LONG) {
    showToast(getString(toastMessageResource), toastLenght)
}


/**
 * Lancio di nuove Activity semplificato
 * Esempio: launcActivity<MiaActivity>()
 */
inline fun <reified T : Any> Activity.launchActivity(
        requestCode: Int = -1,
        options: Bundle? = null,
        noinline init: Intent.() -> Unit = {}
) {

    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivityForResult(intent, requestCode)
    }
}

inline fun <reified T : Any> Context.launchActivity(
        options: Bundle? = null,
        noinline init: Intent.() -> Unit = {}
) {

    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivity(intent, options)
    } else {
        startActivity(intent)
    }
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
        Intent(context, T::class.java)
