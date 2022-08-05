package com.cornershop.counterstest.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.cornershop.counterstest.data.database.entities.toDatabase
import com.cornershop.counterstest.data.models.ResultCounter
import com.cornershop.counterstest.data.preferences.PreferenceKeys
import com.cornershop.counterstest.data.server.toServer
import com.cornershop.counterstest.domain.entity.Counter
import com.cornershop.counterstest.domain.entity.toDomain
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject
import com.cornershop.counterstest.data.database.entities.Counter as CounterDB
import com.cornershop.counterstest.data.server.Counter as CounterServer


class LoggedRepository @Inject constructor(
    private val prefsDataStore: DataStore<Preferences>
) {

    fun getUserLoggedInState(): Flow<Boolean> =
        prefsDataStore.data
            .catch { exception ->
                // dataStore.data throws an IOException when an error is encountered when reading data
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                preferences[PreferenceKeys.IS_USER_LOGGED_IN] ?: false
            }

    suspend fun saveUserLoggedInState(state: Boolean) {
        prefsDataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_USER_LOGGED_IN] = state
        }
    }

}