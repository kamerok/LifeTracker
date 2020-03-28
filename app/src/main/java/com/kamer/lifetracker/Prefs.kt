package com.kamer.lifetracker

import android.content.SharedPreferences
import androidx.core.content.edit


class Prefs(private val sharedPrefs: SharedPreferences) {

    var sheetId: String?
        get() = sharedPrefs.getString("sheet.id", null)
        set(value) = sharedPrefs.edit { putString("sheet.id", value) }

}
