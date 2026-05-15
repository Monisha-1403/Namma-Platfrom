package com.example.nammaplatform.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.nammaplatform.data.repository.DataRepository
import com.example.nammaplatform.models.TrainModel
import kotlinx.coroutines.flow.map
import kotlin.math.absoluteValue

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DataRepository(application)
    
    val allJunctions = repository.getAllJunctions().asLiveData()

    private val selectedJunctionId = MutableLiveData<String?>()

    val selectedJunction = selectedJunctionId.switchMap { id ->
        if (id == null) MutableLiveData(null)
        else repository.getAllJunctions().asLiveData().map { list ->
            list.find { it.junctionId == id }
        }
    }
    
    val filteredTrains: LiveData<List<TrainModel>> = selectedJunctionId.switchMap { junctionId ->
        if (junctionId == null) MutableLiveData(emptyList())
        else {
            repository.getTrainsForJunction(junctionId).asLiveData().map { entities ->
                entities.map { entity ->
                    TrainModel(
                        trainId = entity.trainId,
                        trainNumber = entity.trainNumber,
                        trainNameEnglish = entity.trainNameEnglish,
                        trainNameKannada = entity.trainNameKannada,
                        platformNumber = entity.platform,
                        arrivalTime = entity.arrivalTime,
                        status = entity.status,
                        isDelayed = entity.status.equals("Delayed", ignoreCase = true)
                    )
                }
            }
        }
    }

    val announcements = selectedJunctionId.switchMap { junctionId ->
        if (junctionId == null) MutableLiveData(emptyList())
        else repository.getAnnouncements(junctionId).asLiveData()
    }

    val nextThreeTrains: LiveData<List<TrainModel>> = filteredTrains.map { trains ->
        trains.take(3)
    }

    fun selectJunction(junctionId: String) {
        selectedJunctionId.value = junctionId
    }

    fun getSelectedJunctionId(): LiveData<String?> = selectedJunctionId
}
