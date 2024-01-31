package com.drbrosdev.extractor.ui.settings.periodic

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object ExtractorPeriodicWorkNavTarget : NavTarget {

    @Composable
    override fun Content() {
        val viewModel: ExtractorPeriodicWorkViewModel = koinViewModel()
        val isChecked by viewModel.doesPeriodicWorkExist.collectAsStateWithLifecycle()

        val navController = LocalNavController.current

        ExtractorPeriodicWorkScreen(
            checked = isChecked,
            onCheckedChange = viewModel::onCheckedChange,
            onBack = { navController.pop() }
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ExtractorPeriodicWorkScreen(
                checked = false,
                onCheckedChange = {},
                onBack = {}
            )
        }
    }
}