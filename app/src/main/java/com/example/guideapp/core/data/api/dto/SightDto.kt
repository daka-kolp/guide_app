package com.example.guideapp.core.data.api.dto

import com.example.guideapp.core.domain.entities.Geolocation
import com.example.guideapp.core.domain.entities.Sight

data class SightDto(
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val image: String,
) {
    fun toEntity(): Sight {
        return Sight(
            geolocation = Geolocation(latitude, longitude),
            name = name,
        )
    }
}
