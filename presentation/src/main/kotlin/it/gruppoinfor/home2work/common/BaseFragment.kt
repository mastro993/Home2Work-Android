package it.gruppoinfor.home2work.common

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.di.DaggerFragment
import javax.inject.Inject


abstract class BaseFragment<VM : BaseViewModel, VMF : ViewModelProvider.Factory> : DaggerFragment() {

    @Inject
    lateinit var factory: VMF
    @Inject
    lateinit var localUserData: LocalUserData
    @Inject
    lateinit var imageLoader: ImageLoader

    lateinit var viewModel: VM

    abstract fun getVMClass(): Class<VM>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(getVMClass())
    }

}