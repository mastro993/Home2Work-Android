package it.gruppoinfor.home2work.custom

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.annimon.stream.Stream
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.OngoingShareActivity
import it.gruppoinfor.home2work.user.Const
import it.gruppoinfor.home2workapi.model.Guest
import it.gruppoinfor.home2workapi.model.Share
import kotlinx.android.synthetic.main.layout_share_ongoing.view.*

class OngoinShareView : FrameLayout {

    private var mContext: Context? = null

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.layout_share_ongoing, this)
        mContext = context
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        View.inflate(context, R.layout.layout_share_ongoing, this)
        mContext = context
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        View.inflate(context, R.layout.layout_share_ongoing, this)
        mContext = context
    }

    fun setShare(share: Share) {

        val guestSize = Stream.of(share.guests).filter { value -> value.status != Guest.Status.CANCELED }.count()
        text_ongoin_share_guests.text = guestSize.toString()

        if (share.type == Share.Type.DRIVER) {

            text_ongoing_share_info.setText(R.string.layout_ongoing_share_host)

        } else {

            text_ongoing_share_info.setText(R.string.layout_ongoing_share_guest)
            layout_guests_number.visibility = View.GONE
        }

        share_item_ongoing_container.setOnClickListener {
            val intent = Intent(mContext, OngoingShareActivity::class.java)
            intent.putExtra(Const.EXTRA_SHARE, share)
            mContext!!.startActivity(intent)
        }

    }
}
