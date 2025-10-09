package com.example.views.data.repository

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.views.data.AppDatabase
import com.example.views.data.model.Usuario

class UsuarioRepository(context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "mi_app_interna.db"
    ).build()

    private val usuarioDao = db.usuarioDao()

    suspend fun login(email: String, password: String): Usuario? {
        return usuarioDao.login(email, password)
    }

    suspend fun registrar(usuario: Usuario) {
        usuarioDao.registrar(usuario)
        Log.d("RoomDebug", "Usuario registrado: $usuario")
    }
}
