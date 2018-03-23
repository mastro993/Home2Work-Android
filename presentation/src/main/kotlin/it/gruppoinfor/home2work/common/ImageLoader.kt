package it.gruppoinfor.home2work.common

import android.widget.ImageView
import com.squareup.picasso.Transformation


interface ImageLoader {
    fun load(url: String, imageView: ImageView, transformation: Transformation? = null, placeholder: Int? = null, callback: ((Boolean) -> Unit)? = null, fadeEffect: Boolean = true)


}