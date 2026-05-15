package com.example.nammaplatform.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trains")
data class TrainEntity(
    @PrimaryKey val trainId: String,
    val trainNameEnglish: String,
    val trainNameKannada: String,
    val trainNumber: String,
    val platform: String,
    val arrivalTime: String,
    val departureTime: String,
    val junctionId: String,
    val status: String
)
