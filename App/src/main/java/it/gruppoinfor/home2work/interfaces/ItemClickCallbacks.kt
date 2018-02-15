package it.gruppoinfor.home2work.interfaces

import android.view.View


interface ItemClickCallbacks {
    fun onItemClick(view: View, position: Int)
    fun onLongItemClick(view: View, position: Int): Boolean
}
