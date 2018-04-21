package it.gruppoinfor.home2work.user

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.view.MenuItem
import android.view.View
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.chat.SingleChatActivityLauncher
import it.gruppoinfor.home2work.common.BaseActivity
import it.gruppoinfor.home2work.common.PicassoCircleTransform
import it.gruppoinfor.home2work.common.extensions.*
import it.gruppoinfor.home2work.common.views.AppBarStateChangeListener
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.view_profile_activity_details.*
import kotlinx.android.synthetic.main.view_profile_exp_details.*
import kotlinx.android.synthetic.main.view_profile_footer.*
import kotlinx.android.synthetic.main.view_profile_header.*
import kotlinx.android.synthetic.main.view_profile_shares_details.*
import kotlinx.android.synthetic.main.view_profile_status.*
import java.util.*

class UserActivity : BaseActivity<UserViewModel, UserVMFactory>() {

    override fun getVMClass(): Class<UserViewModel> {
        return UserViewModel::class.java
    }


    private val args by lazy {
        UserActivityLauncher.deserializeFrom(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        initUI()

        observeViewState()

        viewModel.getProfile(args.userId)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initUI() {

        appBar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {

                when (state) {
                    State.COLLAPSED -> if (toolbar_layout.alpha < 1.0f) {

                        toolbar_layout.visibility = View.VISIBLE
                        toolbar_layout.animate()
                                //.translationY(toolbarLayout.getHeight())
                                .alpha(1.0f)
                                .setListener(null)

                    }
                    State.IDLE -> if (toolbar_layout.alpha > 0.0f) {

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
                    State.EXPANDED -> {

                    }
                }
            }
        })

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh_layout.setOnRefreshListener {
            viewModel.refreshProfile(args.userId)
        }

        button_send_message.setOnClickListener {
            SingleChatActivityLauncher(
                    chatId = 0L,
                    recipientId = args.userId,
                    recipientName = args.userName
            ).launch(this)
        }

        text_name_small.text = args.userName
        avatar_view.setAvatarURL(args.userAvatarUrl)
        name_text_view.text = args.userName
        job_text_view.text = args.userCompanyName

        chart_activity.setUp()
        chart_shares.setUp()

    }

    private fun observeViewState() {
        viewModel.viewState.observe(this, Observer {
            it?.let { handleViewState(it) }
        })

        viewModel.errorState.observe(this, Observer {
            it?.let { showToast(it) }
        })
    }

    fun handleViewState(state: UserViewState) {
        status_view.setScreenState(state.screenState)
        swipe_refresh_layout.isRefreshing = state.isRefreshing

        state.profile?.let {
            profile_container.show()

            avatar_view.setLevel(it.exp.level)

            it.status?.let {
                container_profile_status.show()

                imageLoader.load(
                        url = args.userAvatarUrl,
                        imageView = image_status_avatar,
                        placeholder = R.drawable.ic_avatar_placeholder,
                        transformation = PicassoCircleTransform())

                text_status.text = it.status
                text_status_date.text = it.date.formatElapsed()
            } ?: container_profile_status.remove()

            progress_exp.animateTo(it.exp.progress)
            text_exp_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_exp_value), it.exp.amount)
            text_current_lvl_exp.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_current_lvl_exp_value), it.exp.currentLvlKarma)
            text_next_lvl_exp.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_next_lvl_exp_value), it.exp.nextLvlKarma)
            text_month_exp_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_exp_month), it.exp.monthKarma)

            text_shared_distance_value.text = String.format(Locale.ITALY, "%.2f km", it.stats.sharedDistance.div(1000f))
            text_month_shared_distance_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), it.stats.monthSharedDistance.div(1000f))
            text_month_shared_distance_avg_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), it.stats.monthSharedDistanceAvg.div(1000f))

            if (it.stats.sharedDistance > 0) {
                no_activity_chart_data_view.hide()
                chart_activity.show()
                chart_activity.setData(it.activity, it.stats.monthSharedDistanceAvg.div(1000f))
            } else {
                no_activity_chart_data_view.show()
                chart_activity.remove()
            }

            text_month_shares.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month), Date().format("MMMM").capitalize())
            text_month_shares_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_value), it.stats.monthShares)
            text_month_shares_avg_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_avg_value), it.stats.monthlySharesAvg)
            text_month_shares_record_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_shares_month_value), it.stats.bestMonthShares)
            text_longest_share_value.text = String.format(Locale.ITALY, getString(R.string.fragment_profile_card_activity_this_month_value), it.stats.longestShare.div(1000f))

            if (it.stats.totalShares > 0) {
                no_share_chart_data_view.hide()
                chart_shares.show()
                chart_shares.setData(it.stats)
            } else {
                no_share_chart_data_view.show()
                chart_shares.remove()
            }

            text_regdate.text = it.regdate.format("dd MMMM yyyy")

        } ?: profile_container.remove()
    }
}
