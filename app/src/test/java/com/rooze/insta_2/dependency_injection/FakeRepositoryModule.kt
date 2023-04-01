package com.rooze.insta_2.dependency_injection

import com.rooze.insta_2.dependency_injection.application.RepositoryModule
import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.repository.ImageRepository
import com.rooze.insta_2.domain.repository.NotificationRepository
import com.rooze.insta_2.domain.repository.PostRepository
import com.rooze.insta_2.domain.use_case.repository.MockAccountRepository
import com.rooze.insta_2.domain.use_case.repository.MockImageRepository
import com.rooze.insta_2.domain.use_case.repository.MockNotificationRepository
import com.rooze.insta_2.domain.use_case.repository.MockPostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])
class FakeRepositoryModule {
    @Provides
    @Singleton
    fun provideAccountRepository(): AccountRepository = MockAccountRepository()

    @Provides
    @Singleton
    fun providePostRepository(): PostRepository = MockPostRepository()

    @Provides
    @Singleton
    fun provideImageRepository(): ImageRepository = MockImageRepository()

    @Provides
    @Singleton
    fun provideNotificationRepository(): NotificationRepository = MockNotificationRepository()
}