package com.example.employeetracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.employeetracker.data.UserDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDataStore = UserDataStore(application)

    val userName: StateFlow<String> = userDataStore.getUserName
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val userPhotoUri: StateFlow<String?> = userDataStore.getPhotoUri
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            userDataStore.saveUserName(newName)
        }
    }

    fun updateUserPhoto(uri: String) {
        viewModelScope.launch {
            userDataStore.savePhotoUri(uri)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userDataStore.clearData()
        }
    }
}
