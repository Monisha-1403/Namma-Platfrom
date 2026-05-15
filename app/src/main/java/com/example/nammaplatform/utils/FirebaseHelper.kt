package com.example.nammaplatform.utils

import android.util.Log
import com.example.nammaplatform.data.database.AppDatabase
import com.example.nammaplatform.data.entities.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

object FirebaseHelper {

    private const val TAG = "FIREBASE_SYNC"

    fun syncFeedback(feedback: FeedbackEntity, userName: String) {
        try {
            val db = FirebaseFirestore.getInstance()
            val data = hashMapOf(
                "userName" to userName,
                "rating" to feedback.rating,
                "comment" to feedback.feedbackText,
                "timestamp" to feedback.timestamp
            )
            db.collection("feedback").add(data)
                .addOnSuccessListener { Log.d(TAG, "Feedback synced successfully for $userName") }
                .addOnFailureListener { e -> Log.e(TAG, "Error syncing feedback", e) }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase sync exception", e)
        }
    }

    fun syncUserProfile(
        name: String, 
        phone: String, 
        language: String, 
        theme: String = "Dark", 
        audioPreference: Boolean = true,
        accessibility: Boolean = false
    ) {
        try {
            val db = FirebaseFirestore.getInstance()
            val data = hashMapOf(
                "name" to name,
                "phone" to phone,
                "language" to language,
                "theme" to theme,
                "audioPreference" to audioPreference,
                "accessibilityMode" to accessibility,
                "lastActive" to System.currentTimeMillis()
            )
            db.collection("users").document(phone).set(data)
                .addOnSuccessListener { Log.d(TAG, "User profile synced: $name") }
                .addOnFailureListener { e -> Log.e(TAG, "Error syncing user profile", e) }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase profile sync exception", e)
        }
    }

