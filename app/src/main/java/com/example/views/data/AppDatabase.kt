package com.example.views.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.views.data.dao.UsuarioDao
import com.example.views.data.model.Usuario

@Database(entities = [Usuario::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
}
