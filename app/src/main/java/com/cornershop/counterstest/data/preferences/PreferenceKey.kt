package com.cornershop.counterstest.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey

object PreferenceKeys {
    const val PREFERENCES_NAME_USER =  "datastore_prefs"
    val IS_USER_LOGGED_IN = booleanPreferencesKey("is_user_logged_in")
}