package com.drbrosdev.extractor.ui.dialog.userembed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.framework.navigation.DialogNavTarget
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextFieldState
import com.drbrosdev.extractor.ui.imageinfo.UserEmbedUiModel
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorUserEmbedDialogNavTarget(
    private val mediaImageId: Long
) : DialogNavTarget {

    @Composable
    override fun Content(navController: NavController<DialogNavTarget>) {
        val viewModel: ExtractorUserEmbedViewModel = koinViewModel {
            parametersOf(mediaImageId)
        }

        val suggested by viewModel.suggestedEmbeddingsState.collectAsStateWithLifecycle()

        CollectFlow(flow = viewModel.events) {
            when (it) {
                ExtractorUserEmbedDialogEvents.ChangesSaved -> navController.pop()
            }
        }

        ExtractorUserEmbedDialog(
            suggestedEmbedsState = suggested,
            textFieldState = viewModel.embedTextFieldState,
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
            suggestedEmbedsState = ExtractorSuggestedEmbedsUiState.Content(
                listOf(
                    UserEmbedUiModel("Some", false),
                    UserEmbedUiModel("Some", false),
                    UserEmbedUiModel("Some", false),
                    UserEmbedUiModel("Some", false),
                )
            ),
            textFieldState = ExtractorTextFieldState(),
            onCheck = {},
            onCreateNew = {},
            onSave = {},
        )
    }
}