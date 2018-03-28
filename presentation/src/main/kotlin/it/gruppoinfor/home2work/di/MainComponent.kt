package it.gruppoinfor.home2work.di

import android.content.Context
import dagger.Component
import it.gruppoinfor.home2work.common.boot.BootReceiver
import it.gruppoinfor.home2work.common.views.AvatarView
import it.gruppoinfor.home2work.di.chat.ChatModule
import it.gruppoinfor.home2work.di.chat.ChatSubComponent
import it.gruppoinfor.home2work.di.firebase.FirebaseModule
import it.gruppoinfor.home2work.di.firebase.FirebaseSubComponent
import it.gruppoinfor.home2work.di.home.HomeModule
import it.gruppoinfor.home2work.di.home.HomeSubComponent
import it.gruppoinfor.home2work.di.inbox.InboxModule
import it.gruppoinfor.home2work.di.inbox.InboxSubComponent
import it.gruppoinfor.home2work.di.main.MainModule
import it.gruppoinfor.home2work.di.main.MainSubComponent
import it.gruppoinfor.home2work.di.match.MatchModule
import it.gruppoinfor.home2work.di.match.MatchSubComponent
import it.gruppoinfor.home2work.di.profile.ProfileModule
import it.gruppoinfor.home2work.di.settings.SettingsModule
import it.gruppoinfor.home2work.di.settings.SettingsSubComponent
import it.gruppoinfor.home2work.di.sharecurrent.CurrentShareModule
import it.gruppoinfor.home2work.di.sharecurrent.CurrentShareSubComponent
import it.gruppoinfor.home2work.di.sharehistory.ShareHistoryModule
import it.gruppoinfor.home2work.di.sharehistory.ShareHistorySubComponent
import it.gruppoinfor.home2work.di.signin.SignInModule
import it.gruppoinfor.home2work.di.signin.SignInSubComponent
import it.gruppoinfor.home2work.di.splash.SplashModule
import it.gruppoinfor.home2work.di.splash.SplashSubComponent
import it.gruppoinfor.home2work.di.user.ProfileSubComponent
import it.gruppoinfor.home2work.di.user.UserModule
import it.gruppoinfor.home2work.di.user.UserSubComponent
import it.gruppoinfor.home2work.services.LocationService
import javax.inject.Singleton

@Singleton
@Component(modules = [
    (AppModule::class),
    (DataModule::class),
    (ServiceModule::class)
])
interface MainComponent {
    fun inject(context: Context)
    fun inject(bootReceiver: BootReceiver)
    fun inject(locationService: LocationService)
    fun inject(avatarView: AvatarView)
    fun plus(firebaseModule: FirebaseModule): FirebaseSubComponent
    fun plus(authModule: SignInModule): SignInSubComponent
    fun plus(splashModule: SplashModule): SplashSubComponent
    fun plus(mainModule: MainModule): MainSubComponent
    fun plus(homeModule: HomeModule): HomeSubComponent
    fun plus(matchModule: MatchModule): MatchSubComponent
    fun plus(inboxModule: InboxModule): InboxSubComponent
    fun plus(chatModule: ChatModule): ChatSubComponent
    fun plus(profileModule: ProfileModule): ProfileSubComponent
    fun plus(shareHistoryModule: ShareHistoryModule): ShareHistorySubComponent
    fun plus(currentShareModule: CurrentShareModule): CurrentShareSubComponent
    fun plus(userModule: UserModule): UserSubComponent
    fun plus(settingsModule: SettingsModule): SettingsSubComponent
}