package com.drbrosdev.extractor.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage

sealed interface InputImageType {

    data class UriInputImage(val uri: Uri) : InputImageType

    data class BitmapInputImage(val bitmap: Bitmap, val rotationDegrees: Int) : InputImageType
}

interface InputImageProvider {
    fun create(type: InputImageType): InputImage
}

class DefaultInputImageProvider(
    private val context: Context
) : InputImageProvider {

    override fun create(type: InputImageType): InputImage {
        return when (type) {
            is InputImageType.BitmapInputImage -> InputImage.fromBitmap(
                type.bitmap,
                type.rotationDegrees
            )

            is InputImageType.UriInputImage -> InputImage.fromFilePath(context, type.uri)
        }
    }
}