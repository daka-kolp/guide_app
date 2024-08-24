package com.example.guideapp.core.domain.entities

data class Sight(
    val geolocation: Geolocation,
    val name: String,
    val photo: List<String>?,
)
