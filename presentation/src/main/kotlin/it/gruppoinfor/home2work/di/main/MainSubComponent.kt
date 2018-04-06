package it.gruppoinfor.home2work.di.main

import dagger.Subcomponent
import it.gruppoinfor.home2work.main.MainActivity


@MainScope
@Subcomponent(modules = [MainModule::class])
interface MainSubComponent {
    fun inject(mainActivity: MainActivity)
}