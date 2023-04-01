package com.rooze.insta_2.domain.use_case

import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.entity.Post
import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.repository.ImageRepository
import com.rooze.insta_2.domain.repository.PostRepository
import java.io.InputStream

class UploadPost(
    private val postRepository: PostRepository,
    private val imageRepository: ImageRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(content: String, imageInputStream: InputStream?): DataResult<Post> {
        val account = accountRepository.getCurrentAccountId().let {
            when (it) {
                is DataResult.Fail -> null
                is DataResult.Success -> Account(id = it.data)
            }
        } ?: return DataResult.Fail("Login required")

        if (imageInputStream == null) {
            return DataResult.Fail("Image data error")
        }

        val imageUploadResult = imageRepository.uploadImage(imageInputStream)

        if (imageUploadResult is DataResult.Fail) {
            return DataResult.Fail(imageUploadResult.message)
        }

        val imageUrl = (imageUploadResult as DataResult.Success).data

        return postRepository.post(Post(
            content = content,
            imageUrl = imageUrl,
            owner = account
        ))
    }
}