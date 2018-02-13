package it.gruppoinfor.home2work.custom

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.annimon.stream.Stream

import butterknife.BindView
import butterknife.ButterKnife
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.OngoingShareActivity
import it.gruppoinfor.home2workapi.model.Guest
import it.gruppoinfor.home2workapi.model.Share

import it.gruppoinfor.home2work.activities.OngoingShareActivity.EXTRA_SHARE

class OngoinShareView : FrameLayout {

    @BindView(R.id.text_ongoing_share_info)
    internal var textOngoingShareInfo: TextView? = null
    @BindView(R.id.text_ongoin_share_guests)
    internal var textOngoinShareGuests: TextView? = null
    @BindView(R.id.share_item_ongoing_container)
    internal var shareItemOngoingContainer: RelativeLayout? = null
    @BindView(R.id.layout_guests_number)
    internal var layoutGuestsNumber: LinearLayout? = null
    private var mContext: Context? = null

    constructor(context: Context) : super(context) {
        mContext = context
        initUI()
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        mContext = context
        initUI()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        mContext = context
        initUI()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    private fun initUI() {
        val view = View.inflate(context, R.layout.layout_share_ongoing, this)
        ButterKnife.bind(this, view)
    }

    fun setShare(share: Share) {

        val guestSize = Stream.of(share.guests).filter { value -> value.status != Guest.Status.CANCELED }.count()
        textOngoinShareGuests!!.text = guestSize.toString()

        if (share.type == Share.Type.DRIVER) {

            textOngoingShareInfo!!.setText(R.string.layout_ongoing_share_host)

        } else {

            textOngoingShareInfo!!.setText(R.string.layout_ongoing_share_guest)
            layoutGuestsNumber!!.visibility = View.GONE
        }

        shareItemOngoingContainer!!.setOnClickListener { view ->
            val intent = Intent(mContext, OngoingShareActivity::class.java)
            intent.putExtra(EXTRA_SHARE, share)
            mContext!!.startActivity(intent)
        }

    }
}
