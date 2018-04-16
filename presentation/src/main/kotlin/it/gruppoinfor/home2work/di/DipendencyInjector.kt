package it.gruppoinfor.home2work.di

import android.content.Context
import it.gruppoinfor.home2work.chat.ChatActivity
import it.gruppoinfor.home2work.chat.SingleChatActivity
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
import it.gruppoinfor.home2work.home.HomeFragment
import it.gruppoinfor.home2work.leaderboards.LeaderboardsFragment
import it.gruppoinfor.home2work.main.MainActivity
import it.gruppoinfor.home2work.match.MatchesFragment
import it.gruppoinfor.home2work.profile.ProfileFragment
import it.gruppoinfor.home2work.settings.SettingsActivity
import it.gruppoinfor.home2work.sharecurrent.CurrentShareActivity
import it.gruppoinfor.home2work.sharehistory.ShareHistoryActivity
import it.gruppoinfor.home2work.signin.SignInActivity
import it.gruppoinfor.home2work.splash.SplashActivity
import it.gruppoinfor.home2work.user.UserActivity

object DipendencyInjector {
    lateinit var mainComponent: MainComponent

    private var firebaseSubComponent: FirebaseSubComponent? = null
    private var signInSubComponent: SignInSubComponent? = null
    private var splashSubComponent: SplashSubComponent? = null
    private var mainSubComponent: MainSubComponent? = null
    private var homeSubComponent: HomeSubComponent? = null
    private var matchSubComponent: MatchSubComponent? = null
    private var chatSubComponent: ChatSubComponent? = null
    private var singleChatSubComponent: SingleChatSubComponent? = null
    private var profileSubComponent: ProfileSubComponent? = null
    private var leaderboardSubComponent: LeaderboardSubComponent? = null
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

    fun inject(activity: DaggerActivity) {
        when (activity) {
            is SplashActivity -> {
                splashSubComponent = mainComponent.plus(SplashModule())
                splashSubComponent!!.inject(activity)
            }
            is MainActivity -> {
                mainSubComponent = mainComponent.plus(MainModule())
                mainSubComponent!!.inject(activity)
            }
            is SignInActivity -> {
                signInSubComponent = mainComponent.plus(SignInModule())
                signInSubComponent!!.inject(activity)
            }
            is SingleChatActivity -> {
                singleChatSubComponent = mainComponent.plus(SingleChatModule())
                singleChatSubComponent!!.inject(activity)
            }
            is ChatActivity -> {
                chatSubComponent = mainComponent.plus(ChatModule())
                chatSubComponent!!.inject(activity)
            }
            is ShareHistoryActivity -> {
                shareHistorySubComponent = mainComponent.plus(ShareHistoryModule())
                shareHistorySubComponent!!.inject(activity)
            }
            is CurrentShareActivity -> {
                currentShareSubComponent = mainComponent.plus(CurrentShareModule())
                currentShareSubComponent!!.inject(activity)
            }
            is UserActivity -> {
                userSubComponent = mainComponent.plus(UserModule())
                userSubComponent!!.inject(activity)
            }
            is SettingsActivity -> {
                settingsSubComponent = mainComponent.plus(SettingsModule())
                settingsSubComponent!!.inject(activity)
            }
        }
    }

    fun release(activity: DaggerActivity) {
        when (activity) {
            is SplashActivity -> splashSubComponent = null
            is MainActivity -> mainSubComponent = null
            is SignInActivity -> signInSubComponent = null
            is SingleChatActivity -> singleChatSubComponent = null
            is ChatActivity -> chatSubComponent = null
            is ShareHistoryActivity -> shareHistorySubComponent = null
            is CurrentShareActivity -> currentShareSubComponent = null
            is UserActivity -> userSubComponent = null
            is SettingsActivity -> settingsSubComponent = null
        }
    }

    fun inject(fragment: DaggerFragment) {
        when (fragment) {
            is HomeFragment -> {
                homeSubComponent = mainComponent.plus(HomeModule())
                homeSubComponent!!.inject(fragment)
            }
            is MatchesFragment -> {
                matchSubComponent = mainComponent.plus(MatchModule())
                matchSubComponent!!.inject(fragment)
            }
            is ProfileFragment -> {
                profileSubComponent = mainComponent.plus(ProfileModule())
                return profileSubComponent!!.inject(fragment)
            }
            is LeaderboardsFragment -> {
                leaderboardSubComponent = mainComponent.plus(LeaderboardModule())
                return leaderboardSubComponent!!.inject(fragment)
            }
        }
    }

    fun release(fragment: DaggerFragment) {
        when (fragment) {
            is HomeFragment -> homeSubComponent = null
            is MatchesFragment -> matchSubComponent = null
            is ProfileFragment -> profileSubComponent = null
            is LeaderboardsFragment -> leaderboardSubComponent = null
        }
    }

    fun createFirebaseComponent(): FirebaseSubComponent {
        firebaseSubComponent = mainComponent.plus(FirebaseModule())
        return firebaseSubComponent!!
    }

    fun releaseFirebaseComponent() {
        firebaseSubComponent = null
    }


}