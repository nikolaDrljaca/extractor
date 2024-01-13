package com.drbrosdev.extractor.ui.imageinfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.BottomSheetNavTarget
import com.drbrosdev.extractor.util.LocalBottomSheetNavController
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.material.BottomSheetState
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorImageInfoNavTarget(
    val mediaImageId: Long
) : BottomSheetNavTarget {

    @Composable
    override fun Content(sheetState: BottomSheetState) {
        val viewModel: ExtractorImageInfoViewModel = koinViewModel {
            parametersOf(mediaImageId)
        }
        val imageInfoModel by viewModel.imageInfoModel.collectAsState()
        val sheetNavigator = LocalBottomSheetNavController.current

        LaunchedEffect(key1 = Unit) {
            sheetState.expand()
        }

        ExtractorImageInfoScreen(
            model = imageInfoModel,
            onClearVisual = { viewModel.clearVisualEmbedding(it) },
            onSaveEmbeddings = {
                viewModel.saveEmbeddings()
                sheetNavigator.pop()
            }
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorImageInfoScreen(
            modifier = Modifier,
            model = ExtractorImageInfoUiState(),
            onClearVisual = {},
            onSaveEmbeddings = {},
        )
    }
}
