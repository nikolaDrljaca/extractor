package com.drbrosdev.extractor.domain.usecase

import android.content.Context
import com.drbrosdev.extractor.domain.model.InputImageType
import com.google.mlkit.vision.common.InputImage


interface InputImageFactory {
    fun create(type: InputImageType): InputImage
}

class DefaultInputImageFactory(
    private val context: Context
) : InputImageFactory {

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