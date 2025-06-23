package com.drbrosdev.extractor.ui.imageinfo

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextFieldState
import com.drbrosdev.extractor.ui.dialog.userembed.ExtractorUserEmbedDialogNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorImageInfoNavTarget(
    private val mediaImageId: Long
) : NavTarget {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: ExtractorImageInfoViewModel = koinViewModel {
            parametersOf(mediaImageId)
        }
        val imageInfoModel by viewModel.imageInfoModel.collectAsStateWithLifecycle()
        val dialogNavController = navigators.dialogNavController

        Surface(
            tonalElevation = BottomSheetDefaults.Elevation,
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(18.dp)
        ) {
            ExtractorImageInfoScreen(
                model = imageInfoModel,
                textEmbedState = viewModel.textEmbedding,
                onClearVisual = viewModel::clearVisualEmbedding,
                onSaveEmbeddings = {
                    viewModel.saveEmbeddings()
                },
                onClearUser = viewModel::updateUserEmbedding,
                onAddNewUser = {
                    dialogNavController.navigate(ExtractorUserEmbedDialogNavTarget(mediaImageId))
                }
            )
        }
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtractorImageInfoScreen(
                model = ExtractorImageInfoUiState(),
                textEmbedState = ExtractorTextFieldState(""),
                onClearVisual = {},
                onSaveEmbeddings = {},
                onClearUser = {},
                onAddNewUser = {}
            )
        }
    }
}
