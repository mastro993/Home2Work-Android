package it.gruppoinfor.home2work.leaderboards

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseActivity
import it.gruppoinfor.home2work.common.NestedScrollViewEndlessScrollListener
import it.gruppoinfor.home2work.common.extensions.remove
import it.gruppoinfor.home2work.common.extensions.show
import it.gruppoinfor.home2work.entities.Leaderboard
import it.gruppoinfor.home2work.user.UserActivityLauncher
import kotlinx.android.synthetic.main.activity_leaderboards.*
import org.jetbrains.anko.intentFor


class LeaderboardsActivity : BaseActivity<LeaderboardsViewModel, LeaderboardsVMFactory>() {


    val range = Leaderboard.Range.Global
    var type: Leaderboard.Type? = null
    var timespan: Leaderboard.TimeSpan? = null


    private val pageSize = 20

    private lateinit var mLeaderboardsAdapter: LeaderboardsAdapter

    private val mNestedScrollViewEndlessScrollListener = object : NestedScrollViewEndlessScrollListener() {
        override fun loadMoreItems(page: Int) {

            viewModel.getLeaderboardNewPage(
                    type = type,
                    range = range,
                    timespan = timespan,
                    pageSize = pageSize,
                    page = page
            )

            isLoading = true

        }
    }

    private val mOnRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        viewModel.refreshLeaderboard(
                type = type,
                range = range,
                timespan = timespan,
                pageSize = pageSize,
                page = 1
        )
    }

    override fun getVMClass(): Class<LeaderboardsViewModel> {
        return LeaderboardsViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboards)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh_layout.setOnRefreshListener(mOnRefreshListener)

        leaderboards_recycler_view.isNestedScrollingEnabled = false

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)

        leaderboards_recycler_view.layoutManager = layoutManager
        leaderboards_recycler_view.layoutAnimation = animation

        mLeaderboardsAdapter = LeaderboardsAdapter(
                imageLoader = imageLoader,
                onClick = { userRanking, _ ->

                    UserActivityLauncher(
                            userId = userRanking.userId,
                            userName = userRanking.userName,
                            userCompanyId = userRanking.companyId,
                            userCompanyName = userRanking.companyName,
                            userAvatarUrl = userRanking.avatarUrl
                    ).launch(this)

                },
                userId = localUserData.user!!.id)

        leaderboards_recycler_view.adapter = mLeaderboardsAdapter

        spinner_leaderboard_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                type = Leaderboard.Type.from(position)
                mLeaderboardsAdapter.type = Leaderboard.Type.from(position)

                viewModel.getLeaderboard(
                        type = type,
                        range = range,
                        companyId = localUserData.user?.company?.id,
                        timespan = timespan,
                        pageSize = pageSize,
                        page = 1
                )
            }
        }
        spinner_leaderboard_timespan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                timespan = Leaderboard.TimeSpan.from(position)

                viewModel.getLeaderboard(
                        type = type,
                        range = range,
                        companyId = localUserData.user?.company?.id,
                        timespan = timespan,
                        pageSize = pageSize,
                        page = 1
                )
            }
        }

        nested_scroll_view.setOnScrollChangeListener(mNestedScrollViewEndlessScrollListener)

        observeViewState()

        viewModel.getLeaderboard(
                type = type,
                range = range,
                timespan = timespan,
                pageSize = pageSize,
                page = 1
        )

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
        viewModel.viewState.observe(this, Observer {

            it?.let {

                status_view.setScreenState(it.screenState)


                with(it.leaderboard) {
                    mLeaderboardsAdapter.setItems(this)

                    if (isEmpty() || size < pageSize) {
                        mNestedScrollViewEndlessScrollListener.isLastPage = true
                        new_page_loading_view.remove()
                    } else {
                        mNestedScrollViewEndlessScrollListener.isLastPage = false
                        new_page_loading_view.show()
                    }
                }


                swipe_refresh_layout.isRefreshing = it.isRefreshing

            }

        })

        viewModel.loadingState.observe(this, Observer {
            it?.let {
                if (it)
                    new_page_loading_view.show()
                else
                    new_page_loading_view.remove()
            }
        })

        viewModel.newLeaderboardPage.observe(this, Observer {
            it?.let {
                if (it.isEmpty() || it.size < pageSize) {
                    mNestedScrollViewEndlessScrollListener.isLastPage = true
                    new_page_loading_view.remove()
                }
                mLeaderboardsAdapter.addItems(it)
                mNestedScrollViewEndlessScrollListener.isLoading = false
            } ?: let {
                mNestedScrollViewEndlessScrollListener.isLoading = false
                mNestedScrollViewEndlessScrollListener.isLastPage = true
            }
        })
    }
}
