package com.rooze.insta_2.dependency_injection.application

import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.repository.ImageRepository
import com.rooze.insta_2.domain.repository.NotificationRepository
import com.rooze.insta_2.domain.repository.PostRepository
import com.rooze.insta_2.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {
    @Provides
    fun provideAuthentication(
        accountRepository: AccountRepository,
        imageRepository: ImageRepository
    ): Authentication = Authentication(accountRepository, imageRepository)

    @Provides
    fun providePostsList(
        postRepository: PostRepository,
        accountRepository: AccountRepository
    ): PostsList = PostsList(postRepository, accountRepository)

    @Provides
    fun provideProfileInfo(
        postRepository: PostRepository,
        accountRepository: AccountRepository
    ): ProfileInfo = ProfileInfo(postRepository, accountRepository)

    @Provides
    @Singleton
    fun provideUploadPost(
        postRepository: PostRepository,
        imageRepository: ImageRepository,
        accountRepository: AccountRepository
    ): UploadPost = UploadPost(postRepository, imageRepository, accountRepository)

    @Provides
    fun providePostDetails(
        accountRepository: AccountRepository,
        postRepository: PostRepository,
        imageRepository: ImageRepository
    ): PostDetails = PostDetails(postRepository, accountRepository, imageRepository)

    @Provides
    fun provideEditAccount(
        accountRepository: AccountRepository,
        imageRepository: ImageRepository
    ): EditAccount = EditAccount(accountRepository, imageRepository)

    @Provides
    fun provideAddNotification(
        notificationRepository: NotificationRepository,
        accountRepository: AccountRepository
    ): AddNotification = AddNotification(notificationRepository, accountRepository)

    @Provides
    fun provideListenNotification(
        notificationRepository: NotificationRepository,
        accountRepository: AccountRepository
    ): ListenNotification = ListenNotification(notificationRepository, accountRepository)

    @Provides
    fun provideDeleteNotification(
        notificationRepository: NotificationRepository,
        accountRepository: AccountRepository
    ): DeleteNotification = DeleteNotification(accountRepository, notificationRepository)

    @Provides
    fun provideLikesList(
        postRepository: PostRepository,
        accountRepository: AccountRepository
    ): LikesList = LikesList(postRepository, accountRepository)
}