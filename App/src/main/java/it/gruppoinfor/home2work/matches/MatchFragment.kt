package it.gruppoinfor.home2work.matches

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import it.gruppoinfor.home2work.Constants
import it.gruppoinfor.home2work.MainActivity
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.chat.ChatActivity
import it.gruppoinfor.home2work.user.UserActivity
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.match.Match
import kotlinx.android.synthetic.main.fragment_match.*
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop


class MatchFragment : Fragment() {

    private var matchesAdapter: MatchAdapter? = null
    private var matchList: ArrayList<Match> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_match, container, false)
        setHasOptionsMenu(true)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        getMatches()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_match, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initUI() {

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = true
            refreshMatches()
        }

        matches_recycler_view.isNestedScrollingEnabled = false

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)

        matches_recycler_view.layoutManager = layoutManager
        matches_recycler_view.layoutAnimation = animation

        matchesAdapter = MatchAdapter(context!!, matchList)

        matches_recycler_view.adapter = matchesAdapter

        matchesAdapter?.getClickPosition()?.subscribe { position ->

            val match = matchList[position]
            if (match.score == 0) {
                startActivity(context!!.intentFor<UserActivity>(Constants.EXTRA_USER to match.host).singleTop())
            } else {
                if (match.isNew) {
                    match.isNew = false
                    matchesAdapter?.notifyItemChanged(position)
                    refreshBadgeCounter()
                }
                startActivity(context!!.intentFor<MatchInfoActivity>(Constants.EXTRA_MATCH to matchList[position]))
            }

        }

        matchesAdapter?.getLongClickPosition()?.subscribe { position ->
            val dialog = BottomSheetDialog(context!!)
            val sheetView = layoutInflater.inflate(R.layout.dialog_match_options, null)

            dialog.setContentView(sheetView)
            dialog.show()

            sheetView.find<TextView>(R.id.match_dialog_show_profile).setOnClickListener {
                dialog.dismiss()
                val matchedUser = matchList[position].host
                startActivity(context!!.intentFor<UserActivity>(Constants.EXTRA_USER to matchedUser).singleTop())
            }
            sheetView.find<TextView>(R.id.match_dialog_send_message).setOnClickListener {
                dialog.dismiss()
                val matchedUser = matchList[position].host
                startActivity(context!!.intentFor<ChatActivity>(ChatActivity.EXTRA_NEW_CHAT to matchedUser).singleTop())
            }
            sheetView.find<TextView>(R.id.match_dialog_hide).setOnClickListener {
                dialog.dismiss()
                showHideMatchDialog(position)
            }
        }

    }

    private fun getMatches() {

        status_view.loading()

        HomeToWorkClient.getMatchList(OnSuccessListener { matches ->

            matchList.clear()
            matchList.addAll(matches)
            matchesAdapter?.notifyDataSetChanged()

            refreshUI()
            refreshBadgeCounter()

        }, OnFailureListener {

            status_view.error("Impossibile ottenere lista match al momento")
            it.printStackTrace()

        })

    }

    private fun refreshMatches() {

        HomeToWorkClient.getMatchList(OnSuccessListener { matches ->

            matchList.clear()
            matchList.addAll(matches)
            matchesAdapter?.notifyDataSetChanged()

            refreshBadgeCounter()
            refreshUI()

        }, OnFailureListener {

            swipe_refresh_layout.isRefreshing = false
            Toast.makeText(context, "Impossibile aggiornare lista match al momento", Toast.LENGTH_SHORT).show()
            it.printStackTrace()

        })

    }

    private fun refreshUI() {

        when {
            matchList.isEmpty() -> {
                status_view.empty("Non sono ancora disponibili dei match per te.")
            }
            matchList.any { it.score == 0 } -> {
                status_view.done()
                no_matches_view.visibility = View.VISIBLE
            }
            else -> {
                status_view.done()
                no_matches_view.visibility = View.GONE
            }
        }

        swipe_refresh_layout.isRefreshing = false

    }

    fun refreshBadgeCounter() {
        val newMatches = matchList.count { it.isNew }

        (context as MainActivity).setNavigationBadge(MainActivity.MATCHES_TAB, if (newMatches > 0) newMatches.toString() else "")
    }

    private fun showHideMatchDialog(position: Int) {

        val matchItem = matchList[position]

        AlertDialog.Builder(context!!)
                .setTitle(R.string.item_match_dialog_hide_title)
                .setMessage(R.string.item_match_dialog_hide_content)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, { _, _ ->
                    matchItem.hidden = true

                    HomeToWorkClient.editMatch(matchItem, OnSuccessListener {
                        matchList.removeAt(position)
                        matchesAdapter?.remove(position)
                    }, OnFailureListener { Toast.makeText(context!!, R.string.item_match_dialog_hide_error, Toast.LENGTH_SHORT).show() })

                })
                .setNegativeButton(android.R.string.no, null)
                .show()

    }


}

