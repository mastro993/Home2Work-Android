package it.gruppoinfor.home2work.data.repositories

import android.content.Context
import android.content.SharedPreferences
import it.gruppoinfor.home2work.domain.interfaces.SettingsRepository

class SettingsRepositoryImpl(val context: Context) : SettingsRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private companion object {
        const val PREFS_VACANCY_MODE = "vacancy_mode"
        var cache: Cache = Cache(null)
    }

    override var vacancyModeEnabled: Boolean
        get() {
            if (cache.vacancyModeEnabled == null) {
                cache = cache.copy(vacancyModeEnabled = prefs.getBoolean(PREFS_VACANCY_MODE, false))
            }
            return cache.vacancyModeEnabled!!
        }
        set(value) {
            cache = cache.copy(vacancyModeEnabled = value)
            val editor = prefs.edit()
            editor.putBoolean(PREFS_VACANCY_MODE, value)
            editor.apply()
        }


    private data class Cache(
            var vacancyModeEnabled: Boolean?
    )
}