package com.drbrosdev.extractor.domain.usecase.image.create

import android.content.Context
import com.drbrosdev.extractor.domain.model.InputImageType
import com.google.mlkit.vision.common.InputImage

class DefaultCreateInputImage(
    private val context: Context
) : CreateInputImage {

    override fun execute(type: InputImageType): InputImage {
        return when (type) {
            is InputImageType.BitmapInputImage -> InputImage.fromBitmap(
                type.bitmap,
                type.rotationDegrees
            )

            is InputImageType.UriInputImage -> InputImage.fromFilePath(context, type.uri)
        }
    }
}
