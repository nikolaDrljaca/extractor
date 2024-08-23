package com.drbrosdev.extractor.domain.usecase.image.create

import android.content.Context
import arrow.core.Either
import com.drbrosdev.extractor.domain.model.InputImageType
import com.google.mlkit.vision.common.InputImage

/**
 * Creates an input image from either a bitmap or file URI path.
 *
 * The resulting [InputImage] data structure will ALWAYS be a [android.graphics.Bitmap].
 * Meaning, accessing [InputImage.getBitmapInternal] should NEVER return null.
 */
class DefaultCreateInputImage(
    private val context: Context
) : CreateInputImage {

    override fun execute(type: InputImageType): Either<InputImageCreationFailed, InputImage> {
        val out = Either.catch {
            when (type) {
                is InputImageType.BitmapInputImage ->
                    InputImage.fromBitmap(
                        type.bitmap,
                        type.rotationDegrees
                    )

                is InputImageType.UriInputImage ->
                    InputImage.fromFilePath(context, type.uri)
            }
        }

        return out.mapLeft { InputImageCreationFailed }
    }
}
