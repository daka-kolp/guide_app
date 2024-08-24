package com.example.guideapp.core.domain

import com.example.guideapp.core.domain.entities.Geolocation
import com.example.guideapp.core.domain.entities.Sight

interface GuideRepository {
    suspend fun getSightsByUserLocation(location: Geolocation) : List<Sight>
}
