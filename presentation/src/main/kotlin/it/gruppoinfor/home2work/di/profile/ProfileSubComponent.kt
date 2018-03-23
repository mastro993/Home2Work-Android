package it.gruppoinfor.home2work.di.user

import dagger.Subcomponent
import it.gruppoinfor.home2work.di.profile.ProfileModule
import it.gruppoinfor.home2work.di.profile.ProfileScope
import it.gruppoinfor.home2work.profile.ProfileFragment


@ProfileScope
@Subcomponent(modules = [ProfileModule::class])
interface ProfileSubComponent {
    fun inject(profileFragment: ProfileFragment)
}