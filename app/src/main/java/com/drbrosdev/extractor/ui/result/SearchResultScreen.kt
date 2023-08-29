package com.drbrosdev.extractor.ui.result

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.ui.components.BackButton
import com.drbrosdev.extractor.ui.components.ExtractorImageGrid
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    modifier: Modifier = Modifier,
) {
    BottomSheetScaffold(
        sheetContent = {}
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
                images = emptyList(),
                onClick = {}
            )

            BackButton(
                modifier = Modifier.constrainAs(
                    ref = backButton,
                    constrainBlock = {
                        top.linkTo(parent.top, margin = 24.dp)
                        start.linkTo(parent.start)
                    }
                ),
                onClick = {}
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
    ExtractorTheme {
        SearchResultScreen()
    }
}
