package com.example.incarmonitorcompose.data.request

import retrofit2.http.Body
import retrofit2.http.POST

data class SummaryRequest(
    val frames: List<String>
)

interface SummaryApi {
    @POST("log-summary")
    suspend fun sendSummary(@Body request: SummaryRequest)
}