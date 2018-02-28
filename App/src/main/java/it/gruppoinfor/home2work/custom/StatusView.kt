package it.gruppoinfor.home2work.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import it.gruppoinfor.home2work.R
import kotlinx.android.synthetic.main.custom_status_view.view.*

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
    }

    fun loading() {
        error_view.visibility = View.GONE
        loading_view.visibility = View.VISIBLE
    }

    fun error(errorMessage: String) {
        text_error.text = errorMessage
        error_view.visibility = View.VISIBLE
        loading_view.visibility = View.GONE
    }


}
