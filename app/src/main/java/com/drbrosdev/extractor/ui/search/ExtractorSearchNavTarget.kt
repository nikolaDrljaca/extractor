package com.drbrosdev.extractor.ui.search

import android.os.Parcelable
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.ImageSearchParams
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetComponent
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime

@Parcelize
data class ExtractorSearchNavTarget(val args: SearchNavTargetArgs? = null) : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: ExtractorSearchViewModel = koinViewModel()

        ExtractorSearchScreen(
            searchSheetComponent = viewModel.searchSheetState
        )
    }
}

@Parcelize
data class SearchNavTargetArgs(
    val query: String,
    val keywordType: KeywordType,
    val startRange: String?,
    val endRange: String?,
    val searchType: SearchType
) : Parcelable

fun SearchNavTargetArgs.toSearchParams(): ImageSearchParams {
    val dateRange = when {
        startRange != null && endRange != null -> DateRange(
            start = LocalDateTime.parse(startRange),
            end = LocalDateTime.parse(endRange)
        )
        else -> null
    }
    return ImageSearchParams(
        query = query,
        keywordType = keywordType,
        searchType = searchType,
        dateRange = dateRange
    )
}

fun ImageSearchParams.asSearchNavTargetArgs() = SearchNavTargetArgs(
    query = query,
    keywordType = keywordType,
    searchType = searchType,
    startRange = dateRange?.start?.toString(),
    endRange = dateRange?.end?.toString()
)

@ScreenPreview
@Composable
private fun SearchScreenPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface {
            ExtractorSearchScreen(
                searchSheetComponent = ExtractorSearchSheetComponent({}, SavedStateHandle())
            )
        }
    }
}
