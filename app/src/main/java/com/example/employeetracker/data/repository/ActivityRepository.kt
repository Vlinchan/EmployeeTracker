package com.example.employeetracker.data.repository

import com.example.employeetracker.data.ActivityDao
import com.example.employeetracker.models.Activity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ActivityRepository @Inject constructor(
    private val activityDao: ActivityDao
) {
    fun getAllActivities(): Flow<List<Activity>> = activityDao.getAllActivities()
    
    fun getRecentActivities(): Flow<List<Activity>> = activityDao.getRecentActivities()

    suspend fun addActivity(activity: Activity) {
        activityDao.insertActivity(activity)
    }

    suspend fun deleteActivity(activityId: Long) {
        activityDao.deleteActivity(activityId)
    }
}