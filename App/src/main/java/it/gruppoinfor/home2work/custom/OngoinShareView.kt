package it.gruppoinfor.home2work.custom

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.OngoingShareActivity
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2workapi.model.Guest
import it.gruppoinfor.home2workapi.model.Share
import kotlinx.android.synthetic.main.layout_share_ongoing.view.*

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

        val extra = Const.EXTRA_SHARE

        share_item_ongoing_container.setOnClickListener {
            val intent = Intent(context, OngoingShareActivity::class.java)
            intent.putExtra(extra, share)
            context.startActivity(intent)
        }

    }
}
