package com.drbrosdev.extractor.framework.navigation

import android.os.Parcelable
import androidx.compose.runtime.Composable


interface DialogNavTarget : Parcelable {

    @Composable
    fun Content()
}
