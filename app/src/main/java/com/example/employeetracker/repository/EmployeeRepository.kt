package com.example.employeetracker.repository

import com.example.employeetracker.data.EmployeeDao
import com.example.employeetracker.models.Employee
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EmployeeRepository @Inject constructor(private val employeeDao: EmployeeDao) {

    fun getAllEmployees(): Flow<List<Employee>> = employeeDao.getAllEmployees()

    suspend fun getEmployeeByName(name: String): Employee? = employeeDao.getEmployeeByName(name)

    suspend fun getEmployeeById(id: Long): Employee? = employeeDao.getEmployeeById(id)

    suspend fun insertEmployee(employee: Employee) {
        employeeDao.insertEmployee(employee)
    }

    suspend fun updateEmployeePerformance(employeeId: Long, performance: Float) {
        employeeDao.updateEmployeePerformance(employeeId, performance)
    }

    suspend fun updateEmployeeRating(employeeId: Long, rating: Int) {
        employeeDao.updateEmployeeRating(employeeId, rating)
    }
}