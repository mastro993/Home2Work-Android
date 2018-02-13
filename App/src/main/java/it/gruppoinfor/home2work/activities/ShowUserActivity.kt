package it.gruppoinfor.home2work.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

import butterknife.BindView
import butterknife.ButterKnife
import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.custom.AppBarStateChangeListener
import it.gruppoinfor.home2work.custom.AvatarView
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.User
import it.gruppoinfor.home2workapi.model.UserProfile

class ShowUserActivity : AppCompatActivity() {

    @BindView(R.id.text_name_small)
    internal var textNameSmall: TextView? = null
    @BindView(R.id.toolbar_layout)
    internal var toolbarLayout: LinearLayout? = null
    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null
    @BindView(R.id.avatar_view)
    internal var avatarView: AvatarView? = null
    @BindView(R.id.name_text_view)
    internal var nameTextView: TextView? = null
    @BindView(R.id.job_text_view)
    internal var jobTextView: TextView? = null
    @BindView(R.id.collapsingToolbar)
    internal var collapsingToolbar: CollapsingToolbarLayout? = null
    @BindView(R.id.appBar)
    internal var appBar: AppBarLayout? = null
    @BindView(R.id.swipe_refresh_layout)
    internal var swipeRefreshLayout: SwipeRefreshLayout? = null
    @BindView(R.id.rootView)
    internal var rootView: CoordinatorLayout? = null

    private var mUser: User? = null
    private var mProfile: UserProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_user)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        val intent = intent
        if (intent.hasExtra(EXTRA_USER)) {
            mUser = intent.getSerializableExtra(EXTRA_USER) as User
            initUI()
            refreshData()
        } else {
            Toasty.error(this, getString(R.string.activity_show_user_error)).show()
            finish()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initUI() {
        appBar!!.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: AppBarStateChangeListener.State) {
                when (state) {
                    AppBarStateChangeListener.State.COLLAPSED -> if (toolbarLayout!!.alpha < 1.0f) {
                        toolbarLayout!!.visibility = View.VISIBLE
                        toolbarLayout!!.animate()
                                //.translationY(toolbarLayout.getHeight())
                                .alpha(1.0f)
                                .setListener(null)
                    }
                    AppBarStateChangeListener.State.IDLE -> if (toolbarLayout!!.alpha > 0.0f) {
                        toolbarLayout!!.animate()
                                .translationY(0f)
                                .alpha(0.0f)
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        super.onAnimationEnd(animation)
                                        toolbarLayout!!.visibility = View.GONE
                                    }
                                })
                    }
                }
            }
        })

        swipeRefreshLayout!!.setOnRefreshListener {
            swipeRefreshLayout!!.isRefreshing = true
            refreshData()
        }
        swipeRefreshLayout!!.setColorSchemeResources(R.color.colorAccent)

        avatarView!!.setAvatarURL(mUser!!.avatarURL)
        nameTextView!!.text = mUser!!.toString()
        jobTextView!!.text = mUser!!.company.toString()
        textNameSmall!!.text = mUser!!.toString()

    }

    private fun refreshData() {


        HomeToWorkClient.getInstance().getUserProfile(mUser!!.id, { userProfile ->
            swipeRefreshLayout!!.isRefreshing = false
            mProfile = userProfile
            refreshUI()
        }) {
            Toasty.error(this@ShowUserActivity, "Impossibile ottenere informazioni dell'utente al momento").show()
            swipeRefreshLayout!!.isRefreshing = false
        }

    }

    private fun refreshUI() {
        avatarView!!.setLevel(mProfile!!.exp.level!!)
    }

    companion object {

        val EXTRA_USER = "user"
    }
}
