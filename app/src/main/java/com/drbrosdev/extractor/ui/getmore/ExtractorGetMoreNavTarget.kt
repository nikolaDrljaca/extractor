package com.drbrosdev.extractor.ui.getmore

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.drbrosdev.extractor.framework.navigation.LocalNavController
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Parcelize
object ExtractorGetMoreNavTarget : NavTarget {

    @Composable
    override fun Content() {
        val viewModel: ExtractorGetMoreViewModel = koinViewModel()

        val navController = LocalNavController.current

        ExtractorGetMoreScreen(
            onBack = { navController.pop() },
            onViewAdClick = {
                viewModel.rewardSearches()
            }
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ExtractorGetMoreScreen(
                onBack = {},
                onViewAdClick = {}
            )
        }
    }
}