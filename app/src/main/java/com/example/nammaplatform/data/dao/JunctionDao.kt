package com.example.nammaplatform.data.dao

import androidx.room.*
import com.example.nammaplatform.data.entities.JunctionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JunctionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(junctions: List<JunctionEntity>)

    @Query("SELECT * FROM junctions")
    fun getAllJunctions(): Flow<List<JunctionEntity>>

    @Query("SELECT * FROM junctions WHERE junctionId = :junctionId LIMIT 1")
    suspend fun getJunctionById(junctionId: String): JunctionEntity?

    @Query("SELECT COUNT(*) FROM junctions")
    suspend fun getCount(): Int
}
