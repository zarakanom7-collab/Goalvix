package com.example.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebasePredictionService(private val context: Context) {

    companion object {
        private const val TAG = "GoalvixStorage"
    }

    private suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            if (continuation.isActive) continuation.resume(result)
        }
        addOnFailureListener { exception ->
            if (continuation.isActive) continuation.resumeWithException(exception)
        }
        addOnCanceledListener {
            if (continuation.isActive) continuation.cancel()
        }
    }

    private fun getStorageInstance(): FirebaseStorage? {
        return try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isNotEmpty()) {
                FirebaseStorage.getInstance()
            } else {
                // Attempt initialization if default config is present
                val app = FirebaseApp.initializeApp(context)
                if (app != null) FirebaseStorage.getInstance() else null
            }
        } catch (e: Throwable) {
            Log.w(TAG, "FirebaseApp not initialized or google-services.json not found: ${e.message}")
            null
        }
    }

    suspend fun fetchPredictionImageUrl(
        storagePath: String,
        forceRefresh: Boolean = false
    ): Result<Pair<String, Long>> = withContext(Dispatchers.IO) {
        val storage = getStorageInstance()
            ?: return@withContext Result.failure(
                IllegalStateException("Firebase Storage is not initialized yet. Ensure your Firebase project is configured.")
            )

        try {
            val storageRef: StorageReference = storage.reference.child(storagePath)

            // 1. Fetch metadata to extract updatedTimeMillis
            val metadata: StorageMetadata? = try {
                storageRef.metadata.awaitTask()
            } catch (e: Exception) {
                Log.w(TAG, "Could not fetch metadata for $storagePath: ${e.message}")
                null
            }

            // If forced refresh, use current timestamp to bypass any intermediate proxy/CDN caching
            val lastUpdated = if (forceRefresh) {
                System.currentTimeMillis()
            } else {
                metadata?.updatedTimeMillis ?: System.currentTimeMillis()
            }

            // 2. Fetch the dynamic download URL from Firebase Storage
            val downloadUri: Uri = storageRef.downloadUrl.awaitTask()
            val baseUriString = downloadUri.toString()

            // 3. Append cache-busting parameter
            val cacheBustedUrl = if (baseUriString.contains("?")) {
                "$baseUriString&_cb=$lastUpdated"
            } else {
                "$baseUriString?_cb=$lastUpdated"
            }

            Log.d(TAG, "Resolved $storagePath -> $cacheBustedUrl (cacheBuster: $lastUpdated, forced: $forceRefresh)")
            Result.success(Pair(cacheBustedUrl, lastUpdated))
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to fetch image from Firebase Storage for $storagePath", e)
            Result.failure(e)
        }
    }
}
