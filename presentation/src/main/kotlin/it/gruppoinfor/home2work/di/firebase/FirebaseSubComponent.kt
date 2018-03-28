package it.gruppoinfor.home2work.di.firebase

import dagger.Subcomponent
import it.gruppoinfor.home2work.services.FirebaseTokenService


@FirebaseScope
@Subcomponent(modules = [FirebaseModule::class])
interface FirebaseSubComponent {
    fun inject(firebaseTokenService: FirebaseTokenService)
}