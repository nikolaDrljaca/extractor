package com.drbrosdev.extractor.ui.extractorimageinfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.drbrosdev.extractor.util.BottomSheetNavTarget
import com.drbrosdev.extractor.util.LocalBottomSheetNavController
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