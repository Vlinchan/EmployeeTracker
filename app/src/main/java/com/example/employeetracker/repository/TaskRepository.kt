package com.example.employeetracker.repository

import com.example.employeetracker.data.TaskDao
import com.example.employeetracker.models.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    fun getTasksForEmployee(employeeId: Long): Flow<List<Task>> {
        return taskDao.getTasksForEmployee(employeeId)
    }

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
}