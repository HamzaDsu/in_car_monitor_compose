package com.example.incarmonitorcompose.domain.usecase

import com.example.incarmonitorcompose.data.repository.SummaryRepository
import com.example.incarmonitorcompose.domain.model.Summary


class GenerateSummaryUseCase(private val repository: SummaryRepository) {
    suspend operator fun invoke(frames: List<String>): Summary {
        return repository.getSummary(frames)
    }
}