    suspend fun syncDataFromFirestore(appDatabase: AppDatabase) {
        try {
            val db = FirebaseFirestore.getInstance()
            Log.d(TAG, "FIREBASE_SYNC_START: Starting full hybrid sync from Firestore...")
            
            // Sync Junctions
            Log.d(TAG, "FIREBASE_SYNC: Syncing Junctions...")
            val junctionSnaps = db.collection("junctions").get().await()
            val junctions = junctionSnaps.documents.mapNotNull { doc ->
                val junctionId = doc.getString("junctionId") ?: return@mapNotNull null
                JunctionEntity(
                    junctionId = junctionId,
                    name = doc.getString("name") ?: "",
                    nameKn = doc.getString("nameKn") ?: "",
                    code = doc.getString("code") ?: "",
                    city = doc.getString("city") ?: ""
                )
            }
            if (junctions.isNotEmpty()) {
                appDatabase.junctionDao().insertAll(junctions)
                Log.d(TAG, "FIREBASE_SYNC: Junctions cached: ${junctions.size}")
            }

            // Sync Trains
            Log.d(TAG, "FIREBASE_SYNC: Syncing Trains...")
            val trainSnaps = db.collection("trains").get().await()
            val trains = trainSnaps.documents.mapNotNull { doc ->
                val trainId = doc.getString("trainId") ?: return@mapNotNull null
                TrainEntity(
                    trainId = trainId,
                    trainNameEnglish = doc.getString("trainNameEnglish") ?: doc.getString("trainName") ?: "",
                    trainNameKannada = doc.getString("trainNameKannada") ?: doc.getString("trainNameKn") ?: "",
                    trainNumber = doc.getString("trainNumber") ?: "",
                    platform = doc.getString("platform") ?: "",
                    arrivalTime = doc.getString("arrivalTime") ?: "",
                    departureTime = doc.getString("departureTime") ?: "",
                    junctionId = doc.getString("junctionId") ?: "",
                    status = doc.getString("status") ?: "On Time"
                )
            }
            if (trains.isNotEmpty()) {
                appDatabase.trainDao().insertAll(trains)
                Log.d(TAG, "FIREBASE_SYNC: Trains cached: ${trains.size}")
            }

            // Sync Routes
            Log.d(TAG, "FIREBASE_SYNC: Syncing Routes...")
            val routeSnaps = db.collection("routes").get().await()
            val routes = routeSnaps.documents.mapNotNull { doc ->
                RouteEntity(
                    trainId = doc.getString("trainId") ?: "",
                    stationNameEnglish = doc.getString("stationNameEnglish") ?: doc.getString("stationName") ?: "",
                    stationNameKannada = doc.getString("stationNameKannada") ?: doc.getString("stationNameKn") ?: "",
                    arrival = doc.getString("arrival") ?: "",
                    departure = doc.getString("departure") ?: "",
                    platform = doc.getString("platform") ?: "",
                    sequence = doc.getLong("sequence")?.toInt() ?: 0
                )
            }
            if (routes.isNotEmpty()) {
                appDatabase.routeDao().insertAll(routes)
                Log.d(TAG, "FIREBASE_SYNC: Routes cached: ${routes.size}")
            }

            // Sync Coaches
            Log.d(TAG, "FIREBASE_SYNC: Syncing Coaches...")
            val coachSnaps = db.collection("coachData").get().await()
            val coaches = coachSnaps.documents.mapNotNull { doc ->
                CoachEntity(
                    trainId = doc.getString("trainId") ?: "",
                    coachName = doc.getString("coachName") ?: "",
                    coachType = doc.getString("coachType") ?: "",
                    coachTypeKn = doc.getString("coachTypeKn") ?: "",
                    seatAvailability = doc.getString("seatAvailability") ?: "0",
                    crowdLevel = doc.getString("crowdLevel") ?: "LOW",
                    platformPosition = doc.getString("platformPosition") ?: "",
                    platformPositionKn = doc.getString("platformPositionKn") ?: "",
                    boardingAdvice = doc.getString("boardingAdvice") ?: "",
                    boardingAdviceKn = doc.getString("boardingAdviceKn") ?: "",
                    isLadies = doc.getBoolean("isLadies") ?: false,
                    securityLevel = doc.getString("securityLevel") ?: "",
                    securityLevelKn = doc.getString("securityLevelKn") ?: "",
                    nearestPolice = doc.getString("nearestPolice") ?: "",
                    nearestPoliceKn = doc.getString("nearestPoliceKn") ?: "",
                    nearestWashroom = doc.getString("nearestWashroom") ?: "",
                    nearestWashroomKn = doc.getString("nearestWashroomKn") ?: "",
                    safeZone = doc.getString("safeZone") ?: "",
                    safeZoneKn = doc.getString("safeZoneKn") ?: ""
                )
            }
            if (coaches.isNotEmpty()) {
                appDatabase.coachDao().insertAll(coaches)
                Log.d(TAG, "FIREBASE_SYNC: Coach data cached: ${coaches.size}")
            }

            // Sync Facilities
            Log.d(TAG, "FIREBASE_SYNC: Syncing Facilities...")
            val facilitySnaps = db.collection("facilities").get().await()
            val facilities = facilitySnaps.documents.mapNotNull { doc ->
                FacilityEntity(
                    junctionId = doc.getString("junctionId") ?: "",
                    facilityName = doc.getString("facilityName") ?: "",
                    facilityNameKn = doc.getString("facilityNameKn") ?: "",
                    location = doc.getString("location") ?: "",
                    locationKn = doc.getString("locationKn") ?: "",
                    distance = doc.getString("distance") ?: "",
                    distanceKn = doc.getString("distanceKn") ?: "",
                    availability = doc.getString("availability") ?: "",
                    availabilityKn = doc.getString("availabilityKn") ?: "",
                    description = doc.getString("description") ?: "",
                    descriptionKn = doc.getString("descriptionKn") ?: ""
                )
            }
            if (facilities.isNotEmpty()) {
                appDatabase.facilityDao().insertAll(facilities)
                Log.d(TAG, "FIREBASE_SYNC: Facilities cached: ${facilities.size}")
            }

            // Sync Announcements
            Log.d(TAG, "FIREBASE_SYNC: Syncing Announcements...")
            val announcementSnaps = db.collection("announcements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .get().await()
            val announcements = announcementSnaps.documents.mapNotNull { doc ->
                val announcementId = doc.id
                AnnouncementEntity(
                    announcementId = announcementId,
                    junctionId = doc.getString("junctionId") ?: "",
                    messageEn = doc.getString("messageEn") ?: "",
                    messageKn = doc.getString("messageKn") ?: "",
                    type = doc.getString("type") ?: "General",
                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                )
            }
            if (announcements.isNotEmpty()) {
                appDatabase.announcementDao().insertAll(announcements)
                Log.d(TAG, "FIREBASE_SYNC: Announcements cached: ${announcements.size}")
            }

            Log.d(TAG, "FIREBASE_SYNC_SUCCESS: Full hybrid sync from Firestore completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "FIREBASE_SYNC_FAILURE: Offline Mode - Firebase sync failed. Using local Room cache.", e)
        }
    }
}
