package com.example.nammaplatform.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey(autoGenerate = true) val routeId: Long = 0,
    val trainId: String,
    val stationNameEnglish: String,
    val stationNameKannada: String,
    val arrival: String,
    val departure: String,
    val platform: String,
    val sequence: Int
)
