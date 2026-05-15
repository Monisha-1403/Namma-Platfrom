package com.example.nammaplatform.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammaplatform.data.repository.DataRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DataRepository(application)

    fun initializeData() {
        viewModelScope.launch {
            repository.checkAndImportData()
        }
    }
}
