package com.example.views.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import com.example.views.R
import androidx.compose.foundation.isSystemInDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onHojasDeVidaClick: () -> Unit,
    onCotizacionClick: () -> Unit,
    onInventarioClick: () -> Unit,
    onContratoClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Sistema de Inventarios",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo_texto), // coloca tu logo aquí
                        contentDescription = "Logo",
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(40.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MenuCard(
                    title = "Realizar Hojas de Vida",
                    imageRes = R.drawable.hojas_vida,
                    onClick = onHojasDeVidaClick
                )

                MenuCard(
                    title = "Cotización de Servicios y Equipo",
                    imageRes = R.drawable.cotizacion,
                    onClick = onCotizacionClick
                )

                MenuCard(
                    title = "Inventario de Equipos",
                    imageRes = R.drawable.inventario,
                    onClick = onInventarioClick
                )

                MenuCard(
                    title = "Contrato",
                    imageRes = R.drawable.contrato,
                    onClick = onContratoClick
                )
            }
        }
    )
}

@Composable
fun MenuCard(
    title: String,
    imageRes: Int,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (pressed) 0.97f else 1f)
    val isDarkTheme = isSystemInDarkTheme()


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 8.dp)
            .scale(scale)
            .clickable(
                onClick = {
                    pressed = true
                    onClick()
                    pressed = false
                }
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isDarkTheme)
                            Color.Black.copy(alpha = 0.5f)
                        else
                            Color.Black.copy(alpha = 0.3f)
                    )
            )

            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isDarkTheme)
                            Color.White.copy(alpha = 0.2f)
                        else
                            Color.Black.copy(alpha = 0.4f)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}


data class MenuOption(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)
