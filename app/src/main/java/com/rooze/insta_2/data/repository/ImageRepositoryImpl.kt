package com.rooze.insta_2.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.repository.ImageRepository
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "ImageRepositoryImpl"

class ImageRepositoryImpl(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : ImageRepository {
    override suspend fun uploadImage(imageInputStream: InputStream): DataResult<String> {
        val firebaseUser = auth.currentUser ?: return DataResult.Fail("Login required")

        val imagePath = "users/${firebaseUser.uid}/image_${System.currentTimeMillis()}"

        return suspendCoroutine { continuation ->
            storage.reference.child(imagePath)
                .putStream(imageInputStream)
                .addOnSuccessListener { snapshot ->
                    snapshot.metadata?.reference?.downloadUrl?.let { urlTask ->
                        urlTask.addOnCompleteListener {
                            if (it.isSuccessful) {
                                continuation.resume(DataResult.Success(it.result.toString()))
                            } else {
                                continuation.resume(DataResult.Fail("Failed to get image url"))
                            }
                        }
                    } ?: run {
                        continuation.resume(DataResult.Fail("Failed to get image url"))
                    }
                }.addOnFailureListener { e ->
                    Log.e(TAG, "uploadImage: ", e)
                    continuation.resume(DataResult.Fail("Failed to upload image"))
                }
        }
    }

    override suspend fun deleteImage(imageUrl: String): DataResult<Boolean> {
        return suspendCoroutine { continuation ->
            storage.getReferenceFromUrl(imageUrl)
                .delete()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(DataResult.Success(true))
                    } else {
                        Log.e(TAG, "deleteImage: ", it.exception)
                        continuation.resume(DataResult.Fail("Failed to delete image"))
                    }
                }
        }
    }
}