package com.example.nammaplatform.data.dao

import androidx.room.*
import com.example.nammaplatform.data.entities.CoachEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoachDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coaches: List<CoachEntity>)

    @Query("SELECT * FROM coaches WHERE trainId = :trainId ORDER BY coachId ASC")
    fun getCoachesForTrain(trainId: String): Flow<List<CoachEntity>>

    @Query("SELECT COUNT(*) FROM coaches")
    suspend fun getCount(): Int
}
