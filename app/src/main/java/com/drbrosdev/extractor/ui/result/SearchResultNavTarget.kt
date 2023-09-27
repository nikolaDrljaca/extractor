package com.drbrosdev.extractor.ui.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.drbrosdev.extractor.domain.usecase.LabelType
import com.drbrosdev.extractor.ui.image.ImageDetailNavTarget
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Parcelize
data class SearchResultNavTarget(
    private val query: String,
    private val labelType: LabelType
) : NavTarget {

    @Composable
    override fun Content() {
        val viewModel: SearchResultViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navController = LocalNavController.current

        LaunchedEffect(key1 = Unit) {
            viewModel.performSearch(query, labelType)
        }

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
            onNavBack = { navController.pop() }
        )
    }
}