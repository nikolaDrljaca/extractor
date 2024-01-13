package com.drbrosdev.extractor.ui.components.searchsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilter
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilterState
import com.drbrosdev.extractor.ui.components.extractorloaderbutton.ExtractorLoaderButton
import com.drbrosdev.extractor.ui.components.extractorloaderbutton.ExtractorLoaderButtonState
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchView
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun ExtractorSearchSheet(
    modifier: Modifier = Modifier,
    onDone: () -> Unit,
    onCreateAlbumClick: () -> Unit,
    searchViewState: ExtractorSearchViewState,
    dateFilterState: ExtractorDateFilterState,
    loaderButtonState: ExtractorLoaderButtonState,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExtractorSearchView(
            onDone = onDone,
            state = searchViewState,
            contentPadding = PaddingValues(),
            textFieldPadding = PaddingValues(bottom = 16.dp)
        )

        ExtractorDateFilter(state = dateFilterState)

        Spacer(modifier = Modifier.height(6.dp))

        ExtractorLoaderButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onCreateAlbumClick,
            state = loaderButtonState,
            loadingContent = {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    trackColor = Color.Transparent,
                    color = Color.Black,
                    strokeCap = StrokeCap.Round
                )
            },
            successContent = {
                Icon(imageVector = Icons.Rounded.Check, contentDescription = "")
                Text(text = stringResource(R.string.album_created))
            }
        ) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = "")
            Text(text = stringResource(R.string.create_album))
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}


@Preview
@Composable
private fun SheetPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            ExtractorSearchSheet(
                onDone = {},
                onCreateAlbumClick = {},
                searchViewState = ExtractorSearchViewState("", KeywordType.ALL),
                dateFilterState = ExtractorDateFilterState(),
                loaderButtonState = ExtractorLoaderButtonState()
            )
        }
    }
}
