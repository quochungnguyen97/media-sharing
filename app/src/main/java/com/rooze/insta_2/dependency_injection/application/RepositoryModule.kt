package com.rooze.insta_2.dependency_injection.application

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.rooze.insta_2.data.repository.AccountRepositoryImpl
import com.rooze.insta_2.data.repository.ImageRepositoryImpl
import com.rooze.insta_2.data.repository.NotificationRepositoryImpl
import com.rooze.insta_2.data.repository.PostRepositoryImpl
import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.repository.ImageRepository
import com.rooze.insta_2.domain.repository.NotificationRepository
import com.rooze.insta_2.domain.repository.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideAccountRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AccountRepository = AccountRepositoryImpl(auth, firestore)

    @Provides
    @Singleton
    fun provideImageRepository(
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ): ImageRepository = ImageRepositoryImpl(auth, storage)

    @Provides
    @Singleton
    fun providePostRepository(
        firestore: FirebaseFirestore
    ): PostRepository = PostRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideNotificationRepository(
        database: FirebaseDatabase,
        firestore: FirebaseFirestore
    ): NotificationRepository = NotificationRepositoryImpl(database, firestore)
}