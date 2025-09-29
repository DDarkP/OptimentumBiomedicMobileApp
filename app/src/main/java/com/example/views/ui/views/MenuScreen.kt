package com.example.views.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MenuScreen(
    onHojasDeVidaClick: () -> Unit,
    onCotizacionClick: () -> Unit,
    onInventarioClick: () -> Unit,
    onContratoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "Menú Principal",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = onHojasDeVidaClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Realizar hojas de vida")
        }

        Button(
            onClick = onCotizacionClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cotización de servicios y equipo")
        }

        Button(
            onClick = onInventarioClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Inventario de equipos")
        }

        Button(
            onClick = onContratoClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Contrato")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    MaterialTheme {
        MenuScreen(
            onHojasDeVidaClick = {},
            onCotizacionClick = {},
            onInventarioClick = {},
            onContratoClick = {}
        )
    }
}
