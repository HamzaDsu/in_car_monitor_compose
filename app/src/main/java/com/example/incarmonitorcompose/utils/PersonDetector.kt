package com.example.incarmonitorcompose.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector

object PersonDetector {
    private var detector: ObjectDetector? = null

    fun init(context: Context) {
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)
            .setScoreThreshold(0.5f)
            .build()

        detector = ObjectDetector.createFromFileAndOptions(
            context,
            "person_detection.tflite",
            options
        )
    }

    fun detectPersons(bitmap: Bitmap): List<RectF> {
        //Ensure bitmap is in ARGB_8888 format
        val argbBitmap = if (bitmap.config != Bitmap.Config.ARGB_8888) {
            bitmap.copy(Bitmap.Config.ARGB_8888, false)
        } else {
            bitmap
        }

        val tensorImage = TensorImage.fromBitmap(argbBitmap)
        return detector?.detect(tensorImage)
            ?.filter { it.categories.firstOrNull()?.label == "person" }
            ?.map { it.boundingBox } ?: emptyList()
    }

}
