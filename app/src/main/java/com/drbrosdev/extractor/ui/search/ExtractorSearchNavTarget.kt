package com.drbrosdev.extractor.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.domain.usecase.LabelType
import com.drbrosdev.extractor.ui.components.datafilterchip.toLabelType
import com.drbrosdev.extractor.ui.image.ExtractorImageNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorSearchNavTarget(
    private val query: String,
    private val labelType: LabelType
) : NavTarget {

    @Composable
    override fun Content() {
        val viewModel: ExtractorSearchViewModel = koinViewModel {
            parametersOf(query, labelType)
        }
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navController = LocalNavController.current

        //TODO: Loading state, animated placeholders or spinners
        ExtractorSearchScreen(
            state = state,
            onNavToDetail = { selectedIndex ->
                navController.navigate(
                    ExtractorImageNavTarget(
                        images = viewModel.getImageUris(),
                        initialIndex = selectedIndex
                    )
                )
            },
            onFilterChanged = { viewModel.setLabelType(it.toLabelType()) }
        )
    }
}

@ScreenPreview
@Composable
private fun SearchScreenPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorSearchScreen(
            onFilterChanged = {},
            state = ExtractorSearchScreenUiState.Loading(
                searchTerm = "",
                labelType = LabelType.ALL
            ),
            onNavToDetail = {},
        )
    }
}
