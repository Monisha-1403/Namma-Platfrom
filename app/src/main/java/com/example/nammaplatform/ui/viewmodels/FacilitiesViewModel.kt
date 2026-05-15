package com.example.nammaplatform.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.nammaplatform.data.repository.DataRepository

class FacilitiesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DataRepository(application)
    
    private val junctionId = MutableLiveData<String?>()

    val filteredFacilities = junctionId.switchMap { id ->
        if (id == null) MutableLiveData(emptyList())
        else repository.getFacilitiesByJunction(id).asLiveData()
    }

    fun setJunctionId(id: String?) {
        junctionId.value = id
    }
}
