package com.example.nammaplatform.data.dao

import androidx.room.*
import com.example.nammaplatform.data.entities.RouteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(routes: List<RouteEntity>)

    @Query("SELECT * FROM routes WHERE trainId = :trainId ORDER BY sequence ASC")
    fun getRouteForTrain(trainId: String): Flow<List<RouteEntity>>

    @Query("SELECT COUNT(*) FROM routes")
    suspend fun getCount(): Int
}
