package com.example.nammaplatform.data.dao

import androidx.room.*
import com.example.nammaplatform.data.entities.FacilityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FacilityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(facilities: List<FacilityEntity>)

    @Query("SELECT * FROM facilities WHERE junctionId = :junctionId")
    fun getFacilitiesByJunction(junctionId: String): Flow<List<FacilityEntity>>

    @Query("SELECT COUNT(*) FROM facilities")
    suspend fun getCount(): Int
}
