package com.example.guideapp.infrastructure.di

import android.content.Context
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
class GuideAppLogicModule {
    @Provides
    @Singleton
    fun getApiClient(): GuideApiClient = GuideApiClient()

    @Provides
    @Singleton
    fun getGuideRepository(client: GuideApiClient): GuideRepository = GoogleGuideRepository(client)

    @Singleton
    @Provides
    fun getGoogleSignInProvider(@ApplicationContext context: Context): GoogleSignInProvider {
        return GoogleSignInProvider(context)
    }
}
