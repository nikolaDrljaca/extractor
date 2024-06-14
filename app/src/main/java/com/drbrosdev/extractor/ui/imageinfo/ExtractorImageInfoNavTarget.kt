package com.drbrosdev.extractor.ui.imageinfo

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.framework.navigation.BottomSheetNavTarget
import com.drbrosdev.extractor.framework.navigation.DialogNavTarget
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextFieldState
import com.drbrosdev.extractor.ui.dialog.userembed.ExtractorUserEmbedDialogNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.material.BottomSheetState
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorImageInfoNavTarget(
    val mediaImageId: Long
) : BottomSheetNavTarget {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(
        sheetState: BottomSheetState,
        dialogNavController: NavController<DialogNavTarget>,
        sheetNavController: NavController<BottomSheetNavTarget>
    ) {
        val viewModel: ExtractorImageInfoViewModel = koinViewModel {
            parametersOf(mediaImageId)
        }
        val imageInfoModel by viewModel.imageInfoModel.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = Unit) {
            sheetState.expand()
        }

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
                    sheetNavController.pop()
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
