package com.example.guideapp.core.data

import com.example.guideapp.core.data.api.GuideApiClient
import com.example.guideapp.core.data.api.GuideApiService
import com.example.guideapp.core.domain.GuideRepository
import com.example.guideapp.core.domain.entities.Geolocation
import com.example.guideapp.core.domain.entities.Sight

class GoogleGuideRepository(private val client: GuideApiClient) : GuideRepository {
    override suspend fun getSightsByUserLocation(location: Geolocation): List<Sight> {
        val api = client.retrofit.create(GuideApiService::class.java)
        val result = api.getSights(
            location = location.toString(),
            radius = 1000.0,
            type = "tourist_attraction"
        )
        return result.map { it.toEntity() }
    }
}
