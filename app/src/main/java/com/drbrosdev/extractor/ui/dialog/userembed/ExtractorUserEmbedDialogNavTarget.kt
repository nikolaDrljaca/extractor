package com.drbrosdev.extractor.ui.dialog.userembed

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.framework.navigation.DialogNavTarget
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextFieldState
import com.drbrosdev.extractor.ui.imageinfo.UserEmbedUiModel
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import dev.olshevski.navigation.reimagined.NavController
import kotlinx.coroutines.delay
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

        val suggested by viewModel.suggestedEmbeddings.collectAsStateWithLifecycle()
        val context = LocalContext.current
        val toast = remember {
            Toast.makeText(context, context.getString(R.string.keyword_added), Toast.LENGTH_SHORT)
        }

        CollectFlow(flow = viewModel.events) {
            when (it) {
                ExtractorUserEmbedDialogEvents.KeywordAdded -> {
                    toast.show()
                    delay(1_000)
                    toast.cancel()
                }
            }
        }

        ExtractorUserEmbedDialog(
            suggestedEmbedsState = suggested,
            textFieldState = viewModel.embedTextFieldState,
            onCheck = viewModel::checkEmbedding,
            onCreateNew = viewModel::createNewUserEmbed,
        )
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
            )

            ExtractorUserEmbedDialog(
                suggestedEmbedsState = ExtractorSuggestedEmbedsUiState.Empty,
                textFieldState = ExtractorTextFieldState(),
                onCheck = {},
                onCreateNew = {},
            )
        }
    }
}