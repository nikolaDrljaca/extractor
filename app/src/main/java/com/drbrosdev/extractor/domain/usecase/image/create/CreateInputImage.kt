package com.drbrosdev.extractor.domain.usecase.image.create

import arrow.core.Either
import com.drbrosdev.extractor.domain.model.InputImageType
import com.google.mlkit.vision.common.InputImage


interface CreateInputImage {

    fun execute(type: InputImageType): Either<InputImageCreationFailed, InputImage>
}

