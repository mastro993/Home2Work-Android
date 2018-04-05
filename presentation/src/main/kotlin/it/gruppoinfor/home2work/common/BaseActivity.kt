package it.gruppoinfor.home2work.common

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import it.gruppoinfor.home2work.common.user.LocalUserData
import javax.inject.Inject


abstract class BaseActivity<VM : BaseViewModel, VMF : ViewModelProvider.Factory> : AppCompatActivity() {

    @Inject
    lateinit var factory: VMF
    @Inject
    lateinit var localUserData: LocalUserData


    private lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        viewModel = ViewModelProvider(this, factory).get(viewModel::class.java)
    }

}