package it.gruppoinfor.home2work.common.user

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences


class SettingsPreferences constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("settings", MODE_PRIVATE)


}