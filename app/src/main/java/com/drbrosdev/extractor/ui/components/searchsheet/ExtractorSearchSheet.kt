package com.drbrosdev.extractor.ui.components.searchsheet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.saveable
import arrow.core.Option
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilter
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilterState
import com.drbrosdev.extractor.ui.components.extractorloaderbutton.ExtractorLoaderButton
import com.drbrosdev.extractor.ui.components.extractorloaderbutton.ExtractorLoaderButtonState
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchView
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

sealed interface ExtractorSearchSheetEvents {
    data object OnSearchClick : ExtractorSearchSheetEvents

    data class OnKeywordTypeChange(
        val keywordType: KeywordType
    ) : ExtractorSearchSheetEvents

    data class OnSearchTypeChange(
        val searchType: SearchType
    ) : ExtractorSearchSheetEvents

    data object OnResetDate : ExtractorSearchSheetEvents

    data class OnDateChange(
        val startDate: Option<Long>,
        val endDate: Option<Long>
    ) : ExtractorSearchSheetEvents
}


class ExtractorSearchSheetState(
    initialQuery: String,
    initialKeywordType: KeywordType,
    disabled: Boolean
) {
    val searchViewState = ExtractorSearchViewState(
        initialQuery = initialQuery,
        initialKeywordType = initialKeywordType
    )

    val dateFilterState = ExtractorDateFilterState()

    var disabled by mutableStateOf(disabled)
        private set

    fun enable() {
        disabled = false
    }

    fun disable() {
        disabled = true
    }

    companion object {
        val Saver = object : Saver<ExtractorSearchSheetState, Map<String, Any>> {
            override fun restore(value: Map<String, Any>): ExtractorSearchSheetState {
                return ExtractorSearchSheetState(
                    initialQuery = value.getOrDefault("query", "") as String,
                    initialKeywordType = value.getOrDefault(
                        "keywordType",
                        KeywordType.ALL
                    ) as KeywordType,
                    disabled = value.getOrDefault("sheet_disabled", false) as Boolean
                )
            }

            override fun SaverScope.save(value: ExtractorSearchSheetState): Map<String, Any> {
                return mapOf(
                    "query" to value.searchViewState.query,
                    "keywordType" to value.searchViewState.keywordType,
                    "sheet_disabled" to value.disabled
                )
            }

        }

        fun fromSavedStateHandle(handle: SavedStateHandle): ExtractorSearchSheetState {
            return handle.saveable(
                key = "extractor_search_sheet",
                saver = Saver
            ) {
                ExtractorSearchSheetState(
                    initialQuery = "",
                    initialKeywordType = KeywordType.ALL,
                    disabled = false
                )
            }
        }
    }
}

@Composable
fun ExtractorSearchSheet(
    modifier: Modifier = Modifier,
    isHidden: Boolean = false,
    onEvent: (ExtractorSearchSheetEvents) -> Unit,
    state: ExtractorSearchSheetState,
) {
    val alphaOffset by animateFloatAsState(targetValue = if (isHidden) 0f else 1f, label = "")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExtractorSearchView(
            state = state.searchViewState,
            onDone = {
                onEvent(ExtractorSearchSheetEvents.OnSearchClick)
            },
            onKeywordTypeChange = {
                onEvent(ExtractorSearchSheetEvents.OnKeywordTypeChange(it))
            },
            onSearchTypeChange = {
                onEvent(ExtractorSearchSheetEvents.OnSearchTypeChange(it))
            }
        )

        ExtractorDateFilter(
            modifier = Modifier.graphicsLayer {
                alpha = alphaOffset
            },
            state = state.dateFilterState,
            onResetDate = {
                onEvent(ExtractorSearchSheetEvents.OnResetDate)
            },
            onDateChanged = { start, end ->
                onEvent(
                    ExtractorSearchSheetEvents.OnDateChange(
                        startDate = Option.fromNullable(start),
                        endDate = Option.fromNullable(end)
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun ExtractorSearchSheet(
    modifier: Modifier = Modifier,
    isHidden: Boolean = false,
    onDone: () -> Unit,
    onCreateAlbumClick: () -> Unit,
    searchViewState: ExtractorSearchViewState,
    dateFilterState: ExtractorDateFilterState,
    loaderButtonState: ExtractorLoaderButtonState,
) {
    val alphaOffset by animateFloatAsState(targetValue = if (isHidden) 0f else 1f, label = "")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExtractorSearchView(
            onDone = onDone,
            state = searchViewState,
            isHidden = isHidden,
            contentPadding = PaddingValues(),
            textFieldPadding = PaddingValues(bottom = 16.dp),
            onSearchTypeChange = {},
            onKeywordTypeChange = {}
        )

        Column(
            modifier = Modifier.graphicsLayer {
                alpha = alphaOffset
            }
        ) {
            ExtractorDateFilter(
                state = dateFilterState,
                onDateChanged = { _, _ -> },
                onResetDate = {}
            )

            Spacer(modifier = Modifier.height(14.dp))

            ExtractorLoaderButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onCreateAlbumClick,
                state = loaderButtonState,
                loadingContent = {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        trackColor = Color.Transparent,
                        color = Color.Black,
                        strokeCap = StrokeCap.Round
                    )
                },
                successContent = {
                    Icon(imageVector = Icons.Rounded.Check, contentDescription = "")
                    Text(text = stringResource(R.string.album_created))
                }
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "")
                Text(text = stringResource(R.string.create_album))
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}


@Preview
@Composable
private fun SheetPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            ExtractorSearchSheet(
                onDone = {},
                onCreateAlbumClick = {},
                searchViewState = ExtractorSearchViewState("", KeywordType.ALL),
                dateFilterState = ExtractorDateFilterState(),
                loaderButtonState = ExtractorLoaderButtonState()
            )
        }
    }
}
