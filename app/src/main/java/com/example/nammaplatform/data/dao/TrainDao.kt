package com.example.nammaplatform.data.dao

import androidx.room.*
import com.example.nammaplatform.data.entities.TrainEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trains: List<TrainEntity>)

    @Query("SELECT * FROM trains WHERE junctionId = :junctionId")
    fun getTrainsForJunction(junctionId: String): Flow<List<TrainEntity>>

    @Query("SELECT * FROM trains WHERE trainId = :trainId LIMIT 1")
    suspend fun getTrainById(trainId: String): TrainEntity?

    @Query("SELECT * FROM trains")
    fun getAllTrains(): Flow<List<TrainEntity>>

    @Query("SELECT COUNT(*) FROM trains")
    suspend fun getCount(): Int
}
