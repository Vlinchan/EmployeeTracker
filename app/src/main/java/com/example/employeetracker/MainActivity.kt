package com.example.employeetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.employeetracker.data.ThemeDataStore
import com.example.employeetracker.employeesection.EmployeeSec
import com.example.employeetracker.screens.ChangePasswordScreen
import com.example.employeetracker.screens.SettingsScreen
import com.example.employeetracker.ui.theme.EmployeeTrackerTheme
import com.example.employeetracker.viewmodels.LoginState
import com.example.employeetracker.viewmodels.LoginViewModel
import com.example.employeetracker.viewmodels.PasswordViewModel
import com.example.employeetracker.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var themeDataStore: ThemeDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeDataStore = ThemeDataStore(this)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by themeDataStore.isDarkTheme.collectAsState(isSystemInDarkTheme())

            EmployeeTrackerTheme(darkTheme = isDarkTheme) {

                val navController = rememberNavController()
                val loginViewModel: LoginViewModel = hiltViewModel()
                val loginState by loginViewModel.loginState.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination =
                        if (loginState is LoginState.Success) "dashboard" else "login"
                ) {

                    // LOGIN SCREEN
                    composable("login") {
                        LoginScreen(
                            onAdminLoginSuccess = {
                                navController.navigate("dashboard") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onEmployeeLoginSuccess = {
                                navController.navigate("employee_section") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    // ADMIN DASHBOARD
                    composable("dashboard") {
                        val userViewModel: UserViewModel = hiltViewModel()
                        val passwordViewModel: PasswordViewModel = hiltViewModel()

                        DashboardScreenMain(
                            themeDataStore = themeDataStore,
                            onLogout = {
                                userViewModel.logout()
                                passwordViewModel.logout()
                                loginViewModel.resetState()
                                navController.navigate("login") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            },
                            onNavigateToSettings = { navController.navigate("settings") }
                        )
                    }

                    // SETTINGS SCREEN
                    composable("settings") {
                        val userViewModel: UserViewModel = hiltViewModel()
                        val passwordViewModel: PasswordViewModel = hiltViewModel()

                        SettingsScreen(
                            themeDataStore = themeDataStore,
                            onLogout = {
                                userViewModel.logout()
                                passwordViewModel.logout()
                                loginViewModel.resetState()
                                navController.navigate("login") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            },
                            onNavigateToChangePassword = { navController.navigate("change_password") },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // CHANGE PASSWORD
                    composable("change_password") {
                        val userViewModel: UserViewModel = hiltViewModel()

                        ChangePasswordScreen(
                            userViewModel = userViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // EMPLOYEE SECTION
                    composable("employee_section") {
                        EmployeeSec()
                    }
                }
            }
        }
    }
}
