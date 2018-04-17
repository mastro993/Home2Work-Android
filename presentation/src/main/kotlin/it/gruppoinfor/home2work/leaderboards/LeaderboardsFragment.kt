package it.gruppoinfor.home2work.leaderboards


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseFragment
import it.gruppoinfor.home2work.common.EndlessScrollListener
import it.gruppoinfor.home2work.common.extensions.hide
import it.gruppoinfor.home2work.common.extensions.show
import it.gruppoinfor.home2work.entities.Leaderboard
import it.gruppoinfor.home2work.user.UserActivityLauncher
import kotlinx.android.synthetic.main.fragment_leaderboards.*


class LeaderboardsFragment : BaseFragment<LeaderboardsViewModel, LeaderboardsVMFactory>() {

    private lateinit var mLeaderboardsAdapter: LeaderboardsAdapter
    private lateinit var mScrollListener: EndlessScrollListener

    var type: Leaderboard.Type? = null
    var range: Leaderboard.Range? = null
    var timespan: Leaderboard.TimeSpan? = null

    private val pageSize = 20

    override fun getVMClass(): Class<LeaderboardsViewModel> {
        return LeaderboardsViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel.getLeaderboard(
                type = type,
                range = range,
                timespan = timespan,
                pageSize = pageSize,
                page = 1
        )

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_leaderboards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh_layout.setOnRefreshListener {

            viewModel.refreshLeaderboard(
                    type = type,
                    range = range,
                    timespan = timespan,
                    pageSize = pageSize,
                    page = 1
            )

        }

        leaderboards_recycler_view.isNestedScrollingEnabled = false

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)

        leaderboards_recycler_view.layoutManager = layoutManager
        leaderboards_recycler_view.layoutAnimation = animation

        mLeaderboardsAdapter = LeaderboardsAdapter(
                imageLoader = imageLoader,
                onClick = { userRanking, position ->

                    UserActivityLauncher(
                            userId = userRanking.userId,
                            userName = userRanking.userName,
                            userCompanyId = userRanking.companyId,
                            userCompanyName = userRanking.companyName,
                            userAvatarUrl = userRanking.avatarUrl
                    ).launch(context!!)

                },
                userId = localUserData.user!!.id)

        mScrollListener = object : EndlessScrollListener(pageSize) {
            override fun loadMoreItems(page: Int) {

                viewModel.getLeaderboardNewPage(
                        type = type,
                        range = range,
                        timespan = timespan,
                        pageSize = pageSize,
                        page = page
                )

                mScrollListener.isLoading = true


            }
        }

        leaderboards_recycler_view.addOnScrollListener(mScrollListener)
        leaderboards_recycler_view.adapter = mLeaderboardsAdapter


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.viewState.observe(this, Observer {

            it?.let {

                status_view.setScreenState(it.screenState)


                with(it.leaderboard) {
                    mLeaderboardsAdapter.setItems(this)

                    if (isEmpty() || size < pageSize) {
                        mScrollListener.isLastPage = true
                        new_page_loading_view.hide()
                    } else {
                        mScrollListener.isLastPage = false
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
                    new_page_loading_view.hide()
            }
        })

        viewModel.newLeaderboardPage.observe(this, Observer {
            it?.let {
                if (it.isEmpty() || it.size < pageSize) {
                    mScrollListener.isLastPage = true
                    new_page_loading_view.hide()
                }
                mLeaderboardsAdapter.addItems(it)
                mScrollListener.isLoading = false
            } ?: let {
                mScrollListener.isLoading = false
                mScrollListener.isLastPage = true
            }
        })


    }


}
