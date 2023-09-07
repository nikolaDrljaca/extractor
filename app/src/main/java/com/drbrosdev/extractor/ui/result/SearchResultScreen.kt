package com.drbrosdev.extractor.ui.result

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.ui.components.BackButton
import com.drbrosdev.extractor.ui.components.ExtractorImageGrid
import com.drbrosdev.extractor.ui.components.SearchFilterSheet
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    modifier: Modifier = Modifier,
    onNavToDetail: (selectedIndex: Int) -> Unit,
    onNavBack: () -> Unit,
    state: SearchResultScreenState,
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = true,
            density = LocalDensity.current,
            initialValue = SheetValue.Hidden
        )
    )

    LaunchedEffect(key1 = Unit) {
        delay(500)
        scaffoldState.bottomSheetState.show()
    }

    BottomSheetScaffold(
        sheetContent = {
            SearchFilterSheet(
                onClearFilterClick = {}
            )
        },
        sheetContainerColor = MaterialTheme.colorScheme.primary,
        sheetDragHandle = { Spacer(modifier.height(24.dp)) },
        sheetContentColor = Color.White,
        sheetPeekHeight = 100.dp
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
        ) {
            val (backButton, images) = createRefs()

            ExtractorImageGrid(
                modifier = Modifier.constrainAs(
                    ref = images,
                    constrainBlock = {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                ),
                images = state.images,
                searchTerm = state.searchTerm,
                onClick = onNavToDetail
            )

            BackButton(
                modifier = Modifier.constrainAs(
                    ref = backButton,
                    constrainBlock = {
                        top.linkTo(parent.top, margin = 24.dp)
                        start.linkTo(parent.start)
                    }
                ),
                onClick = onNavBack
            )
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SearchScreenPreview() {
    ExtractorTheme(dynamicColor = false) {
        SearchResultScreen(
            state = SearchResultScreenState(),
            onNavToDetail = {},
            onNavBack = {}
        )
    }
}
