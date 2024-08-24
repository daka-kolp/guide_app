package com.example.guideapp.core.data.api

import retrofit2.http.GET
import retrofit2.http.Query

val apiKey = "AIzaSyCqDLPUrWoM0pawFuA6X2OFcTr1kijCn-w"

interface GuideApiService {
    @GET("place/nearbysearch/json")
    suspend fun getSights(
        @Query("location") location: String,
        @Query("radius") radius: Double,
        @Query("type") type: String,
        @Query("key") key: String = apiKey
    ) : List<SightDto>
}
