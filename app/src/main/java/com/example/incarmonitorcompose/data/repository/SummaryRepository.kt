package com.example.incarmonitorcompose.data.repository

import com.example.incarmonitorcompose.data.request.SummaryRequest
import com.example.incarmonitorcompose.data.service.SummaryService
import com.example.incarmonitorcompose.domain.model.Summary


class SummaryRepository(private val api: SummaryService) {
    suspend fun getSummary(frames: List<String>): Summary {
        return api.generateSummary(SummaryRequest(frames))
    }
}

