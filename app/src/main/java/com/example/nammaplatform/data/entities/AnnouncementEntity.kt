package com.example.nammaplatform.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val announcementId: String,
    val junctionId: String,
    val messageEn: String,
    val messageKn: String,
    val type: String, // "Delay", "PlatformChange", "NextTrain", "General"
    val timestamp: Long = System.currentTimeMillis()
)
