package it.gruppoinfor.home2work.sharehistory

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseActivity
import it.gruppoinfor.home2work.common.RecyclerViewEndlessScrollListener
import it.gruppoinfor.home2work.common.extensions.hide
import it.gruppoinfor.home2work.common.extensions.show
import it.gruppoinfor.home2work.common.extensions.showToast
import kotlinx.android.synthetic.main.activity_shares.*


class ShareHistoryActivity : BaseActivity<ShareHistoryViewModel, ShareHistoryVMFactory>() {

    private lateinit var mSharesAdapter: ShareHistoryAdapter
    private lateinit var mScrollListenerRecyclerView: RecyclerViewEndlessScrollListener

    private val pageSize = 5

    override fun getVMClass(): Class<ShareHistoryViewModel> {
        return ShareHistoryViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shares)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mSharesAdapter = ShareHistoryAdapter(imageLoader, { share, pos ->
            // TODO shareclick
        })

        mScrollListenerRecyclerView = object : RecyclerViewEndlessScrollListener(pageSize) {
            override fun loadMoreItems(page: Int) {
                viewModel.loadMoreShares(pageSize, page)
                mScrollListenerRecyclerView.isLoading = true

            }
        }

        shares_recycler_view.layoutManager = layoutManager
        shares_recycler_view.adapter = mSharesAdapter
        shares_recycler_view.addOnScrollListener(mScrollListenerRecyclerView)
        shares_recycler_view.isNestedScrollingEnabled = false

        observeViewState()

        viewModel.getShareList(pageSize, 1)
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
            it?.let {

                status_view.setScreenState(it.screenState)

                it.sharesHistory?.let {

                    if (it.isEmpty() || it.size < pageSize) {
                        mScrollListenerRecyclerView.isLastPage = true
                        new_page_loading_view.hide()
                    } else {
                        mScrollListenerRecyclerView.isLastPage = false
                        new_page_loading_view.show()
                    }

                    mSharesAdapter.setItems(it)
                }

            }
        })

        viewModel.loadingState.observe(this, Observer {
            it?.let {

                if (it)
                    new_page_loading_view.show()
                else
                    new_page_loading_view.hide()

            }
        })

        viewModel.newSharePage.observe(this, Observer {
            it?.let {
                if (it.isEmpty() || it.size < pageSize) {
                    mScrollListenerRecyclerView.isLastPage = true
                    new_page_loading_view.hide()
                }
                mSharesAdapter.addItems(it)
                mScrollListenerRecyclerView.isLoading = false
            }
        })

    }


}
