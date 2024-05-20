package com.drbrosdev.extractor.ui.dialog.userembed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.framework.navigation.DialogNavTarget
import com.drbrosdev.extractor.framework.navigation.LocalDialogNavController
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextFieldState
import com.drbrosdev.extractor.ui.imageinfo.UserEmbedUiModel
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorUserEmbedDialogNavTarget(
    private val mediaImageId: Long
) : DialogNavTarget {

    @Composable
    override fun Content() {
        val viewModel: ExtractorUserEmbedViewModel = koinViewModel {
            parametersOf(mediaImageId)
        }
        val dialogNavController = LocalDialogNavController.current

        val suggested by viewModel.suggestedEmbeddingsState.collectAsStateWithLifecycle()
        val loading by viewModel.loading.collectAsStateWithLifecycle()

        CollectFlow(flow = viewModel.events) {
            when (it) {
                ExtractorUserEmbedDialogEvents.ChangesSaved -> dialogNavController.pop()
            }
        }

        ExtractorUserEmbedDialog(
            suggestedKeywords = suggested,
            textFieldState = viewModel.embedTextFieldState,
            loading = loading,
            onCheck = viewModel::checkEmbedding,
            onCreateNew = viewModel::createNewUserEmbed,
            onSave = viewModel::saveChanges
        )
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        ExtractorUserEmbedDialog(
            suggestedKeywords = listOf(
                UserEmbedUiModel("Some", false),
                UserEmbedUiModel("Some", false),
                UserEmbedUiModel("Some", false),
                UserEmbedUiModel("Some", false),
            ),
            textFieldState = ExtractorTextFieldState(),
            loading = true,
            onCheck = {},
            onCreateNew = {},
            onSave = {},
        )
    }
}