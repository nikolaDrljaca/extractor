package com.drbrosdev.extractor.framework.navigation

import android.os.Parcelable
import androidx.compose.runtime.Composable
import dev.olshevski.navigation.reimagined.NavController


interface DialogNavTarget : Parcelable {

    @Composable
    fun Content(navController: NavController<DialogNavTarget>)
}
