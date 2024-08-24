package com.example.guideapp.core.data.api.dto

import com.example.guideapp.core.domain.entities.Geolocation
import com.example.guideapp.core.domain.entities.Sight
import com.google.gson.annotations.SerializedName

data class SightsDto(@SerializedName("results") val sights: List<SightDto>)
data class SightDto(
    val geometry: GeometryDto,
    val name: String,
    val photos: List<PhotoDto>? = null,
    @SerializedName("vicinity") val address: String,
) {
    fun toEntity(): Sight {
        val location = geometry.location
        return Sight(
            geolocation = Geolocation(location.lat, location.lng),
            name = name,
            photo = photos?.map { it.photoReference }?.filterIsInstance<String>()
        )
    }
}

data class GeometryDto(val location: LocationDto)
data class LocationDto(val lat: Double, val lng: Double)
data class PhotoDto(@SerializedName("photo_reference") val photoReference: String? = null)