package com.example.views.data.repository

import kotlinx.coroutines.flow.Flow
import com.example.views.data.dao.EquipoDao
import com.example.views.data.model.Equipo

/**
 * Repositorio que maneja la lógica de acceso a datos
 * entre el ViewModel y la base de datos Room.
 */
class EquipoRepository(private val equipoDao: EquipoDao) {

    /** Inserta un nuevo equipo y devuelve su ID generado. */
    suspend fun insertarEquipo(equipo: Equipo): Long {
        return equipoDao.insertEquipo(equipo)
    }

    /** Actualiza un equipo existente. */
    suspend fun actualizarEquipo(equipo: Equipo) {
        equipoDao.updateEquipo(equipo)
    }

    /** Elimina un equipo. */
    suspend fun eliminarEquipo(equipo: Equipo) {
        equipoDao.deleteEquipo(equipo)
    }

    /** Obtiene todos los equipos como un flujo observable. */
    fun obtenerTodosLosEquipos(): Flow<List<Equipo>> {
        return equipoDao.getAllEquipos()
    }

    /** Busca un equipo por ID. */
    suspend fun obtenerEquipoPorId(id: Long): Equipo? {
        return equipoDao.getEquipoById(id)
    }
}
