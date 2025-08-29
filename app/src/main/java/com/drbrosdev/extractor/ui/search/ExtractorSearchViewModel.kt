package com.drbrosdev.extractor.ui.search

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.LupaImage
import com.drbrosdev.extractor.domain.model.LupaImageMetadata
import com.drbrosdev.extractor.domain.model.asAlbumName
import com.drbrosdev.extractor.domain.model.search.ImageSearchParams
import com.drbrosdev.extractor.domain.model.search.SearchType
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.domain.usecase.search.SearchImages
import com.drbrosdev.extractor.framework.StringResourceProvider
import com.drbrosdev.extractor.framework.logger.logEvent
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.searchresult.SearchResultComponent
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetComponent
import com.drbrosdev.extractor.ui.yourspace.ExtractorYourSpaceNavTarget
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter

data class FilterModel(
    val keywordType: KeywordType = KeywordType.ALL,
    val searchType: SearchType = SearchType.PARTIAL,
    val dateRange: DateRange? = null,
    val isShown: Boolean = false
)

data class LupaSearchUiModel(
    val query: String = "",
    val searchResult: List<LupaImage> = emptyList(),
    val filter: FilterModel = FilterModel()
)

sealed interface LupaSearchMsg {
    data class QueryChange(val value: String) : LupaSearchMsg

    data class SearchResult(val value: List<LupaImage>) : LupaSearchMsg

    data object EmptyResult : LupaSearchMsg

    data object NoSearchesLeft : LupaSearchMsg

    data object ShowFilter : LupaSearchMsg

    data object HideFilter : LupaSearchMsg

    data object Search : LupaSearchMsg

    data object Reset: LupaSearchMsg

    data class FilterChange(
        val keywordType: KeywordType? = null,
        val searchType: SearchType? = null,
        val dateRange: DateRange? = null,
    ) : LupaSearchMsg
}

sealed interface LupaSearchCommand {
    data class Search(val params: ImageSearchParams) : LupaSearchCommand

    data object None : LupaSearchCommand
}

fun LupaSearchUiModel.searchParams() = ImageSearchParams(
    query = query,
    keywordType = filter.keywordType,
    dateRange = filter.dateRange,
    searchType = filter.searchType
)

fun LupaSearchUiModel.update(msg: LupaSearchMsg): Pair<LupaSearchUiModel, LupaSearchCommand> {
    return when (msg) {
        LupaSearchMsg.EmptyResult -> copy(searchResult = emptyList()) to LupaSearchCommand.None

        is LupaSearchMsg.FilterChange -> {
            val filter = filter.copy(
                keywordType = msg.keywordType ?: filter.keywordType,
                searchType = msg.searchType ?: filter.searchType,
                dateRange = msg.dateRange ?: filter.dateRange,
            )
            val current = copy(filter = filter)
            current to LupaSearchCommand.Search(searchParams())
        }

        LupaSearchMsg.HideFilter ->
            copy(filter = filter.copy(isShown = false)) to LupaSearchCommand.None

        LupaSearchMsg.ShowFilter ->
            copy(filter = filter.copy(isShown = true)) to LupaSearchCommand.None

        LupaSearchMsg.NoSearchesLeft -> copy(searchResult = emptyList()) to LupaSearchCommand.None

        is LupaSearchMsg.QueryChange -> copy(query = msg.value) to LupaSearchCommand.None

        is LupaSearchMsg.SearchResult -> copy(searchResult = msg.value) to LupaSearchCommand.None

        is LupaSearchMsg.Search -> this to LupaSearchCommand.Search(searchParams())

        is LupaSearchMsg.Reset -> copy(
            query = "",
            searchResult = emptyList(),
            filter = FilterModel()
        ) to LupaSearchCommand.None
    }
}

// the glue object -> runtime/container
// technically this would be the ViewModel - extend like the one below
// and things like navigation and snackbar handling can live in here
// outside the TEA stuff
class LupaSearchStore(
    private val scope: CoroutineScope,
    private val navigators: Navigators,
    private val imageSearch: SearchImages
) {
    private val _state = MutableStateFlow(LupaSearchUiModel())
    val state = _state.asStateFlow()

    fun dispatch(msg: LupaSearchMsg) {
        val (model, command) = _state.value.update(msg)
        _state.update { model }
        processCommand(command)
    }

    private fun processCommand(cmd: LupaSearchCommand) = scope.launch {
        when (cmd) {
            LupaSearchCommand.None -> Unit
            is LupaSearchCommand.Search -> imageSearch.execute(cmd.params)
                .fold(
                    ifLeft = {},
                    ifRight = { dispatch(LupaSearchMsg.SearchResult(it)) }
                )
        }
    }
}

class ExtractorSearchViewModel(
    private val stateHandle: SavedStateHandle,
    private val albumRepository: AlbumRepository,
    private val imageSearch: SearchImages,
    private val navigators: Navigators,
    private val args: SearchNavTargetArgs,
    private val stringProvider: StringResourceProvider
) : ViewModel() {

    val searchResultComponent = SearchResultComponent(
        coroutineScope = viewModelScope,
        navigators = navigators,
        searchImages = imageSearch::execute,
        createAlbum = ::compileUserAlbum
    )

    val searchSheetComponent = ExtractorSearchSheetComponent(
        stateHandle = stateHandle,
        onSearchEvent = ::performSearch
    )

    val snackbarHostState = SnackbarHostState()

    private val initArgsJob = flowOf(args)
        .onEach { handleSearchArgs(it) }
        .launchIn(viewModelScope)

    private fun handleSearchArgs(args: SearchNavTargetArgs) {
        when (args) {
            SearchNavTargetArgs.Empty -> viewModelScope.launch {
                delay(500L)
                searchSheetComponent.requestFocus()
            }

            is SearchNavTargetArgs.Args -> {
                val params = args.toSearchParams()
                searchSheetComponent.query.setTextAndPlaceCursorAtEnd(params.query)
                searchSheetComponent.keywordType = params.keywordType
                searchSheetComponent.searchType = params.searchType
                searchResultComponent.executeSearch(params)
            }
        }
    }

    private fun performSearch(params: ImageSearchParams) {
        searchResultComponent.executeSearch(params)
    }

    private fun compileUserAlbum(input: List<LupaImageMetadata>) {
        viewModelScope.launch {
            val searchData = searchSheetComponent.getSearchParamSnapshot()
            val name = when {
                searchData.query.isNotBlank() -> searchData.query
                searchData.dateRange != null -> searchData.dateRange.asAlbumName()
                else -> {
                    logEvent("ExtractorSearchViewModel.compileUserAlbum: Attempting to save an album with no name. Using timestamp.")
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                }
            }
            val albumEntries = input.map { NewAlbum.Entry(it.uri, it.mediaImageId) }
            val album = NewAlbum(
                name = name,
                keyword = searchData.query,
                searchType = searchData.searchType,
                keywordType = searchData.keywordType,
                origin = NewAlbum.Origin.USER_GENERATED,
                entries = albumEntries
            )
            albumRepository.createAlbum(album)
            val result = snackbarHostState.showSnackbar(
                message = stringProvider.get(R.string.album_created),
                actionLabel = stringProvider.get(R.string.snack_view),
                duration = SnackbarDuration.Long
            )
            when (result) {
                SnackbarResult.Dismissed -> Unit
                SnackbarResult.ActionPerformed -> navigators.navController.navigate(
                    ExtractorYourSpaceNavTarget
                )
            }
        }
    }
}
