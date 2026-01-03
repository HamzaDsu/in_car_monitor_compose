package com.example.incarmonitorcompose.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.incarmonitorcompose.domain.usecase.GenerateSummaryUseCase

class MainViewModelFactory(
    private val generateSummaryUseCase: GenerateSummaryUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(generateSummaryUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
