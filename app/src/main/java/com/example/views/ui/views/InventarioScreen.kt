package com.example.views.ui.views

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.views.data.AppDatabase
import com.example.views.data.model.Equipo
import com.example.views.data.repository.EquipoRepository
import com.example.views.view_model.InventarioViewModel
import com.example.views.view_model.InventarioViewModelFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.InputStream


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen() {
    val context = LocalContext.current

    // Instancia del repositorio y base de datos
    val db = AppDatabase.getDatabase(context)
    val repository = EquipoRepository(db.equipoDao())

    // ✅ Crea el ViewModel correctamente con el factory
    val viewModel: InventarioViewModel = viewModel(
        factory = InventarioViewModelFactory(repository)
    )

    val equipo by viewModel.equipoActual.collectAsState()
    val exportacionExitosa by viewModel.exportacionExitosa.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Selector de imagen (foto del equipo)
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.actualizarFoto(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Registro de Equipo Biomédico") })
        }, snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Campos del formulario
            CampoTexto("Información general", equipo.informacionGeneral) { valor ->
                viewModel.actualizarCampo { equipoActual -> equipoActual.copy(informacionGeneral = valor) }
            }
            CampoTexto("Marca", equipo.marca) { valor ->
                viewModel.actualizarCampo { equipoActual -> equipoActual.copy(marca = valor) }
            }
            CampoTexto("Modelo", equipo.modelo) { valor ->
                viewModel.actualizarCampo { equipoActual -> equipoActual.copy(modelo = valor) }
            }
            CampoTexto("Serie", equipo.serie) { valor ->
                viewModel.actualizarCampo { equipoActual -> equipoActual.copy(serie = valor) }
            }
            CampoTexto("Clasificación biomédica", equipo.clasificacionBiomedica) { valor ->
                viewModel.actualizarCampo { equipoActual -> equipoActual.copy(clasificacionBiomedica = valor) }
            }
            CampoTexto("Tecnología predominante", equipo.tecnologiaPredominante) { valor ->
                viewModel.actualizarCampo { equipoActual -> equipoActual.copy(tecnologiaPredominante = valor) }
            }
            CampoTexto("Riesgo biológico", equipo.clasificacionRiesgoBiologico) { valor ->
                viewModel.actualizarCampo { equipoActual ->
                    equipoActual.copy(
                        clasificacionRiesgoBiologico = valor
                    )
                }
            }
            CampoTexto("Riesgo eléctrico", equipo.clasificacionRiesgoElectrico) { valor ->
                viewModel.actualizarCampo { equipoActual ->
                    equipoActual.copy(
                        clasificacionRiesgoElectrico = valor
                    )
                }
            }


            // Voltaje
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoNumero(
                    "Voltaje min (V)",
                    equipo.voltajeMin?.toString() ?: ""
                ) { valor: String ->
                    viewModel.actualizarCampo { eq -> eq.copy(voltajeMin = valor.toDoubleOrNull()) }
                }
                CampoNumero(
                    "Voltaje máx (V)",
                    equipo.voltajeMax?.toString() ?: ""
                ) { valor: String ->
                    viewModel.actualizarCampo { eq -> eq.copy(voltajeMax = valor.toDoubleOrNull()) }
                }
            }

            // Corriente
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoNumero(
                    "Corriente min (A)",
                    equipo.corrienteMin?.toString() ?: ""
                ) { valor: String ->
                    viewModel.actualizarCampo { eq -> eq.copy(corrienteMin = valor.toDoubleOrNull()) }
                }
                CampoNumero(
                    "Corriente máx (A)",
                    equipo.corrienteMax?.toString() ?: ""
                ) { valor: String ->
                    viewModel.actualizarCampo { eq -> eq.copy(corrienteMax = valor.toDoubleOrNull()) }
                }
            }

            CampoNumero("Cantidad", equipo.cantidad.toString()) { valor: String ->
                viewModel.actualizarCampo { eq -> eq.copy(cantidad = valor.toIntOrNull() ?: 1) }
            }
            CampoNumero("Valor equipo", equipo.valorEquipo?.toString() ?: "") { valor: String ->
                viewModel.actualizarCampo { eq -> eq.copy(valorEquipo = valor.toDoubleOrNull()) }
            }
            CampoNumero(
                "Valor mantenimiento",
                equipo.valorMantenimiento?.toString() ?: ""
            ) { valor: String ->
                viewModel.actualizarCampo { eq -> eq.copy(valorMantenimiento = valor.toDoubleOrNull()) }
            }

            // Botón para seleccionar foto
            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (equipo.fotoUri != null) "Cambiar foto" else "Agregar foto")
            }

            // Botones de acción
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
                    val plantilla = cargarPlantilla(context)
                    if (plantilla != null) {
                        viewModel.exportarAExcel(context, plantilla)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                "✅ Archivo Excel generado correctamente",
                                withDismissAction = true
                            )
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                "❌ No se encontró la plantilla Excel en assets/",
                                withDismissAction = true
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Exportar a Excel")
            }

            // Mostrar ruta si la exportación fue exitosa
            exportacionExitosa?.let { ruta ->
                Text(
                    text = "📂 Archivo generado en:\n$ruta",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Mostrar resultado de exportación
        exportacionExitosa?.let { ruta ->
            Text(
                text = "Archivo generado en:\n$ruta",
                style = MaterialTheme.typography.bodySmall
            )
        }

    }
}

/** Reutilizable para campos de texto */
@Composable
fun CampoTexto(label: String, valor: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}

/** Campo numérico (acepta solo números y decimales) */
@Composable
fun CampoNumero(label: String, valor: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(1f)
    )
}

fun cargarPlantilla(context: Context): InputStream? {
    return try {
        context.resources.openRawResource(
            com.example.views.R.raw.plantilla  // usa el nombre sin extensión
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

