package it.gruppoinfor.home2work.match

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.TextView
import it.gruppoinfor.home2work.MainActivity
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.chat.ChatActivityLancher
import it.gruppoinfor.home2work.common.ImageLoader
import it.gruppoinfor.home2work.common.extensions.getScore
import it.gruppoinfor.home2work.common.extensions.hide
import it.gruppoinfor.home2work.common.extensions.show
import it.gruppoinfor.home2work.common.extensions.showToast
import it.gruppoinfor.home2work.common.views.ScreenStateView
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.entities.Match
import it.gruppoinfor.home2work.user.UserActivityLancher
import kotlinx.android.synthetic.main.fragment_match.*
import org.jetbrains.anko.find
import javax.inject.Inject


class MatchesFragment : Fragment() {


    @Inject
    lateinit var factory: MatchVMFactory
    @Inject
    lateinit var imageLoader: ImageLoader

    private lateinit var viewModel: MatchViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var statusView: ScreenStateView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mMatchesAdapter: MatchesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DipendencyInjector.createMatchComponent().inject(this)
        viewModel = ViewModelProvider(this, factory).get(MatchViewModel::class.java)

        viewModel.getMatchList()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.errorState.observe(this, Observer {
            it?.let { showToast(it) }
        })

        viewModel.viewState.observe(this, Observer {
            it?.let { handleViewState(it) }
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_match, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        swipeRefreshLayout = swipe_refresh_layout
        recyclerView = matches_recycler_view
        statusView = status_view

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshMatchList()
        }

        recyclerView.isNestedScrollingEnabled = false

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)

        recyclerView.layoutManager = layoutManager
        recyclerView.layoutAnimation = animation

        mMatchesAdapter = MatchesAdapter(
                imageLoader,
                onMatchClick = { match, position -> onMatchClick(match, position) },
                onMatchLongClick = { match, position -> onMatchLongClick(match, position) })

        recyclerView.adapter = mMatchesAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_match, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_match_sort -> {
                // TODO ordine match
            }
            R.id.action_match_settings -> {
                // TODO preferenze matches
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        DipendencyInjector.releaseMatchComponent()
    }

    private fun handleViewState(state: MatchViewState) {

        statusView.setScreenState(state.screenState)
        swipeRefreshLayout.isRefreshing = state.isRefreshing

        if (state.isLightMatches) {
            light_matches_view.show()
        } else {
            light_matches_view.hide()
        }

        state.matches?.let {
            mMatchesAdapter.setItems(it)

        }

        (activity as MainActivity).setNavigationBadge(MainActivity.MATCHES_TAB, state.badgeCount)

    }

    private fun onMatchClick(match: Match, position: Int) {
        if (match.getScore() != null) {
            viewModel.setMatchAsViewed(match)
            val dialog = MatchInfoDialog(context!!, imageLoader, match)
            dialog.show()
        } else {

            val user = match.host

            UserActivityLancher(
                    userId = user.id,
                    userName = user.fullName,
                    userAvatarUrl = user.avatarUrl,
                    userCompanyId = user.company!!.id,
                    userCompanyName = user.company!!.formattedName
            ).launch(context!!)

        }
    }

    private fun onMatchLongClick(match: Match, position: Int): Boolean {

        val dialog = BottomSheetDialog(context!!)
        val sheetView = layoutInflater.inflate(R.layout.dialog_match_options, null, false)

        dialog.setContentView(sheetView)
        dialog.show()

        sheetView.find<TextView>(R.id.match_dialog_show_profile).setOnClickListener {
            dialog.dismiss()

            val user = match.host

            UserActivityLancher(
                    userId = user.id,
                    userName = user.fullName,
                    userAvatarUrl = user.avatarUrl,
                    userCompanyId = user.company!!.id,
                    userCompanyName = user.company!!.formattedName
            ).launch(context!!)

        }
        sheetView.find<TextView>(R.id.match_dialog_send_message).setOnClickListener {
            dialog.dismiss()

            val recipientId = match.host.id
            val recipientName = match.host.name

            ChatActivityLancher(
                    chatId = 0L,
                    recipientId = recipientId,
                    recipientName = recipientName
            ).launch(context!!)

        }
        sheetView.find<TextView>(R.id.match_dialog_hide).setOnClickListener {
            dialog.dismiss()

            AlertDialog.Builder(context!!)
                    .setTitle(R.string.item_match_dialog_hide_title)
                    .setMessage(R.string.item_match_dialog_hide_content)
                    .setPositiveButton(android.R.string.yes, { _, _ ->

                        viewModel.hideMatch(match)
                        mMatchesAdapter.hide(position)
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show()

        }

        if (match.getScore() == null) {
            sheetView.find<TextView>(R.id.match_dialog_hide).visibility = View.GONE
        }

        return true

    }


}

