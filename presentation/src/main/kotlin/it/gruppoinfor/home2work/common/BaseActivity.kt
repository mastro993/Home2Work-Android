package it.gruppoinfor.home2work.common

import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import com.google.firebase.iid.FirebaseInstanceId
import it.gruppoinfor.home2work.di.DaggerActivity
import timber.log.Timber
import javax.inject.Inject


abstract class BaseActivity<VM : BaseViewModel, VMF : ViewModelProvider.Factory> : DaggerActivity() {

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

    fun close() {
        supportFinishAfterTransition()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

}