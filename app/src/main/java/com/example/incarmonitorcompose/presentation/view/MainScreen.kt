package com.example.incarmonitorcompose.presentation.view

import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.incarmonitorcompose.R
import com.example.incarmonitorcompose.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import androidx.core.net.toUri

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val summaryText by viewModel.summaryText.collectAsState()
    val frameResults by viewModel.frameResults.collectAsState()
    val frames by viewModel.processedFrames.collectAsState()
    val isGeneratingSummary by viewModel.isGeneratingSummary.collectAsState()

    var currentFrameIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text("In-Car Monitoring System", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            AndroidView(
                factory = {
                    VideoView(it).apply {
                        setVideoURI("android.resource://${context.packageName}/${R.raw.sample_video}".toUri())
                        val controller = MediaController(it)
                        controller.setAnchorView(this)
                        setMediaController(controller)
                        start()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.startRealTimeProcessing(
                        context = context,
                        videoResId = R.raw.sample_video
                    )
                },
                enabled = !isGeneratingSummary
            ) {
                if (isGeneratingSummary) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Start & Summarize")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Summary:", style = MaterialTheme.typography.titleMedium)
                if (isGeneratingSummary) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
            if (isGeneratingSummary && summaryText.isBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Generating summary...")
            } else {
                Text(summaryText)
            }

            if (isGeneratingSummary && frames.isEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Preparing animation...", style = MaterialTheme.typography.titleMedium)
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(R.drawable.placeholder)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Loading placeholder",
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .padding(vertical = 8.dp)
                )
            }

            if (frames.isNotEmpty()) {
                LaunchedEffect(frames, isPlaying) {
                    while (isPlaying) {
                        delay(250)
                        currentFrameIndex = (currentFrameIndex + 1) % frames.size
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Animated Frame Preview:", style = MaterialTheme.typography.titleMedium)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Button(onClick = { isPlaying = !isPlaying }) {
                        Text(if (isPlaying) "Pause Animation" else "Play Animation")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Frame ${currentFrameIndex + 1}/${frames.size}")
                }

                Image(
                    bitmap = frames[currentFrameIndex].asImageBitmap(),
                    contentDescription = "Animated processed frame",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
            }

            if (frameResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Frame Detection Table:", style = MaterialTheme.typography.titleMedium)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        frameResults.forEach { result ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Frame ${result.frameIndex + 1}")
                                Text("${result.peopleCount} person(s)")
                            }
                        }
                    }
                }
            }
        }
    }
}
