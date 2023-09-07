package com.drbrosdev.extractor.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.drbrosdev.extractor.ui.result.SearchResultNavTarget
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object HomeNavTarget : NavTarget {

    @Composable
    override fun Content() {
        val viewModel: HomeViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val navController = LocalNavController.current
        val keyboardController = LocalSoftwareKeyboardController.current

        HomeScreen(
            state = state,
            onEvent = { event ->
                when (event) {
                    is HomeScreenEvents.PerformSearch -> {
                        keyboardController?.hide()
                        navController.navigate(SearchResultNavTarget(event.query))
                    }

                    is HomeScreenEvents.OnDeleteSearch ->
                        viewModel.deletePreviousSearch(event.value)

                    HomeScreenEvents.OnNavToAbout -> {}
                }
            },
        )
    }
}