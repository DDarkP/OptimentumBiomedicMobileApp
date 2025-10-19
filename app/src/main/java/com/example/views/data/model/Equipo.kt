package com.example.views.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para representar un equipo biomédico.
 * Guardamos la foto como String (URI) para no almacenar binarios en la DB.
 */

@Entity(tableName = "equipos")
data class Equipo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombreEquipo: String = "",
    val informacionGeneral: String = "",
    val marca: String = "",
    val modelo: String = "",
    val serie: String = "",
    val tipo: String = "",
    val referencia: String = "",
    val codigoEquipo: String = "",
    val numeroInventario: String = "",
    val edificio: String = "",
    val area: String = "",
    val direccion: String = "",
    val ubicacion: String = "",
    val centroCostos: String = "",
    val responsable: String = "",
    val clasificacionBiomedica: String = "",
    val tecnologiaPredominante: String = "",
    val clasificacionRiesgoBiologico: String = "",
    val voltajeMax: Double? = null,
    val voltajeMin: Double? = null,
    val corrienteMax: Double? = null,
    val corrienteMin: Double? = null,

    val fotoUri: String? = null // opcional, si guardas una imagen
)
