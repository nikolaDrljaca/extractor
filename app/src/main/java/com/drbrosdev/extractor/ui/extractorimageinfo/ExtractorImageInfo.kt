package com.drbrosdev.extractor.ui.extractorimageinfo

import androidx.compose.runtime.Composable
import com.drbrosdev.extractor.util.DialogNavTarget
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExtractorImageInfo(
    val mediaImageId: Long
): DialogNavTarget {

    @Composable
    override fun Content() {
        TODO("Not yet implemented")
    }
}