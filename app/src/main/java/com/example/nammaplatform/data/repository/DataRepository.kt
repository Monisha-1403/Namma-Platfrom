package com.example.nammaplatform.data.repository

import android.content.Context
import android.util.Log
import com.example.nammaplatform.data.database.AppDatabase
import com.example.nammaplatform.data.entities.*
import com.example.nammaplatform.utils.FirebaseHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val gson = Gson()
    private val TAG = "DATA_REPO"

    suspend fun checkAndImportData() = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "DB_INIT_START: Checking database status...")
            
            // Local JSON Import as first-time fallback
            if (db.junctionDao().getCount() == 0) {
                Log.d(TAG, "DB_INIT: Junctions empty, importing from assets...")
                importJunctions()
            }
            
            if (db.trainDao().getCount() == 0) {
                Log.d(TAG, "DB_INIT: Trains empty, importing from assets...")
                importTrains()
            }
            
            if (db.coachDao().getCount() == 0) {
                Log.d(TAG, "DB_INIT: Coaches empty, importing from assets...")
                importCoaches()
            }
            
            if (db.routeDao().getCount() == 0) {
                Log.d(TAG, "DB_INIT: Routes empty, importing from assets...")
                importRoutes()
            }
            
            if (db.facilityDao().getCount() < 80) { 
                Log.d(TAG, "DB_INIT: Facilities low or empty, importing from assets...")
                importFacilities()
            }
            
            if (db.announcementDao().getCount() == 0) {
                Log.d(TAG, "DB_INIT: Announcements empty, importing from assets...")
                importAnnouncements()
            }

            Log.d(TAG, "DB_INIT_SUCCESS: Local data check complete.")

            // Firebase Sync to update local cache
            Log.d(TAG, "FIREBASE_SYNC_TRIGGER: Attempting Firebase sync...")
            FirebaseHelper.syncDataFromFirestore(db)
            
        } catch (e: Exception) {
            Log.e(TAG, "CRITICAL_ERROR: Data initialization failed", e)
        }
    }

    private suspend fun importJunctions() {
        try {
            val json = context.assets.open("junctions.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<JunctionEntity>>() {}.type
            val junctions: List<JunctionEntity> = gson.fromJson(json, listType)
            db.junctionDao().insertAll(junctions)
            Log.d(TAG, "JUNCTION_INSERT_SUCCESS: ${junctions.size} items")
        } catch (e: Exception) {
            Log.e(TAG, "Error importing junctions", e)
        }
    }

    private suspend fun importTrains() {
        try {
            val json = context.assets.open("trains.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<TrainEntity>>() {}.type
            val trains: List<TrainEntity> = gson.fromJson(json, listType)
            db.trainDao().insertAll(trains)
            Log.d(TAG, "TRAIN_INSERT_SUCCESS: ${trains.size} items")
        } catch (e: Exception) {
            Log.e(TAG, "Error importing trains", e)
        }
    }

    private suspend fun importCoaches() {
        try {
            val json = context.assets.open("coaches.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<CoachEntity>>() {}.type
            val coaches: List<CoachEntity> = gson.fromJson(json, listType)
            db.coachDao().insertAll(coaches)
            Log.d(TAG, "COACH_INSERT_SUCCESS: ${coaches.size} items")
        } catch (e: Exception) {
            Log.e(TAG, "Error importing coaches", e)
        }
    }

    private suspend fun importRoutes() {
        try {
            val json = context.assets.open("routes.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<RouteEntity>>() {}.type
            val routes: List<RouteEntity> = gson.fromJson(json, listType)
            db.routeDao().insertAll(routes)
            Log.d(TAG, "ROUTE_INSERT_SUCCESS: ${routes.size} items")
        } catch (e: Exception) {
            Log.e(TAG, "Error importing routes", e)
        }
    }

    private suspend fun importFacilities() {
        try {
            val json = context.assets.open("facilities.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<FacilityEntity>>() {}.type
            val facilities: List<FacilityEntity> = gson.fromJson(json, listType)
            db.facilityDao().insertAll(facilities)
            Log.d(TAG, "FACILITY_INSERT_SUCCESS: ${facilities.size} items")
        } catch (e: Exception) {
            Log.e(TAG, "Error importing facilities", e)
        }
    }

    private suspend fun importAnnouncements() {
        try {
            val json = context.assets.open("announcements.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<AnnouncementEntity>>() {}.type
            val announcements: List<AnnouncementEntity> = gson.fromJson(json, listType)
            db.announcementDao().insertAll(announcements)
            Log.d(TAG, "ANNOUNCEMENT_INSERT_SUCCESS: ${announcements.size} items")
        } catch (e: Exception) {
            Log.e(TAG, "Error importing announcements", e)
        }
    }

    fun getAllJunctions() = db.junctionDao().getAllJunctions()
    fun getTrainsForJunction(junctionId: String) = db.trainDao().getTrainsForJunction(junctionId)
    fun getCoachesForTrain(trainId: String) = db.coachDao().getCoachesForTrain(trainId)
    fun getRouteForTrain(trainId: String) = db.routeDao().getRouteForTrain(trainId)
    fun getFacilitiesByJunction(junctionId: String) = db.facilityDao().getFacilitiesByJunction(junctionId)
    fun getAnnouncements(junctionId: String) = db.announcementDao().getAnnouncementsForJunction(junctionId)

    suspend fun getTrainById(trainId: String) = db.trainDao().getTrainById(trainId)
    suspend fun getJunctionById(junctionId: String) = db.junctionDao().getJunctionById(junctionId)

    suspend fun insertFeedback(feedback: FeedbackEntity) {
        db.feedbackDao().insert(feedback)
        
        // Fetch username for Firebase sync
        val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "Anonymous") ?: "Anonymous"
        
        FirebaseHelper.syncFeedback(feedback, userName)
    }
}
