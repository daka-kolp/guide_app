package com.example.guideapp.infrastructure.di

import android.content.Context
import com.example.guideapp.GuideApplication
import com.example.guideapp.core.data.GoogleGuideRepository
import com.example.guideapp.core.data.api.GuideApiClient
import com.example.guideapp.core.domain.GuideRepository
import com.example.guideapp.infrastructure.utils.GoogleSignInProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GuideAppModule {
    private val baseUrl = "https://maps.googleapis.com/maps/api/"

    @Provides
    @Singleton
    fun getApiClient(): GuideApiClient = GuideApiClient(baseUrl)

    @Provides
    @Singleton
    fun getGoogleSignInProvider(): GoogleSignInProvider = GoogleSignInProvider()

    @Provides
    @Singleton
    fun getGuideRepository(client: GuideApiClient): GuideRepository = GoogleGuideRepository(client)

    @Provides
    fun providesMainApplicationInstance(@ApplicationContext context: Context): GuideApplication {
        return context as GuideApplication
    }
}
