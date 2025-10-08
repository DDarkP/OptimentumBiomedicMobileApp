package com.example.views.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.views.ui.views.component.MenuOptionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onHojasDeVidaClick: () -> Unit,
    onCotizacionClick: () -> Unit,
    onInventarioClick: () -> Unit,
    onContratoClick: () -> Unit
) {
    val options = listOf(
        MenuOption(
            title = "Hojas de vida",
            icon = Icons.Default.Description,
            onClick = onHojasDeVidaClick
        ),
        MenuOption(
            title = "Cotización de servicios y equipo",
            icon = Icons.Default.Build,
            onClick = onCotizacionClick
        ),
        MenuOption(
            title = "Inventario de equipos",
            icon = Icons.Default.Inventory,
            onClick = onInventarioClick
        ),
        MenuOption(
            title = "Contrato",
            icon = Icons.Default.Assignment,
            onClick = onContratoClick
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menú Principal") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(160.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(options) { option ->
                    MenuOptionCard(option)
                }
            }
        }
    }
}

data class MenuOption(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)
