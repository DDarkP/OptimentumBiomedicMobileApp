package com.example.views.ui.views

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.views.data.AppDatabase
import com.example.views.data.repository.EquipoRepository
import com.example.views.view_model.InventarioViewModel
import com.example.views.view_model.InventarioViewModelFactory
import kotlinx.coroutines.launch
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val repository = EquipoRepository(db.equipoDao())
    val viewModel: InventarioViewModel = viewModel(factory = InventarioViewModelFactory(repository))

    val equipo by viewModel.equipoActual.collectAsState()
    val exportacionExitosa by viewModel.exportacionExitosa.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Selector de imagen
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.actualizarFoto(uri)
    }

    // Crear archivo (el usuario elige la ubicación)
    val crearArchivoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                val plantilla = cargarPlantilla(context)
                if (plantilla != null) {
                    viewModel.exportarAExcelAUri(context, plantilla, output)
                    Toast.makeText(
                        context,
                        "Archivo guardado correctamente",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "No se encontró la plantilla Excel en recursos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Registro de Equipo Biomédico") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            /** -------- SECCIÓN 1: DATOS GENERALES -------- */
            SeccionExpandible(titulo = "Datos Generales") {
                CampoTexto("Nombre del equipo", equipo.nombreEquipo) { valor ->
                    viewModel.actualizarCampo { it.copy(nombreEquipo = valor) }
                }
                CampoTexto("Información general", equipo.informacionGeneral) { valor ->
                    viewModel.actualizarCampo { it.copy(informacionGeneral = valor) }
                }
                CampoTexto("Marca", equipo.marca) { valor ->
                    viewModel.actualizarCampo { it.copy(marca = valor) }
                }
                CampoTexto("Modelo", equipo.modelo) { valor ->
                    viewModel.actualizarCampo { it.copy(modelo = valor) }
                }
                CampoTexto("Serie", equipo.serie) { valor ->
                    viewModel.actualizarCampo { it.copy(serie = valor) }
                }
                CampoTexto("Tipo", equipo.tipo) { valor ->
                    viewModel.actualizarCampo { it.copy(tipo = valor) }
                }
                CampoTexto("Referencia", equipo.referencia) { valor ->
                    viewModel.actualizarCampo { it.copy(referencia = valor) }
                }
                CampoTexto("Código del equipo", equipo.codigoEquipo) { valor ->
                    viewModel.actualizarCampo { it.copy(codigoEquipo = valor) }
                }
                CampoTexto("Número de inventario", equipo.numeroInventario) { valor ->
                    viewModel.actualizarCampo { it.copy(numeroInventario = valor) }
                }
            }

            /** -------- SECCIÓN 2: UBICACIÓN Y RESPONSABLE -------- */
            SeccionExpandible(titulo = "Ubicación y Responsable") {
                CampoTexto("Edificio", equipo.edificio) { valor ->
                    viewModel.actualizarCampo { it.copy(edificio = valor) }
                }
                CampoTexto("Área", equipo.area) { valor ->
                    viewModel.actualizarCampo { it.copy(area = valor) }
                }
                CampoTexto("Dirección", equipo.direccion) { valor ->
                    viewModel.actualizarCampo { it.copy(direccion = valor) }
                }
                CampoTexto("Ubicación", equipo.ubicacion) { valor ->
                    viewModel.actualizarCampo { it.copy(ubicacion = valor) }
                }
                CampoTexto("Centro de costos", equipo.centroCostos) { valor ->
                    viewModel.actualizarCampo { it.copy(centroCostos = valor) }
                }
                CampoTexto("Responsable", equipo.responsable) { valor ->
                    viewModel.actualizarCampo { it.copy(responsable = valor) }
                }
            }

            /** -------- SECCIÓN 3: CLASIFICACIÓN TÉCNICA -------- */
            SeccionExpandible(titulo = "9Clasificación Técnica") {
                CampoTexto("Clasificación biomédica", equipo.clasificacionBiomedica) { valor ->
                    viewModel.actualizarCampo { it.copy(clasificacionBiomedica = valor) }
                }
                CampoTexto("Tecnología predominante", equipo.tecnologiaPredominante) { valor ->
                    viewModel.actualizarCampo { it.copy(tecnologiaPredominante = valor) }
                }
                CampoTexto("Clasificación riesgo biológico", equipo.clasificacionRiesgoBiologico) { valor ->
                    viewModel.actualizarCampo { it.copy(clasificacionRiesgoBiologico = valor) }
                }
            }

            /** -------- SECCIÓN 4: DATOS ELÉCTRICOS -------- */
            SeccionExpandible(titulo = "⚡ Datos Eléctricos") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    CampoNumero(
                        "Voltaje Máx (V)",
                        equipo.voltajeMax?.toString() ?: "",
                        { valor -> viewModel.actualizarCampo { it.copy(voltajeMax = valor.toDoubleOrNull()) } },
                        Modifier.weight(1f)
                    )
                    CampoNumero(
                        "Voltaje Mín (V)",
                        equipo.voltajeMin?.toString() ?: "",
                        { valor -> viewModel.actualizarCampo { it.copy(voltajeMin = valor.toDoubleOrNull()) } },
                        Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    CampoNumero(
                        "Corriente Máx (A)",
                        equipo.corrienteMax?.toString() ?: "",
                        { valor -> viewModel.actualizarCampo { it.copy(corrienteMax = valor.toDoubleOrNull()) } },
                        Modifier.weight(1f)
                    )
                    CampoNumero(
                        "Corriente Mín (A)",
                        equipo.corrienteMin?.toString() ?: "",
                        { valor -> viewModel.actualizarCampo { it.copy(corrienteMin = valor.toDoubleOrNull()) } },
                        Modifier.weight(1f)
                    )
                }
            }

            /** -------- ACCIONES -------- */
            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (equipo.fotoUri != null) "Cambiar foto" else "Agregar foto")
            }

            Button(
                onClick = {
                    viewModel.guardarEquipo()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Equipo guardado correctamente")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar equipo")
            }

            Button(
                onClick = {
                    val nombreArchivo = "Equipo_${equipo.nombreEquipo}_${System.currentTimeMillis()}.xlsx"
                    crearArchivoLauncher.launch(nombreArchivo)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Exportar a Excel")
            }

            exportacionExitosa?.let { mensaje ->
                Text(text = mensaje, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/** -------- COMPONENTES REUTILIZABLES -------- */

@Composable
fun CampoTexto(label: String, valor: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CampoNumero(
    label: String,
    valor: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
            .fillMaxWidth()
            .widthIn(min = 140.dp)
    )
}

/** Sección expandible */
@Composable
fun SeccionExpandible(titulo: String, contenido: @Composable () -> Unit) {
    var expandido by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            TextButton(onClick = { expandido = !expandido }) {
                Text(
                    text = if (expandido) "▼ $titulo" else "▶ $titulo",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            AnimatedVisibility(
                visible = expandido,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    contenido()
                }
            }
        }
    }
}

/** Carga la plantilla desde /res/raw */
fun cargarPlantilla(context: Context): InputStream? {
    return try {
        context.resources.openRawResource(com.example.views.R.raw.plantilla)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}