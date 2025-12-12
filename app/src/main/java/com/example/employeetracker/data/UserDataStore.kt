package com.example.employeetracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserDataStore(context: Context) {

    private val dataStore = context.userDataStore

    private object PreferencesKeys {
        val USER_NAME_KEY = stringPreferencesKey("user_name_key")
        val USER_PHOTO_URI_KEY = stringPreferencesKey("user_photo_uri_key")
    }

    val getUserName: Flow<String?> = dataStore.data.map {
        it[PreferencesKeys.USER_NAME_KEY] ?: "David Chen" // Default name
    }

    suspend fun saveUserName(name: String) {
        dataStore.edit {
            it[PreferencesKeys.USER_NAME_KEY] = name
        }
    }

    val getPhotoUri: Flow<String?> = dataStore.data.map {
        it[PreferencesKeys.USER_PHOTO_URI_KEY]
    }

    suspend fun savePhotoUri(uri: String) {
        dataStore.edit {
            it[PreferencesKeys.USER_PHOTO_URI_KEY] = uri
        }
    }

    suspend fun clearData() {
        dataStore.edit {
            it.clear()
        }
    }
}
