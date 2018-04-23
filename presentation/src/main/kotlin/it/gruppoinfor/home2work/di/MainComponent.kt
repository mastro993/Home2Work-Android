package it.gruppoinfor.home2work.di

import dagger.Component
import it.gruppoinfor.home2work.App
import it.gruppoinfor.home2work.common.boot.BootReceiver
import it.gruppoinfor.home2work.common.views.AvatarView
import it.gruppoinfor.home2work.di.chat.ChatModule
import it.gruppoinfor.home2work.di.chat.ChatSubComponent
import it.gruppoinfor.home2work.di.firebase.FirebaseModule
import it.gruppoinfor.home2work.di.firebase.FirebaseSubComponent
import it.gruppoinfor.home2work.di.home.HomeModule
import it.gruppoinfor.home2work.di.home.HomeSubComponent
import it.gruppoinfor.home2work.di.leaderboard.LeaderboardModule
import it.gruppoinfor.home2work.di.leaderboard.LeaderboardSubComponent
import it.gruppoinfor.home2work.di.main.MainModule
import it.gruppoinfor.home2work.di.main.MainSubComponent
import it.gruppoinfor.home2work.di.match.MatchModule
import it.gruppoinfor.home2work.di.match.MatchSubComponent
import it.gruppoinfor.home2work.di.module.AppModule
import it.gruppoinfor.home2work.di.module.DataModule
import it.gruppoinfor.home2work.di.module.ServiceModule
import it.gruppoinfor.home2work.di.profile.ProfileModule
import it.gruppoinfor.home2work.di.settings.SettingsModule
import it.gruppoinfor.home2work.di.settings.SettingsSubComponent
import it.gruppoinfor.home2work.di.sharecurrent.CurrentShareModule
import it.gruppoinfor.home2work.di.sharecurrent.CurrentShareSubComponent
import it.gruppoinfor.home2work.di.sharehistory.ShareHistoryModule
import it.gruppoinfor.home2work.di.sharehistory.ShareHistorySubComponent
import it.gruppoinfor.home2work.di.signin.SignInModule
import it.gruppoinfor.home2work.di.signin.SignInSubComponent
import it.gruppoinfor.home2work.di.singlechat.SingleChatModule
import it.gruppoinfor.home2work.di.singlechat.SingleChatSubComponent
import it.gruppoinfor.home2work.di.splash.SplashModule
import it.gruppoinfor.home2work.di.splash.SplashSubComponent
import it.gruppoinfor.home2work.di.user.ProfileSubComponent
import it.gruppoinfor.home2work.di.user.UserModule
import it.gruppoinfor.home2work.di.user.UserSubComponent
import it.gruppoinfor.home2work.services.*
import javax.inject.Singleton

@Singleton
@Component(modules = [
    (AppModule::class),
    (DataModule::class),
    (ServiceModule::class)
])
interface MainComponent {
    fun inject(app: App)
    fun inject(bootReceiver: BootReceiver)
    fun inject(service: LocationServiceOld)
    fun inject(service: LocationService) // TODO rimuovere
    fun inject(syncJobService: SyncJobService)
    fun inject(avatarView: AvatarView)
    fun inject(messagingService: MessagingService)
    fun plus(firebaseModule: FirebaseModule): FirebaseSubComponent
    fun plus(authModule: SignInModule): SignInSubComponent
    fun plus(splashModule: SplashModule): SplashSubComponent
    fun plus(mainModule: MainModule): MainSubComponent
    fun plus(homeModule: HomeModule): HomeSubComponent
    fun plus(matchModule: MatchModule): MatchSubComponent
    fun plus(chatModule: ChatModule): ChatSubComponent
    fun plus(singleChatModule: SingleChatModule): SingleChatSubComponent
    fun plus(profileModule: ProfileModule): ProfileSubComponent
    fun plus(shareHistoryModule: ShareHistoryModule): ShareHistorySubComponent
    fun plus(currentShareModule: CurrentShareModule): CurrentShareSubComponent
    fun plus(userModule: UserModule): UserSubComponent
    fun plus(settingsModule: SettingsModule): SettingsSubComponent
    fun plus(leaderboardModule: LeaderboardModule): LeaderboardSubComponent
}