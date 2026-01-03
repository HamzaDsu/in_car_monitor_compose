package com.example.incarmonitorcompose.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import java.lang.Exception
import androidx.core.net.toUri

class VideoFrameReader(private val context: Context, private val videoResId: Int) {
    private val retriever = MediaMetadataRetriever()

    init {
        try {
            val uri = "android.resource://${context.packageName}/$videoResId".toUri()
            retriever.setDataSource(context, uri)
        } catch (e: Exception) {
            println("Failed to set video source: ${e.localizedMessage}")
        }
    }

    // Returns the total duration of the video in milliseconds.
    fun getVideoDurationMs(): Int {
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: 0
    }

    //Returns a frame from the video at a given time (in milliseconds).
    fun getFrameAt(timeMs: Long): Bitmap? {
        return try {
            retriever.getFrameAtTime(timeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            println("Error extracting frame at $timeMs ms: ${e.localizedMessage}")
            null
        }
    }
}
