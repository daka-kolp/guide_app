package com.example.guideapp.core.domain

import com.example.guideapp.core.domain.entities.Geolocation
import com.example.guideapp.core.domain.entities.Route
import com.example.guideapp.core.domain.entities.Sight

interface GuideRepository {
    suspend fun getSightsByUserLocation(location: Geolocation): List<Sight>
    suspend fun getDirections(origin: Geolocation, destination: Geolocation): List<Route>
}
