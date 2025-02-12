package com.drbrosdev.extractor.ui.purchase

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.findActivity
import com.drbrosdev.extractor.util.openAppSettings
import com.drbrosdev.extractor.util.showToast
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Parcelize
object ExtractorPurchaseSearchNavTarget : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: ExtractorPurchaseSearchViewModel = koinViewModel()

        val navController = navigators.navController
        val activity = LocalContext.current.findActivity()
        val context = LocalContext.current

        ExtractorPurchaseSearchScreen(
            snackbarState = viewModel.snackbarHostState,
            onBack = { navController.pop() },
            onSettingsClick = {
                activity.openAppSettings()
            },
            onPurchaseItemClick = {
                context.showToast("Coming soon.")
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
            ExtractorPurchaseSearchScreen(
                snackbarState = SnackbarHostState(),
                onBack = {},
                onSettingsClick = {},
                onPurchaseItemClick = {}
            )
        }
    }
}