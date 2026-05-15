package com.example.nammaplatform.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.nammaplatform.data.entities.CoachEntity
import com.example.nammaplatform.data.repository.DataRepository
import kotlinx.coroutines.launch

class CoachViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DataRepository(application)
    
    private val _trainId = MutableLiveData<String?>()

    val filteredCoaches: LiveData<List<CoachEntity>> = _trainId.switchMap { id ->
        if (id == null) MutableLiveData(emptyList())
        else {
            val liveData = repository.getCoachesForTrain(id).asLiveData()
            liveData.switchMap { coaches ->
                if (coaches.isEmpty()) {
                    // Fallback logic to prevent blank screen
                    val fallback = MutableLiveData<List<CoachEntity>>()
                    viewModelScope.launch {
                        fallback.postValue(listOf(
                            CoachEntity(
                                trainId = id, 
                                coachName = "ENGINE", 
                                coachType = "Locomotive", 
                                coachTypeKn = "ಎಂಜಿನ್", 
                                seatAvailability = "0",
                                crowdLevel = "LOW",
                                platformPosition = "Front", 
                                platformPositionKn = "ಮುಂಭಾಗ", 
                                boardingAdvice = "Restricted", 
                                boardingAdviceKn = "ನಿರ್ಬಂಧಿಸಲಾಗಿದೆ"
                            ),
                            CoachEntity(
                                trainId = id, 
                                coachName = "GEN", 
                                coachType = "General", 
                                coachTypeKn = "ಸಾಮಾನ್ಯ", 
                                seatAvailability = "10", 
                                crowdLevel = "HEAVY", 
                                platformPosition = "Middle", 
                                platformPositionKn = "ಮಧ್ಯಭಾಗ", 
                                boardingAdvice = "Stand in line", 
                                boardingAdviceKn = "ಸಾಲಿನಲ್ಲಿ ನಿಲ್ಲಿ"
                            ),
                            CoachEntity(
                                trainId = id, 
                                coachName = "LADIES", 
                                coachType = "Ladies Reserved", 
                                coachTypeKn = "ಮಹಿಳಾ ಮೀಸಲು", 
                                seatAvailability = "15", 
                                crowdLevel = "LOW", 
                                platformPosition = "Front", 
                                platformPositionKn = "ಮುಂಭಾಗ", 
                                boardingAdvice = "Safe zone",
                                boardingAdviceKn = "ಸುರಕ್ಷಿತ ವಲಯ",
                                isLadies = true, 
                                securityLevel = "High", 
                                securityLevelKn = "ಹೆಚ್ಚು"
                            )
                        ))
                    }
                    fallback
                } else {
                    MutableLiveData(coaches)
                }
            }
        }
    }

    fun setTrainId(id: String?) {
        _trainId.value = id
    }
}
