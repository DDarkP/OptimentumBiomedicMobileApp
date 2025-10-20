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
                safeSetCellValue(sheet, 3, 9, equipo.nombreEquipo)                 // NOMBRE DEL EQUIPO
                safeSetCellValue(sheet, 5, 0, equipo.informacionGeneral)           // INFORMACION GENERAL
                safeSetCellValue(sheet, 6, 3, equipo.marca)                        // MARCA
                safeSetCellValue(sheet, 7, 3, equipo.modelo)                       // MODELO
                safeSetCellValue(sheet, 8, 3, equipo.serie)                        // SERIE
                safeSetCellValue(sheet, 9, 3, equipo.tipo)                        // TIPO
                safeSetCellValue(sheet, 6, 15, equipo.referencia)                  // REFERENCIA
                safeSetCellValue(sheet, 7, 15, equipo.codigoEquipo)                // CODIGO DEL EQUIPO
                safeSetCellValue(sheet, 8, 15, equipo.numeroInventario)            // No. INVENTARIO
                safeSetCellValue(sheet, 12, 3, equipo.edificio)                    // EDIFICIO
                safeSetCellValue(sheet, 13, 3, equipo.area)                        // AREA
                safeSetCellValue(sheet, 14, 3, equipo.direccion)                   // DIRECCION
                safeSetCellValue(sheet, 12, 18, equipo.ubicacion)                   // UBICACION
                safeSetCellValue(sheet, 13, 18, equipo.centroCostos)                // CENTRO DE COSTOS
                safeSetCellValue(sheet, 14, 18, equipo.responsable)                 // RESPONSABLE
                safeSetCellValue(sheet, 17 , 0, equipo.clasificacionBiomedica)      // CLASIFICACION BIOMEDICA
                safeSetCellValue(sheet, 17, 9, equipo.tecnologiaPredominante)      // TECNOLOGIA PREDOMINANTE
                safeSetCellValue(sheet, 17, 18, equipo.clasificacionRiesgoBiologico)// CLASIFICACION RIESGO BIOLOGICO
                safeSetCellValue(sheet, 21, 0, equipo.voltajeMax?.toString() ?: "")// VOLTAJE MAX
                safeSetCellValue(sheet, 21, 7, equipo.voltajeMin?.toString() ?: "")// VOLTAJE MIN
                safeSetCellValue(sheet, 21, 14, equipo.corrienteMax?.toString() ?: "")// CORRIENTE MAX
                safeSetCellValue(sheet, 21, 20, equipo.corrienteMin?.toString() ?: "")// CORRIENTE MIN

                // 🖼️ Insertar imagen (si existe)
                equipo.fotoUri?.let { uriString ->
                    try {
                        val inputStream = context.contentResolver.openInputStream(Uri.parse(uriString))
                        val bytes = inputStream?.readBytes()
                        inputStream?.close()

                        if (bytes != null) {
                            val pictureIdx = workbook.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_JPEG)
                            val helper = workbook.creationHelper
                            val drawing = sheet.createDrawingPatriarch()
                            val anchor = helper.createClientAnchor()

                            // 🔧 Ajusta la posición de la imagen (usa setters en lugar de asignar)
                            anchor.setCol1(19)  // columna H (indexada desde 0)
                            anchor.setRow1(6)  // fila 3
                            anchor.setCol2(26) // ancho de la imagen
                            anchor.setRow2(11) // alto de la imagen

                            val pict = drawing.createPicture(anchor, pictureIdx)
                            pict.resize() // ajusta el tamaño automáticamente
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

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
