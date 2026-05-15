package com.example.nammaplatform.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.nammaplatform.data.entities.RouteEntity
import com.example.nammaplatform.data.repository.DataRepository
import kotlinx.coroutines.launch

class RouteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DataRepository(application)
    
    private val _trainId = MutableLiveData<String?>()

    val filteredRoute: LiveData<List<RouteEntity>> = _trainId.switchMap { id ->
        if (id == null) MutableLiveData(emptyList())
        else {
            val liveData = repository.getRouteForTrain(id).asLiveData()
            liveData.switchMap { routes ->
                if (routes.isEmpty()) {
                    // Fallback logic
                    val fallback = MutableLiveData<List<RouteEntity>>()
                    viewModelScope.launch {
                        val train = repository.getTrainById(id)
                        if (train != null) {
                            val junction = repository.getJunctionById(train.junctionId)
                            val junctionNameEn = junction?.name ?: "Origin"
                            val junctionNameKn = junction?.nameKn ?: "ಮೂಲ"
                            fallback.postValue(listOf(
                                RouteEntity(trainId = id, stationNameEnglish = junctionNameEn, stationNameKannada = junctionNameKn, arrival = "-", departure = train.departureTime, platform = train.platform, sequence = 1),
                                RouteEntity(trainId = id, stationNameEnglish = "Destination", stationNameKannada = "ಗಮ್ಯಸ್ಥಾನ", arrival = "TBD", departure = "-", platform = "1", sequence = 2)
                            ))
                        }
                    }
                    fallback
                } else {
                    MutableLiveData(routes)
                }
            }
        }
    }

    fun setTrainId(id: String?) {
        _trainId.value = id
    }
}
