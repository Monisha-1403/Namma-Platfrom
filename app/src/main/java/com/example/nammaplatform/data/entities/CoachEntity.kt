package com.example.nammaplatform.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coaches")
data class CoachEntity(
    @PrimaryKey(autoGenerate = true) val coachId: Long = 0,
    val trainId: String,
    val coachName: String,
    val coachType: String,
    val coachTypeKn: String = "",
    val seatAvailability: String,
    val crowdLevel: String,
    val platformPosition: String,
    val platformPositionKn: String = "",
    val boardingAdvice: String,
    val boardingAdviceKn: String = "",
    val isLadies: Boolean = false,
    val securityLevel: String? = "",
    val securityLevelKn: String? = "",
    val nearestPolice: String? = "",
    val nearestPoliceKn: String? = "",
    val nearestWashroom: String? = "",
    val nearestWashroomKn: String? = "",
    val safeZone: String? = "",
    val safeZoneKn: String? = ""
)
