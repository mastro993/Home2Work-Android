package it.gruppoinfor.home2work.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.activities.*
import it.gruppoinfor.home2work.adapters.MatchAdapter
import it.gruppoinfor.home2work.interfaces.ItemClickCallbacks
import it.gruppoinfor.home2work.utils.Const
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.match.Match
import kotlinx.android.synthetic.main.fragment_match.*
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

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)

        matches_recycler_view.layoutManager = layoutManager
        matches_recycler_view.layoutAnimation = animation

        matchesAdapter = MatchAdapter(context!!, matchList)

        matches_recycler_view.adapter = matchesAdapter

        matchesAdapter?.setItemClickCallbacks(object : ItemClickCallbacks {

            override fun onItemClick(view: View, position: Int) {

                val match = matchList[position]
                if (match.score == 0) {
                    showMatchUserProfile(position)
                } else {
                    if (match.isNew) {
                        match.isNew = false
                        matchesAdapter?.notifyItemChanged(position)
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

        })

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

        (context as MainActivity).setNavigationBadge(Const.MATCHES_TAB, if (newMatches > 0) newMatches.toString() else "")
    }

    private fun showMatchUserProfile(position: Int) {

        val matchedUser = matchList[position].host
        startActivity(context!!.intentFor<ShowUserActivity>(Const.EXTRA_USER to matchedUser).singleTop())

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

                    HomeToWorkClient.editMatch(matchItem, OnSuccessListener {
                        matchList.removeAt(position)
                        matchesAdapter?.remove(position)
                    }, OnFailureListener { Toast.makeText(context!!, R.string.item_match_dialog_hide_error, Toast.LENGTH_SHORT).show() })


                }
                .build()

        hideDialog.show()

    }


}

