package com.example.nammaplatform.models

data class FacilityModel(
    val name: String,
    val iconRes: Int,
    val location: String,
    val distance: String = "30 meters",
    val availability: String = "24/7",
    val description: String = "",
    var isExpanded: Boolean = false
)
