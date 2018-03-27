package it.gruppoinfor.home2work.sharehistory

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.animation.AnimationUtils
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.ImageLoader
import it.gruppoinfor.home2work.common.extensions.showToast
import it.gruppoinfor.home2work.di.DipendencyInjector
import kotlinx.android.synthetic.main.activity_shares.*
import javax.inject.Inject

class ShareHistoryActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ShareHistoryVMFactory
    @Inject
    lateinit var imageLoader: ImageLoader

    private lateinit var viewModel: ShareHistoryViewModel

    private var mSharesAdapter: ShareHistoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shares)

        DipendencyInjector.createShareHistoryComponent().inject(this)
        viewModel = ViewModelProvider(this, factory).get(ShareHistoryViewModel::class.java)

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

    override fun onDestroy() {
        super.onDestroy()
        DipendencyInjector.releaseShareHistoryComponent()
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
