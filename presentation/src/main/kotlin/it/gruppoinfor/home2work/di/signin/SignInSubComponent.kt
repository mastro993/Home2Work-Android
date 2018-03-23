package it.gruppoinfor.home2work.di.signin

import dagger.Subcomponent
import it.gruppoinfor.home2work.signin.SignInActivity

@SignInScope
@Subcomponent(modules = [SignInModule::class])
interface SignInSubComponent {
    fun inject(authActivity: SignInActivity)
}