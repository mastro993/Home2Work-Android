package it.gruppoinfor.home2work.common

import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation


class PicassoImageLoader(private val picasso: Picasso) : ImageLoader {


    override fun load(url: String, imageView: ImageView, transformation: Transformation?, placeholder: Int?, callback: ((Boolean) -> Unit)?, fadeEffect: Boolean) {
        val request = picasso.load(url)

        placeholder?.let {
            request.placeholder(it)
        }

        transformation?.let {
            request.transform(it)
        }

        if (!fadeEffect) {
            request.noFade()
        }

        callback?.let {
            request.into(imageView, FetchCallback(it))
        } ?: request.into(imageView)


    }


    private class FetchCallback(val delegate: (Boolean) -> Unit) : Callback {
        override fun onSuccess() {
            delegate(true)
        }

        override fun onError() {
            delegate(false)
        }

    }
}