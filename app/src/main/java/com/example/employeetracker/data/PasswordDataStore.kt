package com.example.employeetracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "password_prefs")

class PasswordDataStore(context: Context) {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val PASSWORD_KEY = stringPreferencesKey("password_key")
    }

    val getPassword: Flow<String?> = dataStore.data.map {
        it[PreferencesKeys.PASSWORD_KEY]
    }

    suspend fun savePassword(password: String) {
        dataStore.edit {
            it[PreferencesKeys.PASSWORD_KEY] = password
        }
    }

    suspend fun clearPassword() {
        dataStore.edit {
            it.clear()
        }
    }
}
