package com.example.incarmonitorcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.incarmonitorcompose.presentation.view.MainScreen
import com.example.incarmonitorcompose.presentation.viewmodel.MainViewModel
import com.example.incarmonitorcompose.utils.PersonDetector
import androidx.lifecycle.ViewModelProvider
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.incarmonitorcompose.data.service.SummaryService
import com.example.incarmonitorcompose.data.repository.SummaryRepository
import com.example.incarmonitorcompose.domain.usecase.GenerateSummaryUseCase
import com.example.incarmonitorcompose.presentation.viewmodel.MainViewModelFactory


class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Init TensorFlow
        PersonDetector.init(applicationContext)

        //Create Retrofit instance and ViewModel manually
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // For emulator accessing localhost
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(SummaryService::class.java)
        val repo = SummaryRepository(api)
        val useCase = GenerateSummaryUseCase(repo)
        val factory = MainViewModelFactory(useCase)

        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        //Set Compose content
        setContent {
            MainScreen(viewModel)
        }
    }
}
