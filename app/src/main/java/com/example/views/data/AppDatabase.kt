package com.example.views.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.views.data.dao.EquipoDao
import com.example.views.data.dao.UsuarioDao
import com.example.views.data.model.Usuario
import com.example.views.data.model.Equipo
//import org.apache.poi.sl.draw.geom.Context

//@Database(entities = [Usuario::class], version = 1)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun usuarioDao(): UsuarioDao
//}
@Database(
    entities = [Usuario::class, Equipo::class],
    version = 2, // cambia a 2 para recrear la DB limpia
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun equipoDao(): EquipoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context:  Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "equipos_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
