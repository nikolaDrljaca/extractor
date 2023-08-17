package com.drbrosdev.extractor.ui.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.ui.components.SearchBar
import com.drbrosdev.extractor.ui.components.SearchTopBar
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun SearchScreen(
    syncStatus: SyncStatus
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .systemBarsPadding(),
    ) {
        val topGuideline = createGuidelineFromTop(0.15f)
        val (searchBar, previousSearch, status, aboutIcon) = createRefs()

        SearchBar(
            modifier = Modifier
                .constrainAs(
                    ref = searchBar,
                    constrainBlock = {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(topGuideline)
                        width = Dimension.fillToConstraints
                    }
                )
        )

        SearchTopBar(
            modifier = Modifier
                .constrainAs(
                    ref = status,
                    constrainBlock = {
                        top.linkTo(parent.top, margin = 12.dp)
                        start.linkTo(parent.start)
                    }
                )
                .fillMaxWidth(),
            onClick = {},
            onAboutClick = {},
            localCount = syncStatus.localCount,
            deviceCount = syncStatus.deviceCount
        )


        LazyColumn(
            modifier = Modifier
                .constrainAs(
                    ref = previousSearch,
                    constrainBlock = {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(searchBar.bottom)
                    }
                )
        ) {
//            items(temp) {
//                PreviousSearchItem(
//                    modifier = Modifier.padding(start = 4.dp),
//                    text = it,
//                    onClick = { /*TODO*/ },
//                    onDelete = {}
//                )
//            }
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun SearchScreenPreview() {
    ExtractorTheme {
        Surface {
            SearchScreen(SyncStatus())
        }
    }
}
