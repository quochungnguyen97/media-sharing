package com.rooze.insta_2.domain.use_case.repository

import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.repository.ImageRepository
import java.io.InputStream

class MockImageRepository(var success: Boolean = true) : ImageRepository {
    override suspend fun uploadImage(imageInputStream: InputStream): DataResult<String> {
        return if (success) {
            DataResult.Success("image_data")
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun deleteImage(imageUrl: String): DataResult<Boolean> {
        return if (success) {
            DataResult.Success(true)
        } else {
            DataResult.Fail("Failed")
        }
    }
}