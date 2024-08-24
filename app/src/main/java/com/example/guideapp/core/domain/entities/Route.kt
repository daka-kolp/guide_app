package com.example.guideapp.core.domain.entities

data class Route(val points: String, val distanceInMeters: Int, val timeInMinutes: Int) {
    fun getFormattedDistance(): String {
        val km = distanceInMeters / 1000
        val meters = distanceInMeters % 1000
        return "$km,$meters"
    }

    fun getFormattedTime(): String {
        val hours = timeInMinutes / 3600
        val minutes = timeInMinutes % 3600 / 60
        val seconds = timeInMinutes % 3600 % 60
        return "$hours:$minutes:$seconds"
    }
}
