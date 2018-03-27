package it.gruppoinfor.home2work.di.user

import dagger.Subcomponent
import it.gruppoinfor.home2work.user.UserActivity

@UserScope
@Subcomponent(modules = [UserModule::class])
interface UserSubComponent {
    fun inject(userActivity: UserActivity)
}