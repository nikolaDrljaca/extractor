package com.drbrosdev.extractor.ui.components.suggestsearch

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.SuggestedSearch
import com.drbrosdev.extractor.ui.components.shared.ContentPlaceholder
import com.drbrosdev.extractor.ui.components.shared.OutlinedExtractorActionButton
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@Composable
fun ExtractorSuggestedSearch(
    modifier: Modifier = Modifier,
    state: ExtractorSuggestedSearchState
) {
    AnimatedContent(
        targetState = state,
        label = "",
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        }
    ) {
        Column(
            modifier = Modifier.then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (it) {
                is ExtractorSuggestedSearchState.Content -> SuggestedSearchContent(
                    onClick = { search -> it.onSuggestionClick(search) },
                    suggestedSearches = it.suggestedSearches
                )

                ExtractorSuggestedSearchState.Loading -> SuggestedSearchLoading(
                    numberOfItems = 6
                )

                is ExtractorSuggestedSearchState.Empty -> SuggestedSearchEmpty(
                    onClick = { it.onStartSync() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
private fun SuggestedSearchContent(
    modifier: Modifier = Modifier,
    onClick: (SuggestedSearch) -> Unit,
    suggestedSearches: List<SuggestedSearch>
) {
    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.start_your_search_here),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.suggestion_info),
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(suggestedSearches) {
                SuggestedSearchItem(
                    onClick = { onClick(it) },
                    suggestedSearch = it
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.or_start_a_search_below),
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun SuggestedSearchEmpty(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.suggestion_data_reset),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.suggestion_start_sync),
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedExtractorActionButton(onClick = onClick) {
            Icon(
                painter = painterResource(id = R.drawable.round_sync_24),
                contentDescription = stringResource(R.string.start_sync)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.start_sync))
        }
    }
}

@Composable
private fun SuggestedSearchLoading(
    modifier: Modifier = Modifier,
    numberOfItems: Int = 4,
) {
    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ContentPlaceholder(height = 26, modifier = Modifier.fillMaxWidth(0.5f))
        Spacer(modifier = Modifier.height(2.dp))
        ContentPlaceholder(height = 18, modifier = Modifier.fillMaxWidth(0.7f))

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(numberOfItems) {
                ContentPlaceholder()
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ContentPlaceholder(height = 24)
    }
}

@Immutable
data class SuggestedSearchUiModel(
    val query: String,
    val keywordType: KeywordType,
    val searchType: SearchType
)

@Composable
fun SuggestedSearchItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    suggestedSearch: SuggestedSearchUiModel
) {
    val icon = when (suggestedSearch.keywordType) {
        KeywordType.ALL -> painterResource(R.drawable.round_tag_24)
        KeywordType.TEXT -> painterResource(R.drawable.round_text_fields_24)
        KeywordType.IMAGE -> painterResource(R.drawable.round_image_search_24)
    }
    SuggestionChip(
        modifier = Modifier
            .then(modifier),
        onClick = onClick,
        label = { Text(suggestedSearch.query) },
        icon = { Icon(icon, "") },
        shape = CircleShape
    )
}

@Composable
private fun SuggestedSearchItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    suggestedSearch: SuggestedSearch
) {
    OutlinedCard(
        modifier = Modifier
            .height(56.dp)
            .then(modifier),
        onClick = onClick,
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = suggestedSearch.query,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${suggestedSearch.keywordType.name.lowercase()} \u00B7 ${suggestedSearch.searchType.name.lowercase()}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray
                )
            )
        }
    }
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        val suggestedSearch = SuggestedSearch(
            query = "some",
            keywordType = KeywordType.IMAGE,
            searchType = SearchType.FULL
        )
        val list = (1..6).map { suggestedSearch }
        val contentState = ExtractorSuggestedSearchState.Content({}, list)
        val loadingState = ExtractorSuggestedSearchState.Loading
        val emptyState = ExtractorSuggestedSearchState.Empty({})

        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SuggestedSearchItem(
                    onClick = {}, suggestedSearch = SuggestedSearchUiModel(
                        query = "Sample",
                        keywordType = KeywordType.IMAGE,
                        searchType = SearchType.FULL
                    )
                )
//                ExtractorSuggestedSearch(
//                    modifier = Modifier.fillMaxWidth(),
//                    state = contentState
//                )
//
//                ExtractorSuggestedSearch(
//                    modifier = Modifier.fillMaxWidth(),
//                    state = loadingState
//                )
//
//                ExtractorSuggestedSearch(
//                    modifier = Modifier.fillMaxWidth(),
//                    state = emptyState
//                )
            }
        }
    }
}
