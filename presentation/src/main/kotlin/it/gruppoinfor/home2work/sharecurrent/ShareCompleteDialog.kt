package it.gruppoinfor.home2work.sharecurrent

import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.entities.Share
import kotlinx.android.synthetic.main.dialog_share_completed.*
import java.text.DecimalFormat
import com.google.android.gms.maps.SupportMapFragment



class ShareCompleteDialog constructor(context: Context, val share: Share) : AlertDialog(context) {

    private val mDf: DecimalFormat = DecimalFormat("#.##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_share_completed)

        button_close.setOnClickListener { dismiss() }
        text_share_distance.text = mDf.format(share.sharedDistance.div(1000.0))
        text_share_xp.text = (share.sharedDistance.div(100)).toString()



    }
}