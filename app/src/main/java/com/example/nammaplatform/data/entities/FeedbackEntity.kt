package com.example.nammaplatform.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback")
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rating: Float,
    val feedbackText: String,
    val timestamp: Long = System.currentTimeMillis()
)
