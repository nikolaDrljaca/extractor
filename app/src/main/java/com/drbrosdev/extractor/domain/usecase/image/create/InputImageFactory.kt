package com.drbrosdev.extractor.domain.usecase.image.create

import com.drbrosdev.extractor.domain.model.InputImageType
import com.google.mlkit.vision.common.InputImage


interface InputImageFactory {
    fun create(type: InputImageType): InputImage
}
