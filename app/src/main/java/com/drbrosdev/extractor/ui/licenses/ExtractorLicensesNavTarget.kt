package com.drbrosdev.extractor.ui.licenses

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.NavTarget
import kotlinx.parcelize.Parcelize

@Parcelize
object ExtractorLicensesNavTarget : NavTarget {

    @Composable
    override fun Content() {
        //TODO implement
        ExtractorLicensesScreen()
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorLicensesScreen()
    }
}