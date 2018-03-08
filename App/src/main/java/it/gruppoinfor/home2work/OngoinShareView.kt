package it.gruppoinfor.home2work

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import it.gruppoinfor.home2work.share.OngoingShareActivity
import it.gruppoinfor.home2workapi.share.Guest
import it.gruppoinfor.home2workapi.share.Share
import kotlinx.android.synthetic.main.layout_share_ongoing.view.*
import org.jetbrains.anko.intentFor

class OngoinShareView : FrameLayout {

    constructor(context: Context) : super(context) {

        View.inflate(context, R.layout.layout_share_ongoing, this)

    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {

        View.inflate(context, R.layout.layout_share_ongoing, this)

    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {

        View.inflate(context, R.layout.layout_share_ongoing, this)

    }

    fun setShare(share: Share?) {

        val guestSize = share?.guests?.count { guest -> guest.status != Guest.Status.CANCELED }

        text_ongoin_share_guests.text = guestSize.toString()

        if (share?.type == Share.Type.DRIVER) {

            text_ongoing_share_info.setText(R.string.layout_ongoing_share_host)

        } else {

            text_ongoing_share_info.setText(R.string.layout_ongoing_share_guest)
            layout_guests_number.visibility = View.GONE
        }

        share_item_ongoing_container.setOnClickListener {
            context.startActivity(context.intentFor<OngoingShareActivity>(Constants.EXTRA_SHARE to share))
        }


    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.performClick()
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
    }
}
