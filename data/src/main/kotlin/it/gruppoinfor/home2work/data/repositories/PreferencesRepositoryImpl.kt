package it.gruppoinfor.home2work.data.repositories

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import it.gruppoinfor.home2work.data.entities.UserData
import it.gruppoinfor.home2work.data.mappers.UserDataEntityMapper
import it.gruppoinfor.home2work.data.mappers.UserEntityDataMapper
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.interfaces.PreferencesRepository

class PreferencesRepositoryImpl(
        val context: Context,
        val userDataEntityMapper: UserDataEntityMapper,
        val userEntityDataMapper: UserEntityDataMapper
) : PreferencesRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    private var cache: Cache = Cache(null, null, null, null)

    private companion object {
        const val PREF_LAST_EMAIL = "last_email"
        const val PREF_USER_DATA = "user"
        const val PREF_SESSION_TOKEN = "session_token"
        const val PREF_FIREBASE_TOKEN = "firebase_token"
    }

    override var sessionToken: String?
        get() {
            if (cache.sessionToken.isNullOrEmpty()) {
                cache = cache.copy(sessionToken = prefs.getString(PREF_SESSION_TOKEN, null))
            }
            return cache.sessionToken
        }
        set(value) {
            cache = cache.copy(sessionToken = value)
            val editor = prefs.edit()
            editor.putString(PREF_SESSION_TOKEN, value)
            editor.apply()
        }
    override var lastEmail: String?
        get() {
            if (cache.lastEmail.isNullOrEmpty()) {
                cache = cache.copy(lastEmail = prefs.getString(PREF_LAST_EMAIL, null))
            }
            return cache.lastEmail
        }
        set(value) {
            cache = cache.copy(lastEmail = value)
            val editor = prefs.edit()
            editor.putString(PREF_LAST_EMAIL, value)
            editor.apply()
        }
    override var firebaseToken: String?
        get(){
            if (cache.firebaseToken.isNullOrEmpty()) {
                cache = cache.copy(firebaseToken = prefs.getString(PREF_FIREBASE_TOKEN, null))
            }
            return cache.firebaseToken
        }
        set(value) {
            cache = cache.copy(firebaseToken = value)
            val editor = prefs.edit()
            editor.putString(PREF_FIREBASE_TOKEN, value)
            editor.apply()
        }
    override var user: UserEntity?
        get(){
            if (cache.user == null) {
                val gson = Gson()
                val json = prefs.getString(PREF_USER_DATA, "")
                val obj = gson.fromJson(json, UserData::class.java)
                cache = cache.copy(user = obj as UserData)
            }
            return userDataEntityMapper.mapFrom(cache.user!!)
        }
        set(value) {

            val userData = userEntityDataMapper.mapFrom(value!!)
            cache = cache.copy(user = userData)

            val prefsEditor = prefs.edit()
            val gson = Gson()
            val json = gson.toJson(userData)
            prefsEditor.putString(PREF_USER_DATA, json)
            prefsEditor.apply()

        }

    override fun clear() {
        val editor = prefs.edit()
        editor.remove(PREF_USER_DATA)
        //editor.remove(PREF_LAST_EMAIL)
        editor.remove(PREF_FIREBASE_TOKEN)
        editor.remove(PREF_SESSION_TOKEN)
        editor.apply()
    }

    private data class Cache(
            var sessionToken: String?,
            var lastEmail: String?,
            var firebaseToken: String?,
            var user: UserData?
    )
}