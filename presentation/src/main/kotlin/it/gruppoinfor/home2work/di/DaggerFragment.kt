package it.gruppoinfor.home2work.di


import android.os.Bundle
import android.support.v4.app.Fragment

abstract class DaggerFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DipendencyInjector.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        DipendencyInjector.release(this)
    }
}