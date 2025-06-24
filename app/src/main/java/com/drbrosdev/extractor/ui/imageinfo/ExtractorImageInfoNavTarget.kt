package com.drbrosdev.extractor.ui.imageinfo

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorImageInfoNavTarget(
    private val mediaImageId: Long
) : NavTarget {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: ExtractorImageInfoViewModel = koinViewModel {
            parametersOf(mediaImageId)
        }
        val state by viewModel.imageDetailState.collectAsStateWithLifecycle()

        when {
            state != null -> AppImageDetailScreen(
                modifier = Modifier.fillMaxSize(),
                model = state!!
            )

            else -> Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                LoadingIndicator()
            }
        }
    }
}

@ScreenPreview
@Composable
private fun CurrentScreenPreview() {
    val state = LupaImageDetailState(
        heading = LupaImageHeading(
            mediaImageId = 12123123,
            uri = Uri.EMPTY.toString(),
            dateAdded = "2025-01-01"
        ),
        description = "this is some description bababui",
        editables = LupaImageEditables(
            textEmbed = stringResource(R.string.lorem),
            visualEmbeds = Annotations(
                listOf(
                    "fizzbuzzbazzsssssewrwrw4we",
                    "foo",
                    "bar",
                    "baz"
                )
            ),
            userEmbeds =
                Annotations(
                    listOf(
                        "foo",
                        "bar",
                        "fizzbuzzbazzsssssewrwrw4we",
                        "fizz",
                        "buzz"
                    )
                ),
            {}
        )
    )
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppImageDetailScreen(
                modifier = Modifier.fillMaxHeight(),
                model = state
            )
        }
    }
}
