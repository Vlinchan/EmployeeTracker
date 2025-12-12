package com.example.employeetracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.employeetracker.data.PasswordDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val passwordDataStore = PasswordDataStore(application)

    private val _passwordUpdateState = MutableStateFlow<PasswordUpdateState>(PasswordUpdateState.Idle)
    val passwordUpdateState: StateFlow<PasswordUpdateState> = _passwordUpdateState

    fun updatePassword(current: String, new: String, confirm: String) {
        viewModelScope.launch {
            val storedPassword = passwordDataStore.getPassword.first()

            // Only check the current password if one has already been set
            if (storedPassword != null && storedPassword.isNotBlank()) {
                if (storedPassword != current) {
                    _passwordUpdateState.value = PasswordUpdateState.Error("Incorrect current password.")
                    return@launch
                }
            }

            if (new != confirm) {
                _passwordUpdateState.value = PasswordUpdateState.Error("New passwords do not match.")
                return@launch
            }

            passwordDataStore.savePassword(new)
            _passwordUpdateState.value = PasswordUpdateState.Success
        }
    }

    fun resetState() {
        _passwordUpdateState.value = PasswordUpdateState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            passwordDataStore.clearPassword()
        }
    }
}

sealed class PasswordUpdateState {
    data object Idle : PasswordUpdateState()
    data object Success : PasswordUpdateState()
    data class Error(val message: String) : PasswordUpdateState()
}
