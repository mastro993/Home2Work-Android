package it.gruppoinfor.home2work.custom

import android.content.Context
import android.text.Html
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.facebook.stetho.common.StringUtil
import it.gruppoinfor.home2work.R
import kotlinx.android.synthetic.main.custom_status_view.view.*
import android.os.Build
import android.text.Spanned



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

        error_view.visibility = View.GONE
        loading_view.visibility = View.GONE
        empty_view.visibility = View.GONE

    }

    fun loading() {

        error_view.visibility = View.GONE
        empty_view.visibility = View.GONE
        loading_view.visibility = View.VISIBLE

    }

    fun error(errorMessage: String) {

        text_error.text = fromHtml(errorMessage)

        loading_view.visibility = View.GONE
        empty_view.visibility = View.GONE
        error_view.visibility = View.VISIBLE

    }

    fun error(errorMessageResource: Int) {

        text_error.setText(errorMessageResource)

        loading_view.visibility = View.GONE
        empty_view.visibility = View.GONE
        error_view.visibility = View.VISIBLE

    }

    fun empty(emptyMessage: String) {

        text_empty.text = fromHtml(emptyMessage)

        error_view.visibility = View.GONE
        loading_view.visibility = View.GONE
        empty_view.visibility = View.VISIBLE

    }

    fun empty(emptyMessageResource: Int) {

        text_empty.setText(emptyMessageResource)

        error_view.visibility = View.GONE
        loading_view.visibility = View.GONE
        empty_view.visibility = View.VISIBLE

    }

    private fun fromHtml(source: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(source)
        }
    }



}
