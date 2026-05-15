package com.example.nammaplatform.data.dao

import androidx.room.*
import com.example.nammaplatform.data.entities.AnnouncementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(announcements: List<AnnouncementEntity>)

    @Query("SELECT * FROM announcements WHERE junctionId = :junctionId ORDER BY timestamp DESC")
    fun getAnnouncementsForJunction(junctionId: String): Flow<List<AnnouncementEntity>>

    @Query("SELECT COUNT(*) FROM announcements")
    suspend fun getCount(): Int
}
