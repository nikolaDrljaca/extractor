package com.drbrosdev.extractor.ui.components.extractorsearchview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import kotlinx.coroutines.flow.Flow


@Stable
class ExtractorSearchViewState(
    initialQuery: String,
    initialKeywordType: KeywordType,
    initialSearchType: SearchType = SearchType.PARTIAL
) {
    var query by mutableStateOf(initialQuery)
        private set

    var keywordType by mutableStateOf(initialKeywordType)
        private set

    var searchType by mutableStateOf(initialSearchType)
        private set

    inline fun updateQuery(block: (String) -> String) {
        val new = block(query)
        updateQuery(new)
    }

    fun updateSearchType(value: SearchType) {
        searchType = value
    }

    fun updateQuery(new: String) {
        query = new
    }

    fun updateKeywordType(new: KeywordType) {
        keywordType = new
    }

    companion object {
        fun Saver() = androidx.compose.runtime.saveable.Saver<ExtractorSearchViewState, String>(
            save = { it.query },
            restore = {
                ExtractorSearchViewState(
                    initialQuery = it,
                    initialKeywordType = KeywordType.ALL,
                    initialSearchType = SearchType.PARTIAL
                )
            }
        )
    }
}

fun ExtractorSearchViewState.isBlank(): Boolean {
    return query.isBlank()
}

fun ExtractorSearchViewState.isNotBlank(): Boolean {
    return !isBlank()
}

fun ExtractorSearchViewState.initialLabelTypeIndex(): Int {
    return when (keywordType) {
        KeywordType.ALL -> 0
        KeywordType.TEXT -> 1
        KeywordType.IMAGE -> 2
    }
}


@Composable
fun rememberExtractorSearchViewState(
    initialQuery: String,
    initialKeywordType: KeywordType,
    initialSearchType: SearchType = SearchType.PARTIAL
): ExtractorSearchViewState {
    return rememberSaveable(saver = ExtractorSearchViewState.Saver()) {
        ExtractorSearchViewState(
            initialQuery = initialQuery,
            initialKeywordType = initialKeywordType,
            initialSearchType = initialSearchType
        )
    }
}

fun ExtractorSearchViewState.queryAsFlow(): Flow<String> {
    return snapshotFlow { this.query }
}

fun ExtractorSearchViewState.keywordTypeAsFlow(): Flow<KeywordType> {
    return snapshotFlow { this.keywordType }
}

fun ExtractorSearchViewState.searchTypeAsFlow(): Flow<SearchType> {
    return snapshotFlow { this.searchType }
}