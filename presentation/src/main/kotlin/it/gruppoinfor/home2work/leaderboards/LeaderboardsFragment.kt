package it.gruppoinfor.home2work.leaderboards


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseFragment
import it.gruppoinfor.home2work.common.NestedScrollViewEndlessScrollListener
import it.gruppoinfor.home2work.common.extensions.remove
import it.gruppoinfor.home2work.common.extensions.show
import it.gruppoinfor.home2work.entities.Leaderboard
import it.gruppoinfor.home2work.user.UserActivityLauncher
import kotlinx.android.synthetic.main.fragment_leaderboards.*


class LeaderboardsFragment : BaseFragment<LeaderboardsViewModel, LeaderboardsVMFactory>() {

    private lateinit var mLeaderboardsAdapter: LeaderboardsAdapter

    private val mNestedScrollViewEndlessScrollListener = object : NestedScrollViewEndlessScrollListener() {
        override fun loadMoreItems(page: Int) {

            viewModel.getLeaderboardNewPage(
                    type = type,
                    range = range,
                    companyId = localUserData.user?.company?.id,
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
                companyId = localUserData.user?.company?.id,
                timespan = timespan,
                pageSize = pageSize,
                page = 1
        )
    }


    val range = Leaderboard.Range.Company
    var type: Leaderboard.Type? = null
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
                companyId = localUserData.user?.company?.id,
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
        swipe_refresh_layout.setOnRefreshListener(mOnRefreshListener)

        leaderboards_recycler_view.isNestedScrollingEnabled = false

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)

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
                    ).launch(context!!)

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

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
