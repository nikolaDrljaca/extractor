package com.drbrosdev.extractor.framework.navigation

import android.os.Parcelable
import androidx.compose.runtime.Composable
import dev.olshevski.navigation.reimagined.material.BottomSheetState


interface BottomSheetNavTarget : Parcelable {

    @Composable
    fun Content(sheetState: BottomSheetState)
}
