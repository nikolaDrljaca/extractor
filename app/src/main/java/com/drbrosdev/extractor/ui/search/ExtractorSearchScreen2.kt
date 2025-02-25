package com.drbrosdev.extractor.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.ui.components.shared.ExtractorSearchPill
import com.drbrosdev.extractor.ui.components.statuspill.ExtractorStatusPill
import com.drbrosdev.extractor.ui.components.statuspill.ExtractorStatusPillState
import com.drbrosdev.extractor.ui.components.suggestsearch.SuggestedSearchItem
import com.drbrosdev.extractor.ui.components.suggestsearch.SuggestedSearchUiModel
import com.drbrosdev.extractor.ui.components.usercollage.ExtractorUserCollageItem
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview
import java.time.LocalDateTime

@Composable
fun ExtractorSearchScreen2(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
        constraintSet = searchScreenConstraintSet()
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                .layoutId(ViewIds2.TOP_BAR),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // make clickable -- go to searches page -- show sync status and details
            // below that show search count and how to get more
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Lupa",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                )
                ExtractorStatusPill(state = ExtractorStatusPillState.OutOfSync)
            }

            Surface(
                onClick = {},
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                // Go to albums screen - current home screen
                Icon(
                    painter = painterResource(id = R.drawable.round_grid_view_24),
                    contentDescription = "Go Home",
                    modifier = Modifier
                        .size(32.dp)
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // scrollable content
        LazyColumn(
            modifier = Modifier
                .layoutId(ViewIds2.MAIN_CONTENT),
        ) {
            item { Spacer(Modifier.height(42.dp)) }
            // search bar -- total rework
            item {
                ExtractorSearchPill(
                    text = "Search",
                    onChange = {},
                    onDoneSubmit = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            // suggest searches using keywords/algorithm
            // that is used when generating albums based on most common keywords
            // clicking these navigates to search and uses this to search
            item {
                LazyRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(4) {
                        SuggestedSearchItem(
                            onClick = {},
                            suggestedSearch = SuggestedSearchUiModel(
                                query = "Sample",
                                keywordType = KeywordType.IMAGE,
                                searchType = SearchType.FULL
                            )
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
            // suggestion-generated view of images similar to collage
            // these are searched with the current chatgpt like search
            // instead of displaying search suggestions - just search and display
            items(4) {
                Helper()
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .layoutId(ViewIds2.FAB),
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search"
            )
        }
    }
}

@Composable
private fun Helper(modifier: Modifier = Modifier) {
    ExtractorUserCollageItem(
        modifier = Modifier.padding(vertical = 6.dp),
        onItemClick = {},
        onShareClick = {},
        keyword = "sample",
        extractions = listOf(
            Extraction(
                uri = MediaImageUri(""),
                mediaImageId = MediaImageId(1L),
                path = "",
                dateAdded = LocalDateTime.now()
            ),
            Extraction(
                uri = MediaImageUri(""),
                mediaImageId = MediaImageId(2L),
                path = "",
                dateAdded = LocalDateTime.now()
            ),
            Extraction(
                uri = MediaImageUri(""),
                mediaImageId = MediaImageId(3L),
                path = "",
                dateAdded = LocalDateTime.now()
            ),
            Extraction(
                uri = MediaImageUri(""),
                mediaImageId = MediaImageId(4L),
                path = "",
                dateAdded = LocalDateTime.now()
            ),
            Extraction(
                uri = MediaImageUri(""),
                mediaImageId = MediaImageId(4L),
                path = "",
                dateAdded = LocalDateTime.now()
            ),
            Extraction(
                uri = MediaImageUri(""),
                mediaImageId = MediaImageId(4L),
                path = "",
                dateAdded = LocalDateTime.now()
            ),
        )
    )
}

private fun searchScreenConstraintSet() = ConstraintSet {
    val topBar = createRefFor(ViewIds2.TOP_BAR)
    val mainContent = createRefFor(ViewIds2.MAIN_CONTENT)
    val fab = createRefFor(ViewIds2.FAB)

    constrain(topBar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top, margin = 24.dp)
        width = Dimension.fillToConstraints
    }

    constrain(mainContent) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(topBar.bottom, margin = (-28).dp)
        bottom.linkTo(parent.bottom)
        height = Dimension.fillToConstraints
    }

    constrain(fab) {
        end.linkTo(parent.end, margin = 32.dp)
        bottom.linkTo(parent.bottom, margin = 48.dp)
    }
}

private object ViewIds2 {
    const val FAB = "fab_view"
    const val MAIN_CONTENT = "main_content_view"
    const val TOP_BAR = "top_bar_view"
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtractorSearchScreen2()
        }
    }
}
