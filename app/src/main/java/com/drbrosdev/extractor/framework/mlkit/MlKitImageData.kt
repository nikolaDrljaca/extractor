package com.drbrosdev.extractor.framework.mlkit

import com.drbrosdev.extractor.domain.model.MediaImageData
import com.google.mlkit.vision.common.InputImage

data class MlKitImageData(val inputImage: InputImage) : MediaImageData

fun InputImage.toImageData(): MediaImageData = MlKitImageData(this)
