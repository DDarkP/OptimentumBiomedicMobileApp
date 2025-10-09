//package com.example.views.view_model
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class LoginViewModel : ViewModel() {
//
//    var email by mutableStateOf("")
//        private set
//
//    var password by mutableStateOf("")
//        private set
//
//    var isLoading by mutableStateOf(false)
//        private set
//
//    var errorMessage by mutableStateOf<String?>(null)
//        private set
//
//    fun onEmailChange(newEmail: String) {
//        email = newEmail
//    }
//
//    fun onPasswordChange(newPass: String) {
//        password = newPass
//    }
//
//    fun onLoginClick(onSuccess: () -> Unit) {
//        if (email.isBlank() || password.isBlank()) {
//            errorMessage = "Todos los campos son obligatorios"
//            return
//        }
//
//        isLoading = true
//        errorMessage = null
//
//        // Usamos viewModelScope para manejar el ciclo de vida
//        viewModelScope.launch {
//            delay(1000) // Simulamos login remoto
//            isLoading = false
//
//            if (email == "admin@example.com" && password == "1234") {
//                // Navegamos en el hilo principal
//                withContext(Dispatchers.Main) {
//                    onSuccess()
//                }
//            } else {
//                errorMessage = "Credenciales incorrectas"
//            }
//        }
//    }
//}
package com.example.views.view_model

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.views.data.model.Usuario
import com.example.views.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(application)

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPass: String) {
        password = newPass
    }

    fun onLoginClick(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Todos los campos son obligatorios"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            val user = repository.login(email, password)
            isLoading = false
            if (user != null) {
                onSuccess()
            } else {
                errorMessage = "Credenciales incorrectas"
            }
        }
    }

    fun onRegisterUser(nombre: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.registrar(Usuario(nombre = nombre, email = email, password = password))
            onSuccess()
        }
    }
}
