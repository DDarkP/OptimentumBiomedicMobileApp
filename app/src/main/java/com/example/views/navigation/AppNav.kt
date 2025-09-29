package com.example.views.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.views.ui.views.SignUp
import com.example.views.ui.views.Login
import com.example.views.ui.views.MenuScreen
import com.example.views.ui.views.InventarioScreen

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            Login(
                onLoginClick = { email, pass ->
                    navController.navigate("menu")// validación
                },
                onForgotPasswordClick = {
                    // navegación recuperar contraseña
                },
                onRegisterClick = {
                    navController.navigate("signup")
                }
            )
        }
        composable("signup") {
            SignUp(
                onSignupClick = { name, email, password ->
                    // registro
                    navController.popBackStack() // vuelve al login
                },
                onBackToLoginClick = {
                    navController.popBackStack() // vuelve al login
                }
            )
        }
        // Pantalla de menú
        composable("menu") {
            MenuScreen(
                onHojasDeVidaClick = { /* navController.navigate("hojas") */ },
                onCotizacionClick = { /* navController.navigate("cotizacion") */ },
                onInventarioClick = { navController.navigate("inventario") },
                onContratoClick = { /* navController.navigate("contrato") */ }
            )
        }

        composable("inventario") {
            InventarioScreen(
                itemsList = listOf("Laptop Dell", "Proyector Epson", "Router Cisco"),
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
