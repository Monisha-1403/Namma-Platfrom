package com.example.nammaplatform.models

data class TrainModel(
    val trainId: String,
    val trainNumber: String,
    val trainNameEnglish: String,
    val trainNameKannada: String,
    val platformNumber: String,
    val arrivalTime: String,
    val status: String, // "On Time" or "Delayed"
    val isDelayed: Boolean
)
