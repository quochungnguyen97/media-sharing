package com.rooze.insta_2.dependency_injection.application

import android.content.Context
import com.rooze.insta_2.presentation.notification.MediaNotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Provides
    @Singleton
    fun provideMediaNotificationManager(@ApplicationContext context: Context): MediaNotificationManager {
        return MediaNotificationManager(context)
    }
}