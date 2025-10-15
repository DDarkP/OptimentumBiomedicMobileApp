package com.example.views.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.views.login.LoginScreen
import com.example.views.ui.views.SignUpScreen
import com.example.views.ui.views.MenuScreen
import com.example.views.ui.views.InventarioScreen

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("menu") },
                onRegisterClick = { navController.navigate("signup") },
                onForgotPasswordClick = { /* ... */ }
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignupSuccess = { navController.popBackStack() },
                onBackToLoginClick = { navController.popBackStack() }
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
            InventarioScreen()
        }
    }
}
