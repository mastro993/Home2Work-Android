package it.gruppoinfor.home2work.extensions

import android.app.Activity
import android.support.v4.app.Fragment
import android.widget.Toast

fun Activity.showToast(toastMessage: String, toastLenght: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, toastMessage, toastLenght).show()
}

fun Activity.showToast(toastMessageResource: Int, toastLenght: Int = Toast.LENGTH_LONG) {
    showToast(getString(toastMessageResource), toastLenght)
}


fun Fragment.showToast(toastMessage: String, toastLenght: Int = Toast.LENGTH_LONG) {
    Toast.makeText(context!!, toastMessage, toastLenght).show()
}

fun Fragment.showToast(toastMessageResource: Int, toastLenght: Int = Toast.LENGTH_LONG) {
    showToast(getString(toastMessageResource), toastLenght)
}
