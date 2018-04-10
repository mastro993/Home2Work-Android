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
import it.gruppoinfor.home2work.common.ImageLoader
import it.gruppoinfor.home2work.common.utilities.StaticMapUriBuilder


class ShareCompleteDialog constructor(context: Context, val share: Share, val imageLoader: ImageLoader) : AlertDialog(context) {

    private val mDf: DecimalFormat = DecimalFormat("#.##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_share_completed)

        val staticMapUrl = StaticMapUriBuilder.buildFor(share)
        imageLoader.load(url = staticMapUrl, imageView = share_map, fit = true)

        button_close.setOnClickListener { dismiss() }
        text_share_distance.text = mDf.format(share.sharedDistance.div(1000.0))
        text_share_xp.text = (share.sharedDistance.div(100)).toString()



    }
}