package com.example.nammaplatform.models

data class CoachModel(
    val name: String,
    val type: String,
    val typeKn: String = "",
    val colorRes: Int,
    val seatsAvailable: String = "45",
    val crowdIntensity: CrowdLevel = CrowdLevel.LOW,
    val entryPosition: String = "Middle of Platform 2",
    val entryPositionKn: String = "",
    val nearestExit: String = "Foot Over Bridge",
    val walkingDirection: String = "Move 20 meters left",
    val boardingTip: String = "Stand near yellow coach marking",
    val boardingTipKn: String = "",
    val arrivalPosition: String = "Near middle staircase",
    val facilities: String = "✓ Charging point, ✓ Fan, ✓ Reserved seating",
    val isLadies: Boolean = false,
    val securityLevel: String = "High",
    val securityLevelKn: String = "",
    val nearestPolice: String = "Platform 2 North Side",
    val nearestPoliceKn: String = "",
    val nearestWashroom: String = "15 meters away",
    val nearestWashroomKn: String = "",
    val safeZone: String = "Safe Area",
    val safeZoneKn: String = ""
)

enum class CrowdLevel {
    LOW, MEDIUM, HEAVY
}
