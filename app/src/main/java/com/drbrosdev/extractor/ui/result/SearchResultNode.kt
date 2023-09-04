package com.drbrosdev.extractor.ui.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.drbrosdev.extractor.util.NavTarget
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Parcelize
data class SearchResultNavTarget(private val query: String) : NavTarget {

    @Composable
    override fun Content() {
        val viewModel: SearchResultViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(key1 = Unit) {
            viewModel.performSearch(query)
        }

        SearchResultScreen(
            state = state
        )
    }
}