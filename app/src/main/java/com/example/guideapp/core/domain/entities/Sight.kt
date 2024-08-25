package com.example.guideapp.core.domain.entities

data class Sight(
    val name: String,
    val geolocation: Geolocation,
    val photos: List<String>?,
) {
    val photo: String? = photos?.first()
}
