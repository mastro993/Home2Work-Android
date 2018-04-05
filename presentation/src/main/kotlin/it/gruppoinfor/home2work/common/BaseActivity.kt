package it.gruppoinfor.home2work.common

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.di.DipendencyInjector
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


abstract class BaseActivity<VM : BaseViewModel<VS>, VMF : ViewModelProvider.Factory, VS : BaseViewState> : AppCompatActivity() {

    @Inject
    lateinit var factory: VMF
    @Inject
    lateinit var localUserData: LocalUserData

    lateinit var viewModel: VM

    abstract fun initUI()

    abstract fun observeViewState(viewState: VS?)

    fun onCreate(@LayoutRes contentView: Int, savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(contentView)
        DipendencyInjector.inject(this)
        viewModel = ViewModelProvider(this, factory).get(viewModel::class.java)
        initUI()
        observeViewState(viewModel.viewState.value)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        DipendencyInjector.release(this
        )
    }

}