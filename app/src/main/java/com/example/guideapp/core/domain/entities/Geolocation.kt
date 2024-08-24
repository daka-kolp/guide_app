package com.example.guideapp.core.domain.entities

data class Geolocation(
    val latitude: Double,
    val longitude: Double
) {
    override fun toString(): String {
        return "$latitude,$longitude"
    }
}
