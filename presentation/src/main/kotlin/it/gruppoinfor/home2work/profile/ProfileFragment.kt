package it.gruppoinfor.home2work.profile

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.TextView
import it.gruppoinfor.home2work.R
import it.gruppoinfor.home2work.common.BaseFragment
import it.gruppoinfor.home2work.common.PicassoCircleTransform
import it.gruppoinfor.home2work.common.extensions.*
import it.gruppoinfor.home2work.common.views.AppBarStateChangeListener
import it.gruppoinfor.home2work.settings.SettingsActivity
import it.gruppoinfor.home2work.sharehistory.ShareHistoryActivity
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.view_profile_activity_details.*
import kotlinx.android.synthetic.main.view_profile_exp_details.*
import kotlinx.android.synthetic.main.view_profile_footer.*
import kotlinx.android.synthetic.main.view_profile_header.*
import kotlinx.android.synthetic.main.view_profile_shares_details.*
import kotlinx.android.synthetic.main.view_profile_status.*
import org.jetbrains.anko.find
import java.util.*
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager


class ProfileFragment : BaseFragment<ProfileViewModel, ProfileVMFactory>() {

    override fun getVMClass(): Class<ProfileViewModel> {
        return ProfileViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getProfile()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.viewState.observe(this, Observer {
            it?.let { handleViewState(it) }
        })

        viewModel.errorState.observe(this, Observer {
            it?.let { showToast(it) }
        })


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        appBar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: AppBarStateChangeListener.State) {

                when (state) {
                    AppBarStateChangeListener.State.COLLAPSED -> if (text_name_small.alpha < 1.0f) {
                        text_name_small.visibility = View.VISIBLE
                        text_name_small.animate()
                                //.translationY(toolbarLayout.getHeight())
                                .alpha(1.0f)
                                .setListener(null)
                    }
                    AppBarStateChangeListener.State.IDLE -> if (text_name_small.alpha > 0.0f) {
                        text_name_small.animate()
                                .translationY(0f)
                                .alpha(0.0f)
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        super.onAnimationEnd(animation)
                                        text_name_small.visibility = View.GONE
                                    }
                                })
                    }
                    AppBarStateChangeListener.State.EXPANDED -> {
                    }
                }

            }
        })

        profile_options_button.setOnClickListener {
            context?.launchActivity<SettingsActivity>()
        }

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh_layout.setOnRefreshListener {
            viewModel.refreshProfile()
        }

        localUserData.user?.let {
            text_name_small.text = it.fullName
            avatar_view.setAvatarURL(it.avatarUrl)
            name_text_view.text = it.fullName
            job_text_view.text = it.company?.formattedName
        }

        chart_activity.setUp()
        chart_shares.setUp()

        button_shares_history.setOnClickListener {
            context?.launchActivity<ShareHistoryActivity>()
        }
        button_shares_history.isFocusableInTouchMode = false
        button_shares_history.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) v.performClick() }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun handleViewState(state: ProfileViewState) {

        status_view.setScreenState(state.screenState)
        swipe_refresh_layout.isRefreshing = state.isRefreshing

        state.profile?.let {
            profile_container.show()

            avatar_view.setLevel(it.exp.level)

            it.status?.let {
                container_profile_status.show()
                container_status.show()
                button_add_status.remove()

                imageLoader.load(
                        url = localUserData.user!!.avatarUrl,
                        imageView = image_status_avatar,
                        placeholder = R.drawable.ic_avatar_placeholder,
                        transformation = PicassoCircleTransform())

                text_status.text = it.status
                text_status_date.text = it.date.formatElapsed()

                container_status.setOnClickListener {
                    // TODO showStatusOptionsDialog()
                    showNewStatuDialog()
                }

            } ?: let {
                container_profile_status.show()
                container_status.remove()
                button_add_status.show()

                button_add_status.setOnClickListener {
                    showNewStatuDialog()
                }

            }

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

    private fun showStatusOptionsDialog() {
        val dialog = BottomSheetDialog(context!!)
        val sheetView = layoutInflater.inflate(R.layout.dialog_profile_status, null)

        dialog.setContentView(sheetView)
        dialog.show()

        sheetView.find<TextView>(R.id.new_status).setOnClickListener {
            dialog.dismiss()
            showNewStatuDialog()
        }

        sheetView.find<TextView>(R.id.delete_status).remove()
        sheetView.find<TextView>(R.id.delete_status).setOnClickListener {
            dialog.dismiss()
            // TODO eliminazione stato
        }
    }

    private fun showNewStatuDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_status_input, null)

        val input = view.findViewById<TextInputEditText>(R.id.status_input)

        val dialogBuilder = AlertDialog.Builder(context!!)
                .setView(view)
                .setPositiveButton("Pubblica", { dialog, id ->
                    dialog.dismiss()
                    viewModel.updateStatus(input.text.toString())
                })
                .setNegativeButton("Annulla", { dialog, id ->
                    dialog.dismiss()
                })


        val dialog = dialogBuilder.create()

        dialog.window.attributes.gravity = Gravity.BOTTOM
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.show()

        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        dialog.setOnDismissListener {
            view?.let{
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }

        }

    }


}


