package it.gruppoinfor.home2work.di.sharecurrent

import dagger.Subcomponent
import it.gruppoinfor.home2work.sharecurrent.CurrentShareActivity


@CurrentShareScope
@Subcomponent(modules = [CurrentShareModule::class])
interface CurrentShareSubComponent {
    fun inject(currentShareActivity: CurrentShareActivity)
}