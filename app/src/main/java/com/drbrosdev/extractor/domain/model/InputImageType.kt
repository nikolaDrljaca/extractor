package com.drbrosdev.extractor.domain.model

import android.graphics.Bitmap
import android.net.Uri

sealed interface InputImageType {

    data class UriInputImage(val uri: Uri) : InputImageType

    data class BitmapInputImage(val bitmap: Bitmap, val rotationDegrees: Int) : InputImageType
}
