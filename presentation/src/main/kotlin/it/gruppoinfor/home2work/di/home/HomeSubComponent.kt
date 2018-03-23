package it.gruppoinfor.home2work.di.home

import dagger.Subcomponent
import it.gruppoinfor.home2work.home.HomeFragment

@HomeScope
@Subcomponent(modules = [HomeModule::class])
interface HomeSubComponent {
    fun inject(homeFragment: HomeFragment)
}