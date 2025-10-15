package com.example.views.view_model


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.views.data.repository.EquipoRepository

class InventarioViewModelFactory(
    private val repository: EquipoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventarioViewModel::class.java)) {
            return InventarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
