package com.example.incarmonitorcompose.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.incarmonitorcompose.domain.model.FrameResult
import com.example.incarmonitorcompose.domain.usecase.GenerateSummaryUseCase
import com.example.incarmonitorcompose.utils.PersonDetector
import com.example.incarmonitorcompose.utils.VideoFrameProcessor
import com.example.incarmonitorcompose.utils.VideoFrameReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val generateSummary: GenerateSummaryUseCase) : ViewModel() {

    private val _frameResults = MutableStateFlow<List<FrameResult>>(emptyList())
    val frameResults: StateFlow<List<FrameResult>> = _frameResults

    private val _previewBitmap = MutableStateFlow<Bitmap?>(null)
    val previewBitmap: StateFlow<Bitmap?> = _previewBitmap

    private val _processedFrames = MutableStateFlow<List<Bitmap>>(emptyList())
    val processedFrames: StateFlow<List<Bitmap>> = _processedFrames

    private val _summaryText = MutableStateFlow("Summary will appear here.")
    val summaryText: StateFlow<String> = _summaryText

    private val _isGeneratingSummary = MutableStateFlow(false)
    val isGeneratingSummary: StateFlow<Boolean> = _isGeneratingSummary


    private val capturedFrames = mutableListOf<String>()

    private fun resetFrames() {
        capturedFrames.clear()
    }

    private fun addFrame(base64: String) {
        capturedFrames.add(base64)
    }

    private fun generateSummaryFromFrames() {
        viewModelScope.launch {
            _isGeneratingSummary.value = true
            try {
                val result = generateSummary(capturedFrames)
                _summaryText.value = result.text
            } catch (e: Exception) {
                _summaryText.value = "Error: ${e.localizedMessage}"
            } finally {
                _isGeneratingSummary.value = false
            }
        }
    }

    fun startRealTimeProcessing(context: Context, videoResId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isGeneratingSummary.value = true
            resetFrames()
            _processedFrames.value = emptyList()
            _summaryText.value = ""
            _previewBitmap.value = null
            _frameResults.value = emptyList()

            val reader = VideoFrameReader(context, videoResId)
            val duration = reader.getVideoDurationMs().toLong()

            val annotatedFrames = mutableListOf<Bitmap>()
            val frameResults = mutableListOf<FrameResult>()

            for (time in 0L until duration step 1000L) {
                val bitmap = reader.getFrameAt(time)
                if (bitmap != null) {
                    val boxes = PersonDetector.detectPersons(bitmap)
                    val annotated = VideoFrameProcessor.drawBoundingBox(bitmap, boxes)
                    val base64 = VideoFrameProcessor.encodeToBase64(annotated)

                    annotatedFrames.add(annotated)
                    val frameNumber = (time / 1000).toInt()  // Converts ms to frame count
                    frameResults.add(FrameResult(frameNumber, boxes.size))

                    addFrame(base64)
                    _frameResults.value = frameResults.toList()
                    _processedFrames.value = annotatedFrames.toList()

                    if (time == 0L) _previewBitmap.value = annotated
                }
                delay(250)
            }

            generateSummaryFromFrames()
        }
    }
}
