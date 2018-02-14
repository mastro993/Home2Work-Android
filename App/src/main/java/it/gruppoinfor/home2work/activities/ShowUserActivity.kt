package it.gruppoinfor.home2work.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import es.dmoral.toasty.Toasty
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.custom.AppBarStateChangeListener
import it.gruppoinfor.home2work.user.Const
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.User
import it.gruppoinfor.home2workapi.model.UserProfile
import kotlinx.android.synthetic.main.activity_show_user.*

class ShowUserActivity : AppCompatActivity() {

    private lateinit var mUser: User
    private var mProfile: UserProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_user)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (intent.hasExtra(Const.EXTRA_USER)) {
            mUser = intent.getSerializableExtra(Const.EXTRA_USER) as User
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
                    AppBarStateChangeListener.State.COLLAPSED -> if (toolbar_layout.alpha < 1.0f) {
                        toolbar_layout.visibility = View.VISIBLE
                        toolbar_layout.animate()
                                //.translationY(toolbarLayout.getHeight())
                                .alpha(1.0f)
                                .setListener(null)
                    }
                    AppBarStateChangeListener.State.IDLE -> if (toolbar_layout.alpha > 0.0f) {
                        toolbar_layout.animate()
                                .translationY(0f)
                                .alpha(0.0f)
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        super.onAnimationEnd(animation)
                                        toolbar_layout.visibility = View.GONE
                                    }
                                })
                    }
                    else -> {}
                }
            }
        })

        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = true
            refreshData()
        }
        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)

        avatar_view.setAvatarURL(mUser.avatarURL)
        name_text_view.text = mUser.toString()
        job_text_view.text = mUser.company.toString()
        text_name_small.text = mUser.toString()

    }

    private fun refreshData() {
        HomeToWorkClient.getInstance().getUserProfile(mUser.id, { userProfile ->
            swipe_refresh_layout.isRefreshing = false
            mProfile = userProfile
            refreshUI()
        }) {
            Toasty.error(this@ShowUserActivity, "Impossibile ottenere informazioni dell'utente al momento").show()
            swipe_refresh_layout.isRefreshing = false
        }
    }

    private fun refreshUI() {
        avatar_view.setLevel(mProfile!!.exp.level!!)
    }

}
