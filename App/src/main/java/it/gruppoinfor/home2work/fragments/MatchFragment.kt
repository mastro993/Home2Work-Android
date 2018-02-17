package it.gruppoinfor.home2work.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.MainActivity
import it.gruppoinfor.home2work.activities.MatchActivity
import it.gruppoinfor.home2work.activities.SettingsActivity
import it.gruppoinfor.home2work.activities.ShowUserActivity
import it.gruppoinfor.home2work.adapters.MatchAdapter
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Match
import kotlinx.android.synthetic.main.fragment_match.*

class MatchFragment : Fragment(), ItemClickCallbacks {

    private lateinit var matchesAdapter: MatchAdapter

    private lateinit var matchList: ArrayList<Match>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_match, container, false)
        setHasOptionsMenu(true)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = true
            refreshData()
        }

    }

    override fun onResume() {
        super.onResume()

        refreshData()

    }

    override fun onItemClick(view: View, position: Int) {

        val match = matchList[position]
        if (match.score == 0) {
            showMatchUserProfile(position)
        } else {
            if (match.isNew) {
                match.isNew = false
                matchesAdapter.notifyItemChanged(position)
                refreshBadgeCounter()
            }
            val matchIntent = Intent(context, MatchActivity::class.java)
            matchIntent.putExtra(Const.EXTRA_MATCH, matchList[position])
            startActivity(matchIntent)
        }

    }

    override fun onLongItemClick(view: View, position: Int): Boolean {

        val options = arrayOf("Mostra profilo utente", "Nascondi")

        MaterialDialog.Builder(context!!)
                .items(*options)
                .itemsCallback { _, _, p, _ ->
                    when (p) {
                        0 -> showMatchUserProfile(position)
                        1 -> startActivity(Intent(activity, SettingsActivity::class.java))
                        2 -> showHideMatchDialog(position)
                    }
                }
                .show()


        return true
    }

    private fun refreshData() {

        HomeToWorkClient.getInstance().getUserMatches(OnSuccessListener { matches ->
            matchList = matches
            refreshBadgeCounter()
            refreshList()
            swipe_refresh_layout.isRefreshing = false
        }, OnFailureListener {
            swipe_refresh_layout.isRefreshing = false
        })

    }

    private fun refreshList() {

        val noMatches = matchList.any { it.score == 0 }

        if (noMatches)
            no_matches_view.visibility = View.VISIBLE
        else
            no_matches_view.visibility = View.GONE

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)

        matches_recycler_view.layoutManager = layoutManager
        matches_recycler_view.layoutAnimation = animation

        matchesAdapter = MatchAdapter(context!!, matchList)
        matchesAdapter.notifyDataSetChanged()
        matches_recycler_view.adapter = matchesAdapter
        matchesAdapter.setItemClickCallbacks(this)

    }

    protected fun refreshBadgeCounter() {
        val newMatches = matchList.count { it.isNew }

        (context as MainActivity).setBadge(Const.MATCHES_TAB, if (newMatches > 0) newMatches.toString() else "")
    }

    private fun showMatchUserProfile(position: Int) {

        val userIntent = Intent(activity, ShowUserActivity::class.java)
        val matchedUser = matchList[position].host
        userIntent.putExtra(Const.EXTRA_USER, matchedUser)
        startActivity(userIntent)

    }

    private fun showHideMatchDialog(position: Int) {

        val matchItem = matchList[position]
        val hideDialog = MaterialDialog.Builder(context!!)
                .title(R.string.item_match_dialog_hide_title)
                .content(R.string.item_match_dialog_hide_content)
                .positiveText(R.string.item_match_dialog_hide_confirm)
                .negativeText(R.string.item_match_dialog_hide_cancel)
                .onPositive { _, _ ->

                    matchItem.hidden = true

                    HomeToWorkClient.getInstance().editMatch(matchItem, OnSuccessListener {
                        matchList.removeAt(position)
                        matchesAdapter.remove(position)
                    }, OnFailureListener { Toasty.success(context!!, context!!.getString(R.string.item_match_dialog_hide_error)).show() })


                }
                .build()

        hideDialog.show()

    }


}

