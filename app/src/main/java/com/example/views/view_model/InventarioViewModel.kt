package com.example.views.view_model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.views.data.model.Equipo
import com.example.views.data.repository.EquipoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.io.OutputStream

/**
 * ViewModel para manejar la lógica del inventario:
 * - Estados del formulario
 * - Inserción en la base de datos
 * - Exportación a Excel usando la plantilla con celdas combinadas.
 */
class InventarioViewModel(private val repository: EquipoRepository) : ViewModel() {

    // Estado del formulario
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
     * Exporta los datos del equipo actual a un archivo Excel basado en la plantilla,
     * detectando celdas combinadas para escribir correctamente.
     */
    fun exportarAExcelAUri(context: Context, plantillaStream: InputStream, outputStream: OutputStream) {
        viewModelScope.launch {
            try {
                val equipo = _equipoActual.value
                val workbook = XSSFWorkbook(plantillaStream)
                val sheet = workbook.getSheetAt(0)

                // 🧩 Mapeo de campos -> posiciones (fila, columna)
                safeSetCellValue(sheet, 5, 3, equipo.nombreEquipo)                 // NOMBRE DEL EQUIPO
                safeSetCellValue(sheet, 6, 3, equipo.informacionGeneral)           // INFORMACION GENERAL
                safeSetCellValue(sheet, 7, 3, equipo.marca)                        // MARCA
                safeSetCellValue(sheet, 8, 3, equipo.modelo)                       // MODELO
                safeSetCellValue(sheet, 9, 3, equipo.serie)                        // SERIE
                safeSetCellValue(sheet, 10, 3, equipo.tipo)                        // TIPO
                safeSetCellValue(sheet, 11, 3, equipo.referencia)                  // REFERENCIA
                safeSetCellValue(sheet, 12, 3, equipo.codigoEquipo)                // CODIGO DEL EQUIPO
                safeSetCellValue(sheet, 13, 3, equipo.numeroInventario)            // No. INVENTARIO
                safeSetCellValue(sheet, 14, 3, equipo.edificio)                    // EDIFICIO
                safeSetCellValue(sheet, 15, 3, equipo.area)                        // AREA
                safeSetCellValue(sheet, 16, 3, equipo.direccion)                   // DIRECCION
                safeSetCellValue(sheet, 17, 3, equipo.ubicacion)                   // UBICACION
                safeSetCellValue(sheet, 18, 3, equipo.centroCostos)                // CENTRO DE COSTOS
                safeSetCellValue(sheet, 19, 3, equipo.responsable)                 // RESPONSABLE
                safeSetCellValue(sheet, 20, 3, equipo.clasificacionBiomedica)      // CLASIFICACION BIOMEDICA
                safeSetCellValue(sheet, 21, 3, equipo.tecnologiaPredominante)      // TECNOLOGIA PREDOMINANTE
                safeSetCellValue(sheet, 22, 3, equipo.clasificacionRiesgoBiologico)// CLASIFICACION RIESGO BIOLOGICO
                safeSetCellValue(sheet, 23, 3, equipo.voltajeMax?.toString() ?: "")// VOLTAJE MAX
                safeSetCellValue(sheet, 24, 3, equipo.voltajeMin?.toString() ?: "")// VOLTAJE MIN
                safeSetCellValue(sheet, 25, 3, equipo.corrienteMax?.toString() ?: "")// CORRIENTE MAX
                safeSetCellValue(sheet, 26, 3, equipo.corrienteMin?.toString() ?: "")// CORRIENTE MIN

                // 🗂️ Guardar archivo
                workbook.write(outputStream)
                workbook.close()

                _exportacionExitosa.value = "✅ Archivo exportado correctamente con los datos del equipo."

            } catch (e: Exception) {
                e.printStackTrace()
                _exportacionExitosa.value = "❌ Error al exportar: ${e.message}"
            }
        }
    }

    /** Carga una imagen y actualiza el URI del equipo */
    fun actualizarFoto(uri: Uri?) {
        _equipoActual.value = _equipoActual.value.copy(fotoUri = uri?.toString())
    }

    /**
     * Escribe un valor en una celda, detectando si pertenece a un rango combinado.
     * Si es así, escribe en la celda superior izquierda del rango.
     */
    private fun safeSetCellValue(sheet: Sheet, rowIndex: Int, colIndex: Int, value: String?) {
        val mergedRegions: List<CellRangeAddress> = sheet.mergedRegions

        // Buscar si la celda pertenece a un rango combinado
        val mergedRegion = mergedRegions.firstOrNull { it.isInRange(rowIndex, colIndex) }

        val targetRowIndex: Int
        val targetColIndex: Int

        if (mergedRegion != null) {
            targetRowIndex = mergedRegion.firstRow
            targetColIndex = mergedRegion.firstColumn
        } else {
            targetRowIndex = rowIndex
            targetColIndex = colIndex
        }

        val row = sheet.getRow(targetRowIndex) ?: sheet.createRow(targetRowIndex)
        val cell = row.getCell(targetColIndex) ?: row.createCell(targetColIndex)
        cell.setCellValue(value ?: "")
    }
}
