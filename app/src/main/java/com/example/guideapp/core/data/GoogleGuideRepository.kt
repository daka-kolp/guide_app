package com.example.guideapp.core.data

import com.example.guideapp.core.data.api.GuideApiClient
import com.example.guideapp.core.data.api.GuideApiService
import com.example.guideapp.core.domain.GuideRepository
import com.example.guideapp.core.domain.entities.Geolocation
import com.example.guideapp.core.domain.entities.Route
import com.example.guideapp.core.domain.entities.Sight

class GoogleGuideRepository(private val client: GuideApiClient) : GuideRepository {
    private fun getService(): GuideApiService = client.retrofit.create(GuideApiService::class.java)

    override suspend fun getSightsByUserLocation(location: Geolocation): List<Sight> {
        val result = getService().getSights(location = location.toString())
        return result.body()!!.sights.map { it.toEntity() }
    }

    override suspend fun getDirections(origin: Geolocation, destination: Geolocation): List<Route> {
        val result = getService().getDirections(
            origin = origin.toString(),
            destination = destination.toString()
        )
        return result.body()!!.routes.map { it.toEntity() }
    }
}