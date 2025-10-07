package com.example.views.ui.views.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPass: String) {
        password = newPass
    }

    fun onLoginClick(
        onSuccess: () -> Unit
    ) {
        // Ejemplo básico: validación local
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Todos los campos son obligatorios"
            return
        }

        isLoading = true
        errorMessage = null

        // Simulamos un login asíncrono
        kotlinx.coroutines.GlobalScope.launch {
            kotlinx.coroutines.delay(1000)
            isLoading = false
            if (email == "admin@example.com" && password == "1234") {
                onSuccess()
            } else {
                errorMessage = "Credenciales incorrectas"
            }
        }
    }
}
