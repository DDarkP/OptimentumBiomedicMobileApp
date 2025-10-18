package com.example.views.ui.views

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    // 📸 Selector de imagen (foto del equipo)
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.actualizarFoto(uri)
    }

    // 📂 Lanzador para crear el archivo Excel donde el usuario elija
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
                        "✅ Archivo guardado correctamente",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "❌ No se encontró la plantilla Excel en recursos",
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
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Campos del formulario
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
            CampoTexto("Clasificación biomédica", equipo.clasificacionBiomedica) { valor ->
                viewModel.actualizarCampo { it.copy(clasificacionBiomedica = valor) }
            }
            CampoTexto("Tecnología predominante", equipo.tecnologiaPredominante) { valor ->
                viewModel.actualizarCampo { it.copy(tecnologiaPredominante = valor) }
            }
            CampoTexto("Riesgo biológico", equipo.clasificacionRiesgoBiologico) { valor ->
                viewModel.actualizarCampo { it.copy(clasificacionRiesgoBiologico = valor) }
            }
            CampoTexto("Riesgo eléctrico", equipo.clasificacionRiesgoElectrico) { valor ->
                viewModel.actualizarCampo { it.copy(clasificacionRiesgoElectrico = valor) }
            }
// Voltaje
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CampoNumero(
                    label = "Voltaje min (V)",
                    valor = equipo.voltajeMin?.toString() ?: "",
                    onChange = { valor ->
                        viewModel.actualizarCampo { it.copy(voltajeMin = valor.toDoubleOrNull()) }
                    },
                    modifier = Modifier.weight(1f)
                )

                CampoNumero(
                    label = "Voltaje máx (V)",
                    valor = equipo.voltajeMax?.toString() ?: "",
                    onChange = { valor ->
                        viewModel.actualizarCampo { it.copy(voltajeMax = valor.toDoubleOrNull()) }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

// Corriente
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CampoNumero(
                    label = "Corriente min (A)",
                    valor = equipo.corrienteMin?.toString() ?: "",
                    onChange = { valor ->
                        viewModel.actualizarCampo { it.copy(corrienteMin = valor.toDoubleOrNull()) }
                    },
                    modifier = Modifier.weight(1f)
                )

                CampoNumero(
                    label = "Corriente máx (A)",
                    valor = equipo.corrienteMax?.toString() ?: "",
                    onChange = { valor ->
                        viewModel.actualizarCampo { it.copy(corrienteMax = valor.toDoubleOrNull()) }
                    },
                    modifier = Modifier.weight(1f)
                )
            }


            CampoNumero(
                label = "Cantidad",
                valor = equipo.cantidad.toString(),
                onChange = { valor ->
                    viewModel.actualizarCampo { it.copy(cantidad = valor.toIntOrNull() ?: 1) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            CampoNumero(
                label = "Valor equipo",
                valor = equipo.valorEquipo?.toString() ?: "",
                onChange = { valor ->
                    viewModel.actualizarCampo { it.copy(valorEquipo = valor.toDoubleOrNull()) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            CampoNumero(
                label = "Valor mantenimiento",
                valor = equipo.valorMantenimiento?.toString() ?: "",
                onChange = { valor ->
                    viewModel.actualizarCampo { it.copy(valorMantenimiento = valor.toDoubleOrNull()) }
                },
                modifier = Modifier.fillMaxWidth()
            )


            // 📸 Botón para seleccionar foto
            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (equipo.fotoUri != null) "Cambiar foto" else "Agregar foto")
            }

            // 💾 Botón para guardar equipo
            Button(
                onClick = {
                    viewModel.guardarEquipo()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("✅ Equipo guardado correctamente")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar equipo")
            }

            // 📤 Botón para exportar Excel con diálogo de guardado
            Button(
                onClick = {
                    val nombreArchivo =
                        "Equipo_${equipo.marca}_${equipo.modelo}_${System.currentTimeMillis()}.xlsx"
                    crearArchivoLauncher.launch(nombreArchivo)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Exportar a Excel")
            }

            // 📋 Mostrar mensaje si se exportó correctamente
            exportacionExitosa?.let { mensaje ->
                Text(
                    text = mensaje,
                    style = MaterialTheme.typography.bodySmall
                )
            }
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
            .widthIn(min = 140.dp) // ancho mínimo para evitar que se encoja demasiado
    )
}

//@Composable
//fun CampoNumero(label: String, valor: String, onChange: (String) -> Unit) {
//    OutlinedTextField(
//        value = valor,
//        onValueChange = onChange,
//        label = { Text(label) },
//        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//        modifier = Modifier.fillMaxWidth()
//    )
//}

/** Carga la plantilla Excel desde /res/raw/ */
fun cargarPlantilla(context: Context): InputStream? {
    return try {
        context.resources.openRawResource(com.example.views.R.raw.plantilla)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

