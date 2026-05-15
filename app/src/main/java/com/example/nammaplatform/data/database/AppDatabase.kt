package com.example.nammaplatform.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nammaplatform.data.dao.*
import com.example.nammaplatform.data.entities.*

@Database(
    entities = [
        JunctionEntity::class,
        TrainEntity::class,
        CoachEntity::class,
        RouteEntity::class,
        FacilityEntity::class,
        FeedbackEntity::class,
        AnnouncementEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun junctionDao(): JunctionDao
    abstract fun trainDao(): TrainDao
    abstract fun coachDao(): CoachDao
    abstract fun routeDao(): RouteDao
    abstract fun facilityDao(): FacilityDao
    abstract fun feedbackDao(): FeedbackDao
    abstract fun announcementDao(): AnnouncementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "namma_platform_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
