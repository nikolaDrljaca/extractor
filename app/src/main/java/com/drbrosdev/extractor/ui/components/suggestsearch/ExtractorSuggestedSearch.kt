package com.drbrosdev.extractor.ui.components.suggestsearch

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.SuggestedSearch
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview
import com.drbrosdev.extractor.util.shimmer

@Composable
fun SuggestedSearches(
    modifier: Modifier = Modifier,
    suggestionUiModel: SuggestedSearchUiModel
) {
    AnimatedContent(
        targetState = suggestionUiModel,
        modifier = Modifier
            .padding(top = 8.dp)
            .then(modifier)
    ) { content ->
        when (content) {
            SuggestedSearchUiModel.Loading ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .padding(horizontal = 16.dp)
                        .clip(CircleShape)
                        .shimmer()
                )

            is SuggestedSearchUiModel.Content ->
                LazyRow(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(content.suggestions) {
                        SuggestedSearchItem(
                            onClick = { search -> content.onSuggestionClick(search) },
                            suggestedSearch = it
                        )
                    }
                }
        }
    }
}

@Composable
private fun SuggestedSearchItem(
    modifier: Modifier = Modifier,
    onClick: (SuggestedSearch) -> Unit,
    suggestedSearch: SuggestedSearch
) {
    val icon = when (suggestedSearch.keywordType) {
        KeywordType.ALL -> painterResource(R.drawable.round_tag_24)
        KeywordType.TEXT -> painterResource(R.drawable.round_text_fields_24)
        KeywordType.IMAGE -> painterResource(R.drawable.round_image_search_24)
    }
    SuggestionChip(
        modifier = Modifier
            .then(modifier),
        onClick = { onClick(suggestedSearch) },
        label = { Text(suggestedSearch.query) },
        icon = { Icon(icon, "") },
        shape = CircleShape
    )
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
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            SuggestedSearches(
                suggestionUiModel = SuggestedSearchUiModel.Content(
                    onSuggestionClick = {},
                    list
                )
            )
        }
    }
}
