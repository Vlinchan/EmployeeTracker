package com.example.employeetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.employeetracker.models.Employee
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employees ORDER BY name ASC")
    fun getAllEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE name = :name LIMIT 1")
    suspend fun getEmployeeByName(name: String): Employee?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee)

    @Query("UPDATE employees SET performance = :performance WHERE id = :employeeId")
    suspend fun updateEmployeePerformance(employeeId: Long, performance: Float)

    @Query("UPDATE employees SET rating = :rating WHERE id = :employeeId")
    suspend fun updateEmployeeRating(employeeId: Long, rating: Int)
}
