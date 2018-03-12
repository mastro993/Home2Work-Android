package it.gruppoinfor.home2work.shares

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.animation.AnimationUtils
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2workapi.share.Share
import kotlinx.android.synthetic.main.activity_shares.*

class SharesActivity : AppCompatActivity(), SharesView {

    private var mSharesAdapter: SharesAdapter? = null
    private val mSharesPresenter: SharesPresenter = SharesPresenterImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shares)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initUI()
    }

    override fun onResume() {
        super.onResume()

        mSharesPresenter.onResume()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()

        mSharesPresenter.onPause()
    }

    private fun initUI() {

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)

        shares_recycler_view.layoutManager = layoutManager
        shares_recycler_view.layoutAnimation = animation

        mSharesAdapter = SharesAdapter(this, ArrayList())
        shares_recycler_view.adapter = mSharesAdapter
    }

    override fun onLoading() {
        status_view.loading()
    }

    override fun onLoadingError(errorMessage: String) {
        status_view.error(errorMessage)
    }

    override fun setItems(list: List<Share>) {
        mSharesAdapter?.setItems(list)
        status_view.done()
    }
}
