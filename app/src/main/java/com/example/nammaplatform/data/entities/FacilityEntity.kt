package com.example.nammaplatform.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "facilities")
data class FacilityEntity(
    @PrimaryKey(autoGenerate = true) val facilityId: Long = 0,
    val junctionId: String,
    val facilityName: String,
    val facilityNameKn: String,
    val location: String,
    val locationKn: String = "",
    val distance: String,
    val distanceKn: String = "",
    val availability: String,
    val availabilityKn: String = "",
    val description: String,
    val descriptionKn: String = ""
)
