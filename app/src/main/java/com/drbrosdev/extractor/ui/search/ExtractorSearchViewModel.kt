package com.drbrosdev.extractor.ui.search

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SuggestedSearch
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.domain.usecase.ExtractionProgress
import com.drbrosdev.extractor.domain.usecase.ExtractionStatus
import com.drbrosdev.extractor.domain.usecase.GenerateSuggestedKeywords
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import com.drbrosdev.extractor.domain.usecase.image.search.ImageSearchByKeyword
import com.drbrosdev.extractor.domain.usecase.image.search.SearchStrategy
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilterState
import com.drbrosdev.extractor.ui.components.extractordatefilter.dateRange
import com.drbrosdev.extractor.ui.components.extractordatefilter.dateRangeAsFlow
import com.drbrosdev.extractor.ui.components.extractorloaderbutton.ExtractorLoaderButtonState
import com.drbrosdev.extractor.ui.components.extractorloaderbutton.isSuccess
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import com.drbrosdev.extractor.ui.components.extractorsearchview.isBlank
import com.drbrosdev.extractor.ui.components.extractorsearchview.isNotBlank
import com.drbrosdev.extractor.ui.components.extractorsearchview.keywordTypeAsFlow
import com.drbrosdev.extractor.ui.components.extractorsearchview.queryAsFlow
import com.drbrosdev.extractor.ui.components.extractorsearchview.searchTypeAsFlow
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonState
import com.drbrosdev.extractor.ui.components.suggestsearch.ExtractorSuggestedSearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorSearchViewModel(
    query: String,
    keywordType: KeywordType,
    private val imageSearch: ImageSearchByKeyword,
    private val extractionProgress: ExtractionProgress,
    private val albumRepository: AlbumRepository,
    private val stateHandle: SavedStateHandle,
    private val generateSuggestedKeywords: GenerateSuggestedKeywords,
    private val spawnExtractorWork: SpawnExtractorWork,
    private val datastore: ExtractorDataStore
) : ViewModel() {
    val extractorStatusButtonState = ExtractorStatusButtonState()

    val searchViewState = ExtractorSearchViewState(
        initialQuery = stateHandle["query"] ?: query,
        initialKeywordType = keywordType,
    )

    val dateFilterState = ExtractorDateFilterState()

    val loaderButtonState = ExtractorLoaderButtonState()

    private val _state = MutableStateFlow<ExtractorSearchScreenUiState>(
        ExtractorSearchScreenUiState.ShowSuggestions(
            ExtractorSuggestedSearchState.Loading
        )
    )
    val state = _state.asStateFlow()

    private val lastQuery = MutableStateFlow(LastQuery(query, keywordType))

    val shouldShowSheetFlow = datastore.shouldShowSearchSheet

    private val progressJob = extractionProgress()
        .distinctUntilChanged()
        //handle create album button state
        .onEach {
            val status = when (it) {
                is ExtractionStatus.Done -> when {
                    it.isDataIncomplete -> ExtractorStatusButtonState.Status.OutOfSync
                    else -> ExtractorStatusButtonState.Status.Idle
                }

                is ExtractionStatus.Running -> ExtractorStatusButtonState.Status.ExtractionRunning(
                    it.percentage
                )
            }
            extractorStatusButtonState.update(status)
        }
        //handle show suggestions
        .onEach {
            when (it) {
                is ExtractionStatus.Done -> {
                    when {
                        ((it.inStorageCount != 0) and (_state.value !is ExtractorSearchScreenUiState.Content)) -> {
                            val suggested = generateSuggestedKeywords()
                            _state.update {
                                ExtractorSearchScreenUiState.ShowSuggestions(
                                    ExtractorSuggestedSearchState.Content(suggested.take(6))
                                )
                            }
                        }

                        else -> {
                            _state.update {
                                ExtractorSearchScreenUiState.ShowSuggestions(
                                    ExtractorSuggestedSearchState.Empty
                                )
                            }
                        }
                    }
                }

                is ExtractionStatus.Running -> _state.update {
                    ExtractorSearchScreenUiState.StillIndexing
                }
            }

        }
        .launchIn(viewModelScope)

    private val labelTypeUpdateFlow = searchViewState.keywordTypeAsFlow()
        .distinctUntilChanged()
        .onEach { performSearch(SearchStrategy.NORMAL) }
        .onEach { loaderButtonState.initial() }
        .launchIn(viewModelScope)

    private val searchTypeUpdateFlow = searchViewState.searchTypeAsFlow()
        .distinctUntilChanged()
        .onEach { performSearch(SearchStrategy.NORMAL) }
        .onEach { loaderButtonState.initial() }
        .launchIn(viewModelScope)

    private val saveQueryJob = searchViewState.queryAsFlow()
        .distinctUntilChanged()
        .onEach { stateHandle["query"] = it }
        .launchIn(viewModelScope)

    private val dateFilterJob = dateFilterState.dateRangeAsFlow()
        .distinctUntilChanged()
        .onEach { performSearch(SearchStrategy.NORMAL) }
        .launchIn(viewModelScope)

    private val loaderButtonEnabledJob = state
        .onEach {
            when (it) {
                is ExtractorSearchScreenUiState.Content -> {
                    when {
                        searchViewState.isNotBlank() -> loaderButtonState.enable()
                        else -> loaderButtonState.disable()
                    }
                }

                else -> loaderButtonState.disable()
            }
        }
        .launchIn(viewModelScope)

    fun performSearch(searchStrategy: SearchStrategy) {
        when (searchStrategy) {
            SearchStrategy.NORMAL -> runSearch()
            SearchStrategy.DIRTY_CHECKING -> {
                if (searchViewState.isBlank()) return
                if (
                    lastQuery.value.isOldQuery(
                        searchViewState.query,
                        searchViewState.keywordType
                    )
                ) return
                runSearch()
            }
        }
    }

    fun getImageUris(): List<Uri> {
        return when (val out = state.value) {
            is ExtractorSearchScreenUiState.Content -> out.images.map { it.uri.uri.toUri() }
            else -> emptyList()
        }
    }

    fun onCompileUserAlbum() {
        when (state.value) {
            is ExtractorSearchScreenUiState.Content -> compileUserAlbum()
            else -> Unit
        }
    }

    private fun compileUserAlbum() {
        if (loaderButtonState.isSuccess()) return

        loaderButtonState.loading()
        viewModelScope.launch {
            val newAlbum = NewAlbum(
                keyword = searchViewState.query,
                name = searchViewState.query,
                searchType = searchViewState.searchType,
                keywordType = searchViewState.keywordType,
                origin = NewAlbum.Origin.USER_GENERATED,
                entries = state.value.getImages().map {
                    NewAlbum.Entry(
                        uri = it.uri,
                        id = it.mediaImageId
                    )
                }
            )
            albumRepository.createAlbum(newAlbum)
        }
            .invokeOnCompletion {
                loaderButtonState.success()
            }
    }

    private fun runSearch() {
        if (searchViewState.isBlank()) return
        if (_state.value is ExtractorSearchScreenUiState.StillIndexing) return

        _state.update { ExtractorSearchScreenUiState.Loading }

        viewModelScope.launch {
            val searchQuery = ImageSearchByKeyword.Params(
                query = searchViewState.query,
                keywordType = searchViewState.keywordType,
                dateRange = dateFilterState.dateRange(),
                type = searchViewState.searchType
            )

            val result = imageSearch.search(searchQuery).also {
                lastQuery.update { LastQuery(searchViewState.query, searchViewState.keywordType) }
            }

            _state.createFrom(result)
        }
    }

    private data class LastQuery(val query: String, val keywordType: KeywordType)

    private fun LastQuery.isOldQuery(query: String, keywordType: KeywordType): Boolean {
        return when {
            (query == this.query) and (keywordType == this.keywordType) -> true
            else -> false
        }
    }

    fun performSuggestedSearch(suggestedSearch: SuggestedSearch) = with(suggestedSearch) {
        _state.update { ExtractorSearchScreenUiState.Loading }

        viewModelScope.launch {
            val searchQuery = ImageSearchByKeyword.Params(
                query = query,
                keywordType = keywordType,
                dateRange = null,
                type = searchType
            )

            val result = imageSearch.search(searchQuery).also {
                lastQuery.update { LastQuery(searchViewState.query, searchViewState.keywordType) }
            }

            _state.createFrom(result)
        }
    }

    fun spawnWork() = spawnExtractorWork()

    fun onShowSheetDone() {
        viewModelScope.launch {
            datastore.hasSeenSearchSheet()
        }
    }

    fun resetSearch() {
        _state.update {
            ExtractorSearchScreenUiState.ShowSuggestions(
                ExtractorSuggestedSearchState.Loading
            )
        }
        viewModelScope.launch {
            val suggested = generateSuggestedKeywords()
            _state.update {
                ExtractorSearchScreenUiState.ShowSuggestions(
                    ExtractorSuggestedSearchState.Content(suggested.take(6))
                )
            }
        }
    }
}

