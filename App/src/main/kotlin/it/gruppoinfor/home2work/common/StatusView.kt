package it.gruppoinfor.home2work.common

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import it.gruppoinfor.home2work.R
import kotlinx.android.synthetic.main.custom_status_view.view.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg


class StatusView : RelativeLayout {


    constructor(context: Context) : super(context) {

        View.inflate(context, R.layout.custom_status_view, this)

    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {

        View.inflate(context, R.layout.custom_status_view, this)

    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {

        View.inflate(context, R.layout.custom_status_view, this)

    }

    fun done() {

        image_error.visibility = View.GONE
        loading_view.visibility = View.GONE
        text_info.visibility = View.GONE

        visibility = View.GONE

    }

    fun loading() {

        visibility = View.VISIBLE

        text_info.visibility = View.GONE
        image_error.visibility = View.GONE

        loading_view.visibility = View.VISIBLE

    }

    fun error(errorMessage: String) {

        loading_view.visibility = View.VISIBLE

        async(UI) {
            val text: Deferred<Spanned> = bg { fromHtml(errorMessage) }
            text_info.text = text.await()
        }

        loading_view.visibility = View.GONE

        image_error.visibility = View.VISIBLE
        text_info.visibility = View.VISIBLE

    }

    fun error(errorMessageResource: Int) {

        loading_view.visibility = View.VISIBLE

        text_info.setText(errorMessageResource)

        loading_view.visibility = View.GONE

        image_error.visibility = View.VISIBLE
        text_info.visibility = View.VISIBLE

    }

    fun empty(emptyMessage: String) {

        loading_view.visibility = View.VISIBLE

        async(UI) {
            val text: Deferred<Spanned> = bg { fromHtml(emptyMessage) }
            text_info.text = text.await()
        }

        image_error.visibility = View.GONE
        loading_view.visibility = View.GONE

        text_info.visibility = View.VISIBLE

    }

    fun empty(emptyMessageResource: Int) {

        loading_view.visibility = View.VISIBLE

        text_info.setText(emptyMessageResource)

        image_error.visibility = View.GONE
        loading_view.visibility = View.GONE

        text_info.visibility = View.VISIBLE

    }

    @Suppress("DEPRECATION")
    private fun fromHtml(source: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(source)
        }
    }


}
