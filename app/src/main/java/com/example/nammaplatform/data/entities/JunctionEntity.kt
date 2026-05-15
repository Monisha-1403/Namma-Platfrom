package com.example.nammaplatform.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "junctions")
data class JunctionEntity(
    @PrimaryKey val junctionId: String,
    val name: String,
    val nameKn: String,
    val code: String,
    val city: String
)
