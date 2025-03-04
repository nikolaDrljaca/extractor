package com.drbrosdev.extractor.ui.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheet
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetComponent

@Composable
fun ExtractorSearchScreen(
    searchSheetComponent: ExtractorSearchSheetComponent
) {
    ConstraintLayout(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
        constraintSet = searchResultScreenConstraintSet()
    ) {
        LazyColumn(
            modifier = Modifier.layoutId(ViewIds.MAIN_CONTENT)
        ) {
            // search sheet
            item {
                ExtractorSearchSheet(
                    modifier = Modifier
                        .padding(horizontal = 4.dp),
                    component = searchSheetComponent
                )
            }
            // search results -- content
        }
    }
}

private fun searchResultScreenConstraintSet() = ConstraintSet {
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val mainContent = createRefFor(ViewIds.MAIN_CONTENT)
    val snackbar = createRefFor(ViewIds.SNACKBAR)

    val bottomSheetGuideline = createGuidelineFromBottom(offset = 88.dp)

    constrain(mainContent) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top)
        width = Dimension.fillToConstraints
    }

    constrain(topBar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top)
        width = Dimension.fillToConstraints
    }

    constrain(snackbar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        bottom.linkTo(bottomSheetGuideline, margin = 16.dp)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val MAIN_CONTENT = "content"
    const val TOP_BAR = "topBar"
    const val SNACKBAR = "snack_bar"
}