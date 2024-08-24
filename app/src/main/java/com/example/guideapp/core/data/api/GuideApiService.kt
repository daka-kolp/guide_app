package com.example.guideapp.core.data.api

import com.example.guideapp.core.data.api.dto.RoutesDto
import com.example.guideapp.core.data.api.dto.SightDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val apiKey = "AIzaSyAixLB4aLKOSaeOh9sdt0_2wwxJQzt-Vdc"

interface GuideApiService {
    @GET("place/nearbysearch/json")
    suspend fun getSights(
        @Query("location") location: String,
        @Query("type") type: String = "tourist_attraction",
        @Query("radius") radius: Double = 2000.0,
        @Query("key") key: String = apiKey
    ) : Response<List<SightDto>>

    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String = apiKey
    ) : Response<RoutesDto>
}
