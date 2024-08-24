package com.example.guideapp.core.data.api.dto

import com.example.guideapp.core.domain.entities.Route
import com.google.gson.annotations.SerializedName

data class RoutesDto(val routes: List<RouteDto>)

data class RouteDto(@SerializedName("overview_polyline") val polyline: PolylineDto) {
    fun toEntity(): Route = Route(polyline.points)
}

data class PolylineDto(val points: String)
