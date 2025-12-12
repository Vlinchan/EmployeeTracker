package com.example.employeetracker

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.employeetracker.viewmodels.LoginState
import com.example.employeetracker.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    onAdminLoginSuccess: () -> Unit,
    onEmployeeLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isLoginMode by remember { mutableStateOf(true) }
    var loginAsAdmin by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }

    val loginViewModel: LoginViewModel = hiltViewModel()
    val loginState by loginViewModel.loginState.collectAsState()

    // Only for admin auth
    LaunchedEffect(loginState) {
        if (loginAsAdmin && loginState is LoginState.Success) {
            onAdminLoginSuccess()
        }
    }

    // --- Design Colors & Gradients ---
    val skyGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE0F7FA), // Light Blue Top
            Color(0xFFE3F2FD),
            Color(0xFFFFFFFF)  // White Bottom
        )
    )
    val glassColor = Color.White.copy(alpha = 0.85f)
    val inputFieldColor = Color(0xFFF1F3F5)
    val darkButtonColor = Color(0xFF212529)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(skyGradient),
        contentAlignment = Alignment.Center
    ) {
        // Optional: decorative background circle to enhance glass effect
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(y = (-150).dp)
                .background(Color.White.copy(alpha = 0.3f), CircleShape)
        )

        // --- GLASS CARD ---
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = glassColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Icon
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Login,
                            contentDescription = "Logo",
                            tint = Color.Black
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Heading
                Text(
                    text = if (isLoginMode) "Sign In" else "Create Account",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = when {
                        loginAsAdmin -> "Access your Admin Dashboard"
                        else -> "Access your Employee Portal"
                    },
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                // --- CUSTOM SEGMENTED CONTROL (Admin vs Employee) ---
                // Replacing Radio Buttons with a pill-shaped toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(inputFieldColor, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val adminWeight = 1f

                    // Admin Toggle
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (loginAsAdmin) Color.White else Color.Transparent)
                            .clickable { loginAsAdmin = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Admin",
                            fontWeight = if (loginAsAdmin) FontWeight.Bold else FontWeight.Normal,
                            color = if (loginAsAdmin) Color.Black else Color.Gray
                        )
                    }

                    // Employee Toggle
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!loginAsAdmin) Color.White else Color.Transparent)
                            .clickable { loginAsAdmin = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Employee",
                            fontWeight = if (!loginAsAdmin) FontWeight.Bold else FontWeight.Normal,
                            color = if (!loginAsAdmin) Color.Black else Color.Gray
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --- INPUTS ---

                // Email Input
                GlassyTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (loginAsAdmin && loginState is LoginState.Error) loginViewModel.resetState()
                    },
                    placeholder = "Email address",
                    icon = Icons.Outlined.Email,
                    backgroundColor = inputFieldColor
                )

                Spacer(Modifier.height(16.dp))

                // Password Input
                GlassyTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (loginAsAdmin && loginState is LoginState.Error) loginViewModel.resetState()
                    },
                    placeholder = "Password",
                    icon = Icons.Outlined.Lock,
                    backgroundColor = inputFieldColor,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    }
                )

                // Error Message
                if (loginAsAdmin && loginState is LoginState.Error) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = (loginState as LoginState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Forgot Password (Visual only based on image)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = "Forgot password?",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.clickable { /* Handle forgot password */ }
                    )
                }

                Spacer(Modifier.height(24.dp))

                val isLoading = loginAsAdmin && loginState is LoginState.Loading

                // --- MAIN ACTION BUTTON ---
                Button(
                    onClick = {
                        if (isLoading) return@Button
                        if (loginAsAdmin) {
                            if (isLoginMode) loginViewModel.login(email, password)
                            else loginViewModel.signUp(email, password)
                        } else {
                            onEmployeeLoginSuccess()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkButtonColor,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (isLoginMode) "Get Started" else "Sign Up",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --- TOGGLE LOGIN/SIGNUP MODE ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isLoginMode) "Need an account? " else "Already have an account? ",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (isLoginMode) "Sign Up" else "Log In",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { isLoginMode = !isLoginMode }
                    )
                }
            }
        }
    }
}

// Helper composable for the beautiful input fields
@Composable
fun GlassyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    backgroundColor: Color,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null, tint = Color.Gray) },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            disabledContainerColor = backgroundColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}