package it.gruppoinfor.home2work.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.extensions.hide
import it.gruppoinfor.home2work.common.extensions.show
import it.gruppoinfor.home2work.inbox.InboxActivity
import kotlinx.android.synthetic.main.view_inbox_icon.view.*
import org.jetbrains.anko.intentFor

class InboxIconView : RelativeLayout {

    constructor(context: Context) : super(context) {

        View.inflate(context, R.layout.view_inbox_icon, this)
        initUI()

    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {

        View.inflate(context, R.layout.view_inbox_icon, this)
        initUI()

    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {

        View.inflate(context, R.layout.view_inbox_icon, this)
        initUI()

    }

    private fun initUI() {
        inbox_icon_container.setOnClickListener {
            context.startActivity(context.intentFor<InboxActivity>())
        }
    }

    fun setCount(unread: Int) {
        if (unread > 0) {
            //messages_image_view.visibility = View.INVISIBLE
            messages_counter_text_view.show()
            messages_counter_text_view.text = "$unread"
        } else {
            // messages_image_view.visibility = View.VISIBLE
            messages_counter_text_view.hide()
        }
    }

}



