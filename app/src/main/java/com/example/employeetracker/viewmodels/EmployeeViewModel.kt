package com.example.employeetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.employeetracker.models.Employee
import com.example.employeetracker.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    val allEmployees: StateFlow<List<Employee>> = employeeRepository.getAllEmployees().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addEmployee(name: String, email: String, designation: String, department: String, phoneNumber: String) {
        viewModelScope.launch {
            employeeRepository.insertEmployee(
                Employee(
                    name = name,
                    email = email,
                    designation = designation,
                    department = department,
                    phoneNumber = phoneNumber
                )
            )
        }
    }
}
