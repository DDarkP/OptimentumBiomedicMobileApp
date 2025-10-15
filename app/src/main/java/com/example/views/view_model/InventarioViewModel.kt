package com.example.views.view_model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import com.example.views.data.model.Equipo
import com.example.views.data.repository.EquipoRepository

/**
 * ViewModel para manejar la lógica del inventario:
 * - Estados del formulario
 * - Inserción en la base de datos
 * - Exportación a Excel usando la plantilla.
 */
class InventarioViewModel(private val repository: EquipoRepository) : ViewModel() {

    // Estado del formulario (se puede conectar a Compose con collectAsState)
    private val _equipoActual = MutableStateFlow(Equipo())
    val equipoActual: StateFlow<Equipo> = _equipoActual

    // Estado del resultado de exportación
    private val _exportacionExitosa = MutableStateFlow<String?>(null)
    val exportacionExitosa: StateFlow<String?> = _exportacionExitosa

    /** Actualiza un campo específico del formulario */
    fun actualizarCampo(actualizar: (Equipo) -> Equipo) {
        _equipoActual.value = actualizar(_equipoActual.value)
    }

    /** Guarda el equipo actual en la base de datos */
    fun guardarEquipo() {
        viewModelScope.launch {
            repository.insertarEquipo(_equipoActual.value)
        }
    }

    /**
     * Exporta el equipo actual a un archivo Excel basado en la plantilla.
     * @param context Contexto de la app (para acceder a archivos).
     * @param plantillaStream InputStream de la plantilla Excel.
     */
    fun exportarAExcel(context: Context, plantillaStream: InputStream) {
        viewModelScope.launch {
            try {
                val equipo = _equipoActual.value
                val workbook = XSSFWorkbook(plantillaStream)
                val sheet = workbook.getSheetAt(0)

                // Escribir datos en la plantilla (ajusta las filas/columnas según tu formato)
                sheet.getRow(6)?.getCell(1)?.setCellValue(equipo.informacionGeneral)
                sheet.getRow(7)?.getCell(4)?.setCellValue(equipo.marca)
                sheet.getRow(8)?.getCell(4)?.setCellValue(equipo.modelo)
                sheet.getRow(9)?.getCell(4)?.setCellValue(equipo.serie)
                sheet.getRow(10)?.getCell(4 )?.setCellValue(equipo.clasificacionBiomedica)
                sheet.getRow(6)?.getCell(1)?.setCellValue(equipo.tecnologiaPredominante)
                sheet.getRow(7)?.getCell(1)?.setCellValue(equipo.clasificacionRiesgoBiologico)
                sheet.getRow(8)?.getCell(1)?.setCellValue(equipo.clasificacionRiesgoElectrico)
                sheet.getRow(9)?.getCell(1)?.setCellValue("${equipo.voltajeMin} - ${equipo.voltajeMax} V")
                sheet.getRow(10)?.getCell(1)?.setCellValue("${equipo.corrienteMin} - ${equipo.corrienteMax} A")
                sheet.getRow(11)?.getCell(1)?.setCellValue(equipo.cantidad.toString())
                sheet.getRow(12)?.getCell(1)?.setCellValue(equipo.valorEquipo?.toString() ?: "")
                sheet.getRow(13)?.getCell(1)?.setCellValue(equipo.valorMantenimiento?.toString() ?: "")

                // Guardar el nuevo archivo
                val outFile = File(
                    context.getExternalFilesDir(null),
                    "Equipo_${System.currentTimeMillis()}.xlsx"
                )
                FileOutputStream(outFile).use { workbook.write(it) }
                workbook.close()

                _exportacionExitosa.value = outFile.absolutePath

            } catch (e: Exception) {
                e.printStackTrace()
                _exportacionExitosa.value = null
            }
        }
    }

    /** Carga una imagen y actualiza el URI del equipo */
    fun actualizarFoto(uri: Uri?) {
        _equipoActual.value = _equipoActual.value.copy(fotoUri = uri?.toString())
    }
}
