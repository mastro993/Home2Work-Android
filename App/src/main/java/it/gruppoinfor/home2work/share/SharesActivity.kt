package it.gruppoinfor.home2work.share

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.animation.AnimationUtils
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.share.Share
import kotlinx.android.synthetic.main.activity_shares.*

class SharesActivity : AppCompatActivity() {

    private var mSharesAdapter: SharesAdapter? = null
    private val mShareList = ArrayList<Share>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shares)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initUI()
    }

    override fun onStart() {
        super.onStart()

        getShares()
    }

    private fun initUI() {

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)

        shares_recycler_view.layoutManager = layoutManager
        shares_recycler_view.layoutAnimation = animation

        mSharesAdapter = SharesAdapter(this, mShareList)
        shares_recycler_view.adapter = mSharesAdapter
    }

    private fun getShares() {

        status_view.loading()

        HomeToWorkClient.getShareList(OnSuccessListener { shares ->

            status_view.done()

            mShareList.clear()
            mShareList.addAll(shares)
            mSharesAdapter?.notifyDataSetChanged()

        }, OnFailureListener {

            status_view.error("Impossibile ottenere lista condivisioni")

        })

    }
}
