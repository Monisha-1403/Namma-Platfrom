package com.example.nammaplatform.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammaplatform.data.entities.FeedbackEntity
import com.example.nammaplatform.data.repository.DataRepository
import kotlinx.coroutines.launch

class FeedbackViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DataRepository(application)

    fun submitFeedback(rating: Float, comment: String) {
        viewModelScope.launch {
            repository.insertFeedback(FeedbackEntity(rating = rating, feedbackText = comment))
        }
    }
}
