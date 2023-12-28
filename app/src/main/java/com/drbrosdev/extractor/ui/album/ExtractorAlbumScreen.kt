package com.drbrosdev.extractor.ui.album

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.Album
import com.drbrosdev.extractor.domain.model.AlbumEntry
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorImageItem
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.toUri

@Composable
fun ExtractorAlbumScreen(
    modifier: Modifier = Modifier,
    onImageClick: (index: Int) -> Unit,
    onDropdownAction: (AlbumHeaderDropdownAction) -> Unit,
    onBack: () -> Unit,
    state: ExtractorAlbumScreenState,
) {
    val imageSize = 96
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    when (state) {
        ExtractorAlbumScreenState.Loading -> ExtractorAlbumScreenLoading()
        is ExtractorAlbumScreenState.Content -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = imageSize.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier),
                contentPadding = systemBarsPadding
            ) {
                item(
                    key = "top_spacer",
                    span = { GridItemSpan(maxCurrentLineSpan) }
                ) {
                    Spacer(modifier = Modifier.height(28.dp))
                }

                item(
                    key = "album_info",
                    span = { GridItemSpan(maxCurrentLineSpan) }
                ) {
                    AlbumHeader(
                        keyword = state.album.keyword,
                        metadata = state.metadata,
                        onDropdownAction = onDropdownAction,
                        onBack = onBack,
                    )
                }

                item(
                    key = "spacer",
                    span = { GridItemSpan(maxCurrentLineSpan) }
                ) {
                    Spacer(modifier = Modifier.height(18.dp))
                }

                itemsIndexed(
                    state.album.entries,
                    key = { _, it -> it.id.id }
                ) { index, entry ->
                    ExtractorImageItem(
                        imageUri = entry.uri.toUri(),
                        onClick = { onImageClick(index) },
                        size = imageSize,
                        modifier = Modifier.padding(1.dp)
                    )
                }
            }
        }
    }

}

@Composable
private fun ExtractorAlbumScreenLoading(
    modifier: Modifier = Modifier,
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            trackColor = Color.Transparent,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun AlbumHeader(
    modifier: Modifier = Modifier,
    onDropdownAction: (AlbumHeaderDropdownAction) -> Unit,
    onBack: () -> Unit,
    keyword: String,
    metadata: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BackIconButton(onBack = onBack)
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "# $keyword",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = metadata,
                style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
            )
        }

        AlbumHeaderDropdownMenu(onAction = onDropdownAction)
    }
}

sealed interface AlbumHeaderDropdownAction {

    data object Delete : AlbumHeaderDropdownAction

    data object Share : AlbumHeaderDropdownAction
}

@Composable
private fun AlbumHeaderDropdownMenu(
    modifier: Modifier = Modifier,
    onAction: (AlbumHeaderDropdownAction) -> Unit
) {
    var isDropdownOpen by rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
            .then(modifier)
    ) {

        IconButton(onClick = { isDropdownOpen = true }) {
            Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "")
        }

        DropdownMenu(
            expanded = isDropdownOpen,
            onDismissRequest = { isDropdownOpen = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.bottom_bar_share)) },
                onClick = { onAction(AlbumHeaderDropdownAction.Share) },
                leadingIcon = { Icon(imageVector = Icons.Rounded.Share, contentDescription = "") }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.dropdown_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                onClick = { onAction(AlbumHeaderDropdownAction.Delete) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    }
}

@Composable
@ScreenPreview
private fun CurrentPreview() {
    val data = ExtractorAlbumScreenState.Content(
        album = Album(
            id = 0L,
            name = "Some album",
            keyword = "keyword",
            searchType = SearchType.PARTIAL,
            labelType = LabelType.IMAGE,
            entries = listOf(
                AlbumEntry(uri = MediaImageUri(""), id = MediaImageId(11L)),
                AlbumEntry(uri = MediaImageUri(""), id = MediaImageId(12L)),
                AlbumEntry(uri = MediaImageUri(""), id = MediaImageId(13L)),
            )
        )
    )
    ExtractorTheme(dynamicColor = false) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ExtractorAlbumScreen(
                onImageClick = {},
                state = data,
                onDropdownAction = {},
                onBack = {}
            )
        }
    }
}