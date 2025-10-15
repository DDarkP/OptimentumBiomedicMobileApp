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
    val id: Long = 0L,

    @ColumnInfo(name = "info_general")
    val informacionGeneral: String = "",

    @ColumnInfo(name = "marca")
    val marca: String = "",

    @ColumnInfo(name = "modelo")
    val modelo: String = "",

    @ColumnInfo(name = "serie")
    val serie: String = "",

    @ColumnInfo(name = "clas_biomedica")
    val clasificacionBiomedica: String = "",

    @ColumnInfo(name = "tec_predominante")
    val tecnologiaPredominante: String = "",

    @ColumnInfo(name = "riesgo_biologico")
    val clasificacionRiesgoBiologico: String = "",

    @ColumnInfo(name = "foto_uri")
    val fotoUri: String? = null,

    @ColumnInfo(name = "riesgo_electrico")
    val clasificacionRiesgoElectrico: String = "",

    @ColumnInfo(name = "voltaje_min")
    val voltajeMin: Double? = null,

    @ColumnInfo(name = "voltaje_max")
    val voltajeMax: Double? = null,

    @ColumnInfo(name = "corriente_min")
    val corrienteMin: Double? = null,

    @ColumnInfo(name = "corriente_max")
    val corrienteMax: Double? = null,

    @ColumnInfo(name = "cantidad")
    val cantidad: Int = 1,

    @ColumnInfo(name = "valor_equipo")
    val valorEquipo: Double? = null,

    @ColumnInfo(name = "valor_mantenimiento")
    val valorMantenimiento: Double? = null
)
