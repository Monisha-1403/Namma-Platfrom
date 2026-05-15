package com.example.nammaplatform.models

data class SeatModel(
    val number: String,
    val isAvailable: Boolean,
    var isSelected: Boolean = false
)
