package com.example.views.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.views.data.model.Equipo

/**
 * DAO para operaciones CRUD sobre la tabla "equipos".
 * Usa Flow para observar los cambios en tiempo real.
 */
@Dao
interface EquipoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipo(equipo: Equipo): Long

    @Update
    suspend fun updateEquipo(equipo: Equipo)

    @Delete
    suspend fun deleteEquipo(equipo: Equipo)

    @Query("SELECT * FROM equipos ORDER BY id DESC")
    fun getAllEquipos(): Flow<List<Equipo>>

    @Query("SELECT * FROM equipos WHERE id = :id")
    suspend fun getEquipoById(id: Long): Equipo?
}

