package it.gruppoinfor.home2work.match

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.TextView
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.chat.ChatActivityArgs
import it.gruppoinfor.home2work.extensions.showToast
import it.gruppoinfor.home2work.main.MainActivity
import it.gruppoinfor.home2work.user.UserActivityArgs
import kotlinx.android.synthetic.main.fragment_match.*
import org.jetbrains.anko.find


class MatchesFragment : Fragment(), MatchesView {


    private var mMatchesPresenter: MatchesPresenter = MatchesPresenterImpl(this)
    private var mMatchesAdapter: MatchesAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_match, container, false)
        setHasOptionsMenu(true)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh_layout.setOnRefreshListener { mMatchesPresenter.onRefresh() }

        matches_recycler_view.isNestedScrollingEnabled = false

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)

        matches_recycler_view.layoutManager = layoutManager
        matches_recycler_view.layoutAnimation = animation

        mMatchesAdapter = MatchesAdapter(context!!, this)

        matches_recycler_view.adapter = mMatchesAdapter

        mMatchesPresenter.onViewCreated()

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

    override fun onPause() {
        super.onPause()

        mMatchesPresenter.onPause()
    }

    override fun onLoading() {
        status_view.loading()
    }

    override fun setMatches(list: ArrayList<Match>) {

        if (list.any { match -> match.getScore() == 0 }) {
            status_view.done()
            no_matches_view.visibility = View.VISIBLE
        } else {
            status_view.done()
            no_matches_view.visibility = View.GONE
        }

        list.sortedByDescending { it.getScore() }

        mMatchesAdapter?.setItems(list)
        mMatchesAdapter?.notifyDataSetChanged()

    }

    override fun onLoadingError(errorMessage: String) {
        status_view.error(errorMessage)
    }

    override fun onRefresh() {
        swipe_refresh_layout.isRefreshing = true
    }

    override fun onRefreshDone() {
        swipe_refresh_layout.isRefreshing = false
    }

    override fun showErrorMessage(errorMessage: String) {
        showToast(errorMessage)
    }

    override fun onEmptyList() {
        status_view.empty("Non sono ancora disponibili match per te.")
        no_matches_view.visibility = View.GONE
    }

    override fun onBadgeRefresh(badge: String) {
        (activity as MainActivity).setNavigationBadge(MainActivity.MATCHES_TAB, badge)
    }

    override fun onMatchHidden(position: Int) {
        mMatchesAdapter?.remove(position)
    }

    override fun onMatchClick(position: Int, match: Match) {

        if (match.getScore() == 0) {

            val user = match.host!!

            UserActivityArgs(
                    userId = user.id,
                    userName = user.toString(),
                    userAvatarUrl = user.avatarURL,
                    userCompanyId = user.company.id,
                    userCompanyName = user.company.name
            ).launch(context!!)


        } else {

            mMatchesPresenter.setMatchAsViewed(match)

            //MatchInfoActivityArgs(match).launch(context!!)

            /*val dialog = MatchInfoFragmentDialog()
            val ft = fragmentManager?.beginTransaction()
            dialog.show(ft, "FullScreenDialog")*/

            val dialog = MatchInfoDialog(context!!, match)
            dialog.show()


        }

    }

    override fun onMatchLongClick(position: Int, match: Match) {

        val dialog = BottomSheetDialog(context!!)
        val sheetView = layoutInflater.inflate(R.layout.dialog_match_options, null)

        dialog.setContentView(sheetView)
        dialog.show()

        sheetView.find<TextView>(R.id.match_dialog_show_profile).setOnClickListener {
            dialog.dismiss()

            val user = match.host!!

            UserActivityArgs(
                    userId = user.id,
                    userName = user.toString(),
                    userAvatarUrl = user.avatarURL,
                    userCompanyId = user.company.id,
                    userCompanyName = user.company.name
            ).launch(context!!)

        }
        sheetView.find<TextView>(R.id.match_dialog_send_message).setOnClickListener {
            dialog.dismiss()

            val recipientId = match.host!!.id
            val recipientName = match.host!!.name

            ChatActivityArgs(
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
                        mMatchesPresenter.hideMatch(match)
                        mMatchesAdapter?.remove(position)
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show()

        }

        if (match.getScore() == 0) {
            sheetView.find<TextView>(R.id.match_dialog_hide).visibility = View.GONE
        }

    }


}

