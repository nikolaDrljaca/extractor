package com.drbrosdev.extractor.ui.status

import androidx.compose.runtime.Composable
import com.drbrosdev.extractor.util.DialogNavTarget
import kotlinx.parcelize.Parcelize


@Parcelize
object ExtractorStatusDialogNavTarget : DialogNavTarget {

    @Composable
    override fun Content() {
        ExtractorStatusDialog()
    }
}