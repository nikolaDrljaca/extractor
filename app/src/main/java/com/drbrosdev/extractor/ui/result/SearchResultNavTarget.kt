package com.drbrosdev.extractor.ui.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.domain.usecase.LabelType
import com.drbrosdev.extractor.ui.components.datafilterchip.toLabelType
import com.drbrosdev.extractor.ui.image.ImageDetailNavTarget
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class SearchResultNavTarget(
    private val query: String,
    private val labelType: LabelType
) : NavTarget {

    @Composable
    override fun Content() {
        val viewModel: SearchResultViewModel = koinViewModel {
            parametersOf(query, labelType)
        }
        val state by viewModel.state.collectAsStateWithLifecycle()
        val navController = LocalNavController.current

        //TODO: Loading state, animated placeholders or spinners
        SearchResultScreen(
            state = state,
            onNavToDetail = { selectedIndex ->
                navController.navigate(
                    ImageDetailNavTarget(
                        images = viewModel.getImageUris(),
                        initialIndex = selectedIndex
                    )
                )
            },
            onFilterChanged = { viewModel.setLabelType(it.toLabelType()) }
        )
    }
}