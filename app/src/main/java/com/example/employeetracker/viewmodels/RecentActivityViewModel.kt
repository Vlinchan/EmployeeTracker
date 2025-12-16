package com.example.employeetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.employeetracker.models.Activity
import com.example.employeetracker.data.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RecentActivityViewModel @Inject constructor(
    activityRepository: ActivityRepository
) : ViewModel() {

    val activities: StateFlow<List<Activity>> = activityRepository.getRecentActivities()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
