package it.gruppoinfor.home2work.common.user

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences


class SettingsPreferences constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("settings", MODE_PRIVATE)

    companion object {
        private const val PREFS_VACANCY_MODE = "vacancy_mode"
    }

    var vacancyModeEnabled: Boolean
        get() {
            return prefs.getBoolean(PREFS_VACANCY_MODE, false)
        }
        set(value) {
            val editor = prefs.edit()
            editor.putBoolean(PREFS_VACANCY_MODE, value)
            editor.apply()
        }

}