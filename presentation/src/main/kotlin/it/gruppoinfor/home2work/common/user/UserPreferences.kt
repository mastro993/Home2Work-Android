package it.gruppoinfor.home2work.common.user

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import io.reactivex.Observable
import it.gruppoinfor.home2work.common.ASyncTransformer
import it.gruppoinfor.home2work.entities.User


class UserPreferences constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE)

    companion object {
        const val PREF_EMAIL = "email"
        const val PREF_USER_DATA = "user"
        const val PREF_SESSION_TOKEN = "session_token"
        const val PREF_FIREBASE_TOKEN = "firebase_token"
    }

    fun getSessionToken(): String? {
        return prefs.getString(PREF_SESSION_TOKEN, null)
    }

    fun setSessionToken(token: String?) {
        val editor = prefs.edit()
        editor.putString(PREF_SESSION_TOKEN, token)
        editor.apply()
    }

    fun getEmail(): String? {
        return prefs.getString(PREF_EMAIL, null)
    }

    fun setEmail(token: String?) {
        val editor = prefs.edit()
        editor.putString(PREF_EMAIL, token)
        editor.apply()
    }

    fun getFirebaseToken(): String? {
        return prefs.getString(PREF_FIREBASE_TOKEN, null)
    }

    fun setFirebaseToken(token: String?) {
        token?.let {
            val editor = prefs.edit()
            editor.putString(PREF_FIREBASE_TOKEN, it)
            editor.apply()
        }
    }

    fun clear() {
        val editor = prefs.edit()
        editor.remove(PREF_USER_DATA)
        editor.remove(PREF_FIREBASE_TOKEN)
        editor.remove(PREF_SESSION_TOKEN)
        editor.apply()
    }

    fun getUserData(): User? {

        val gson = Gson()
        val json = prefs.getString(PREF_USER_DATA, "")
        val obj = gson.fromJson(json, User::class.java)
        return obj as User

    }

    fun setUserData(user: User) {

        Observable.create<Void> {
            val prefsEditor = prefs.edit()
            val gson = Gson()
            val json = gson.toJson(user)
            prefsEditor.putString(UserPreferences.PREF_USER_DATA, json)
            prefsEditor.apply()
        }.compose(ASyncTransformer<Void>())
                .subscribe()

    }


}