package com.drbrosdev.extractor.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize

@Parcelize
object ExtractorSettingsNavTarget : NavTarget {

    @Composable
    override fun Content() {

        val navController = LocalNavController.current

        ExtractorSettingsScreen(
            onBack = { navController.pop() }
        )
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ExtractorSettingsScreen(
                onBack = {}
            )
        }
    }
}