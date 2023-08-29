package com.drbrosdev.extractor.util

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap

@Composable
fun applicationIconBitmap(): ImageBitmap {
    return LocalContext.current.packageManager
        .getApplicationIcon("com.drbrosdev.extractor")
        .toBitmap(config = Bitmap.Config.ARGB_8888)
        .asImageBitmap()
}