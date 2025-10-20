package com.example.views.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.views.data.dao.EquipoDao
import com.example.views.data.dao.UsuarioDao
import com.example.views.data.model.Equipo
import com.example.views.data.model.Usuario

/**
 * Base de datos Room que maneja las entidades de la aplicación.
 * Incluye las tablas: Usuario y Equipo.
 */
@Database(
    entities = [Usuario::class, Equipo::class],
    version = 2, // Incrementado para reflejar los nuevos cambios del modelo
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun equipoDao(): EquipoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Devuelve una instancia única (singleton) de la base de datos.
         * Si no existe, la crea.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "equipos_db" // Nombre del archivo .db en almacenamiento interno
                )
                    // Destruye y recrea la BD automáticamente si el esquema cambia
                    // (solo usar en desarrollo, para producción se crean migraciones reales)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
