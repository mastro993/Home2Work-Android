package it.gruppoinfor.home2work.di.splash

import dagger.Subcomponent
import it.gruppoinfor.home2work.splash.SplashActivity

@SplashScope
@Subcomponent(modules = [SplashModule::class])
interface SplashSubComponent {
    fun inject(splashActivity: SplashActivity)
}