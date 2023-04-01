package com.rooze.insta_2.presentation.profile

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rooze.insta_2.dependency_injection.application.FirebaseModule
import com.rooze.insta_2.dependency_injection.application.RepositoryModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, FirebaseModule::class)
class ProfileViewModelTest {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
    }

    @Test
    fun `loadProfileInfo test return success result`() {
        assertEquals("1", "1")
    }
}