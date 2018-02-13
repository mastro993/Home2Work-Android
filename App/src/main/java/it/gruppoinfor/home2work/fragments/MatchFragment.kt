package it.gruppoinfor.home2work.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView

import com.afollestad.materialdialogs.MaterialDialog

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.MainActivity
import it.gruppoinfor.home2work.activities.MatchActivity
import it.gruppoinfor.home2work.activities.SettingsActivity
import it.gruppoinfor.home2work.activities.ShowUserActivity
import it.gruppoinfor.home2work.adapters.MatchAdapter
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.Match
import it.gruppoinfor.home2workapi.model.User

class MatchFragment : Fragment(), ItemClickCallbacks {

    @BindView(R.id.matches_recycler_view)
    internal var matchesRecyclerView: RecyclerView? = null
    @BindView(R.id.swipe_refresh_layout)
    internal var swipeRefreshLayout: SwipeRefreshLayout? = null
    @BindView(R.id.no_matches_view)
    internal var noMatchesView: TextView? = null
    private var mUnbinder: Unbinder? = null
    private var matchesAdapter: MatchAdapter? = null
    private var mContext: Context? = null

    private var matchList: MutableList<Match> = ArrayList()

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_match, container, false)
        mUnbinder = ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)
        swipeRefreshLayout!!.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout!!.setOnRefreshListener {
            swipeRefreshLayout!!.isRefreshing = true
            refreshData()
        }
        return rootView
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
            if (match.isNew!!) {
                match.isNew = false
                matchesAdapter!!.notifyItemChanged(position)
                refreshBadgeCounter()
            }
            val matchIntent = Intent(context, MatchActivity::class.java)
            matchIntent.putExtra("match", matchList[position])
            startActivity(matchIntent)
        }
    }

    override fun onLongItemClick(view: View, position: Int): Boolean {

        val options = arrayOf("Mostra profilo utente", "Nascondi")

        MaterialDialog.Builder(mContext!!)
                .items(*options)
                .itemsCallback { dialog, itemView, p, text ->
                    when (p) {
                        0 -> showMatchUserProfile(position)
                        1 -> startActivity(Intent(activity, SettingsActivity::class.java))
                        2 -> showHideMatchDialog(position)
                    }
                }
                .show()


        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder!!.unbind()
    }

    private fun refreshData() {
        HomeToWorkClient.getInstance().getUserMatches({ matches ->
            matchList = matches
            refreshBadgeCounter()
            refreshList()
            swipeRefreshLayout!!.isRefreshing = false
        }) { e ->
            //Toasty.error(activity, "Impossibile ottenere i match").show();
            swipeRefreshLayout!!.isRefreshing = false
        }
    }

    private fun refreshList() {

        var noMatches = false
        for (match in matchList) {
            if (match.score == 0) noMatches = true
        }

        if (noMatches)
            noMatchesView!!.visibility = View.VISIBLE
        else
            noMatchesView!!.visibility = View.GONE

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)

        matchesRecyclerView!!.layoutManager = layoutManager
        matchesRecyclerView!!.layoutAnimation = animation

        matchesAdapter = MatchAdapter(activity, matchList)
        matchesAdapter!!.notifyDataSetChanged()
        matchesRecyclerView!!.adapter = matchesAdapter
        matchesAdapter!!.setItemClickCallbacks(this)
    }

    protected fun refreshBadgeCounter() {
        var newMatches: Int? = 0

        for (match in matchList)
            if (match.isNew!!) newMatches++

        (mContext as MainActivity).setBadge(1, if (newMatches > 0) newMatches!!.toString() else "")
    }

    private fun showMatchUserProfile(position: Int) {
        val userIntent = Intent(activity, ShowUserActivity::class.java)
        val matchedUser = matchList[position].host
        userIntent.putExtra("user", matchedUser)
        startActivity(userIntent)
    }

    private fun showHideMatchDialog(position: Int) {
        val matchItem = matchList[position]
        val hideDialog = MaterialDialog.Builder(mContext!!)
                .title(R.string.item_match_dialog_hide_title)
                .content(R.string.item_match_dialog_hide_content)
                .positiveText(R.string.item_match_dialog_hide_confirm)
                .negativeText(R.string.item_match_dialog_hide_cancel)
                .onPositive { dialog, which ->

                    matchItem.hidden = true

                    HomeToWorkClient.getInstance().editMatch(matchItem, { match ->
                        matchList.removeAt(position)
                        matchesAdapter!!.remove(position)
                    }) { e -> Toasty.success(mContext!!, mContext!!.getString(R.string.item_match_dialog_hide_error)).show() }


                }
                .build()

        hideDialog.show()
    }


}// Required empty public constructor
