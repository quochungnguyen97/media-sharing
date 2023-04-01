package com.rooze.insta_2.dependency_injection.activity

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.rooze.insta_2.presentation.common.OneTapSignInHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {
    @Provides
    fun provideSignInClient(@ActivityContext context: Context): SignInClient {
        return Identity.getSignInClient(context)
    }

    @Provides
    fun provideOneTapSignInHelper(signInClient: SignInClient): OneTapSignInHelper {
        return OneTapSignInHelper(signInClient)
    }
}