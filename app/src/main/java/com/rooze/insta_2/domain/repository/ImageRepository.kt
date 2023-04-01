package com.rooze.insta_2.domain.repository

import com.rooze.insta_2.domain.common.DataResult
import java.io.InputStream

interface ImageRepository {
    suspend fun uploadImage(imageInputStream: InputStream): DataResult<String>
    suspend fun deleteImage(imageUrl: String): DataResult<Boolean>
}