package it.gruppoinfor.home2work.common.views

import android.content.Context
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.text.Html
import android.text.Spanned
import android.util.AttributeSet
import android.view.View
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.extensions.hide
import it.gruppoinfor.home2work.common.extensions.show
import kotlinx.android.synthetic.main.view_screen_state.view.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg


class ScreenStateView : ConstraintLayout {


    constructor(context: Context) : super(context) {

        View.inflate(context, R.layout.view_screen_state, this)

    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {

        View.inflate(context, R.layout.view_screen_state, this)

    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {

        View.inflate(context, R.layout.view_screen_state, this)

    }

    fun setScreenState(screenState: ScreenState?) {
        screenState?.let {
            when (screenState) {
                is ScreenState.Loading -> loading()
                is ScreenState.Error -> error(screenState.errorMessage)
                is ScreenState.Empty -> empty(screenState.emptyMessage)
                is ScreenState.Done -> done()
            }
        } ?: done()
    }

    private fun done() {
        hide()

        image_error.hide()
        loading_view.hide()
        text_info.hide()
    }

    private fun loading() {
        show()

        text_info.hide()
        image_error.hide()
        loading_view.show()
    }

    private fun error(errorMessage: String?) {
        show()

        errorMessage?.let {
            async(UI) {
                val text: Deferred<Spanned> = bg { fromHtml(it) }
                text_info.text = text.await()
            }
        }

        loading_view.hide()
        image_error.show()
        text_info.show()
    }

    private fun error(errorMessageResource: Int) {
        show()

        text_info.setText(errorMessageResource)

        loading_view.hide()
        image_error.show()
        text_info.show()

    }

    private fun empty(emptyMessage: String?) {
        show()

        emptyMessage?.let {
            async(UI) {
                val text: Deferred<Spanned> = bg { fromHtml(emptyMessage) }
                text_info.text = text.await()
            }
        }

        image_error.hide()
        loading_view.hide()
        text_info.show()

    }

    private fun empty(emptyMessageResource: Int) {
        show()

        text_info.setText(emptyMessageResource)

        image_error.hide()
        loading_view.hide()
        text_info.show()

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
