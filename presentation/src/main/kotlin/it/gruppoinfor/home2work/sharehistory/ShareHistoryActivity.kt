package it.gruppoinfor.home2work.sharehistory

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.animation.AnimationUtils
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseActivity
import it.gruppoinfor.home2work.common.extensions.showToast
import it.gruppoinfor.home2work.di.DipendencyInjector
import kotlinx.android.synthetic.main.activity_shares.*

class ShareHistoryActivity : BaseActivity<ShareHistoryViewModel, ShareHistoryVMFactory>() {

    private var mSharesAdapter: ShareHistoryAdapter? = null

    override fun getVMClass(): Class<ShareHistoryViewModel> {
        return ShareHistoryViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shares)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)

        shares_recycler_view.layoutManager = layoutManager
        shares_recycler_view.layoutAnimation = animation

        mSharesAdapter = ShareHistoryAdapter(imageLoader, { share, pos ->
            // TODO shareclick
        })

        shares_recycler_view.adapter = mSharesAdapter

        observeViewState()

        viewModel.getShareList()
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

    private fun observeViewState() {
        viewModel.errorState.observe(this, Observer {
            it?.let { showToast(it) }
        })

        viewModel.viewState.observe(this, Observer {
            it?.let { handleViewState(it) }
        })

    }

    private fun handleViewState(state: ShareHistoryViewState) {

        status_view.setScreenState(state.screenState)

        state.sharesHistory?.let {
            mSharesAdapter?.setItems(it)
        }

    }


}
