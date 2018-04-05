package it.gruppoinfor.home2work.di

import android.content.Context
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

object DipendencyInjector {
    lateinit var mainComponent: MainComponent

    private var firebaseSubComponent: FirebaseSubComponent? = null
    private var signInSubComponent: SignInSubComponent? = null
    private var splashSubComponent: SplashSubComponent? = null
    private var mainSubComponent: MainSubComponent? = null
    private var homeSubComponent: HomeSubComponent? = null
    private var matchSubComponent: MatchSubComponent? = null
    private var inboxSubComponent: InboxSubComponent? = null
    private var chatSubComponent: ChatSubComponent? = null
    private var profileSubComponent: ProfileSubComponent? = null
    private var shareHistorySubComponent: ShareHistorySubComponent? = null
    private var currentShareSubComponent: CurrentShareSubComponent? = null
    private var userSubComponent: UserSubComponent? = null
    private var settingsSubComponent: SettingsSubComponent? = null

    fun init(context: Context) {

        mainComponent = DaggerMainComponent.builder()
                .appModule(AppModule(context))
                .dataModule(DataModule())
                .build()

    }

    fun createFirebaseComponent(): FirebaseSubComponent {
        firebaseSubComponent = mainComponent.plus(FirebaseModule())
        return firebaseSubComponent!!
    }

    fun releaseFirebaseComponent() {
        firebaseSubComponent = null
    }

    fun createSignInComponent(): SignInSubComponent {
        signInSubComponent = mainComponent.plus(SignInModule())
        return signInSubComponent!!
    }

    fun releaseSignInComponent() {
        signInSubComponent = null
    }

    fun createSplashComponent(): SplashSubComponent {
        splashSubComponent = mainComponent.plus(SplashModule())
        return splashSubComponent!!
    }

    fun releaseSplashComponent() {
        splashSubComponent = null
    }

    fun createMainComponent(): MainSubComponent {
        mainSubComponent = mainComponent.plus(MainModule())
        return mainSubComponent!!
    }

    fun releaseMainComponent() {
        mainSubComponent = null
    }

    fun createHomeComponent(): HomeSubComponent {
        homeSubComponent = mainComponent.plus(HomeModule())
        return homeSubComponent!!
    }

    fun releaseHomeComponent() {
        homeSubComponent = null
    }

    fun createMatchComponent(): MatchSubComponent {
        matchSubComponent = mainComponent.plus(MatchModule())
        return matchSubComponent!!
    }

    fun releaseMatchComponent() {
        matchSubComponent = null
    }

    fun createInboxComponent(): InboxSubComponent {
        inboxSubComponent = mainComponent.plus(InboxModule())
        return inboxSubComponent!!
    }

    fun releaseInboxComponent() {
        inboxSubComponent = null
    }

    fun createChatComponent(): ChatSubComponent {
        chatSubComponent = mainComponent.plus(ChatModule())
        return chatSubComponent!!
    }

    fun releaseChatComponent() {
        chatSubComponent = null
    }

    fun createProfileComponent(): ProfileSubComponent {
        profileSubComponent = mainComponent.plus(ProfileModule())
        return profileSubComponent!!
    }

    fun releaseProfileComponent() {
        profileSubComponent = null
    }

    fun createShareHistoryComponent(): ShareHistorySubComponent {
        shareHistorySubComponent = mainComponent.plus(ShareHistoryModule())
        return shareHistorySubComponent!!
    }

    fun releaseShareHistoryComponent() {
        shareHistorySubComponent = null
    }

    fun createCurrentShareComponent(): CurrentShareSubComponent {
        currentShareSubComponent = mainComponent.plus(CurrentShareModule())
        return currentShareSubComponent!!
    }

    fun releaseCurrentShareComponent() {
        currentShareSubComponent = null
    }

    fun createUserComponent(): UserSubComponent {
        userSubComponent = mainComponent.plus(UserModule())
        return userSubComponent!!
    }

    fun releaseUserComponent() {
        userSubComponent = null
    }

    fun createSettingsComponent(): SettingsSubComponent {
        settingsSubComponent = mainComponent.plus(SettingsModule())
        return settingsSubComponent!!
    }

    fun releaseSettingsComponent() {
        settingsSubComponent = null
    }


}