package com.example.employeetracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val designation: String,
    val email: String,
    val department: String,
    val phoneNumber: String,
    val performance: Float = 0.0f,
    val rating: Int = 0
)
