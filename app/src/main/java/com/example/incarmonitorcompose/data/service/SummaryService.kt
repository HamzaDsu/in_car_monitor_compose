package com.example.incarmonitorcompose.data.service

import com.example.incarmonitorcompose.data.request.SummaryRequest
import com.example.incarmonitorcompose.domain.model.Summary
import retrofit2.http.Body
import retrofit2.http.POST

interface SummaryService {
    @POST("generate-summary")
    suspend fun generateSummary(@Body frames: SummaryRequest): Summary
}
