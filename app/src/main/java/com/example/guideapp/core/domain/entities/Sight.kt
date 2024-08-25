package com.example.guideapp.core.domain.entities

data class Sight(
    val geolocation: Geolocation,
    val name: String,
    val photos: List<String>?,
) {
    val photo: String? = photos?.first()
}
