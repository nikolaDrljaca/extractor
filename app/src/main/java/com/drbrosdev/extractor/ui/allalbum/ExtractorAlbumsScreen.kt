package com.drbrosdev.extractor.ui.allalbum

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ConfirmationDialog
import com.drbrosdev.extractor.ui.components.shared.ConfirmationDialogActions
import com.drbrosdev.extractor.ui.components.shared.ExtractorHeader
import com.drbrosdev.extractor.ui.components.shared.ExtractorImageItem
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBarState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExtractorAlbumsScreen(
    onBack: () -> Unit,
    onAlbumClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDeleteDialogAction: (ConfirmationDialogActions) -> Unit,
    modifier: Modifier = Modifier,
    state: ExtractorAlbumsScreenState
) {
    val lazyListState = rememberLazyListState()
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
    val extractorTopBarState = remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex > 0) ExtractorTopBarState.ELEVATED
            else ExtractorTopBarState.NORMAL
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        when (state) {
            is ExtractorAlbumsScreenState.Content -> {
                if (state.isConfirmDeleteShown) {
                    ConfirmationDialog(
                        icon = { Icon(imageVector = Icons.Rounded.Delete, contentDescription = "") },
                        onAction = onDeleteDialogAction
                    ) {
                        Text(text = stringResource(R.string.album_delete_perm))
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(
                        8.dp,
                        alignment = Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = lazyListState,
                    contentPadding = systemBarsPadding,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    item(key = "top_spacer") {
                        Spacer(modifier = Modifier.height(64.dp))
                    }

                    items(state.albums, key = { it.id }) {
                        AlbumCard(
                            onClick = { onAlbumClick(it.id) },
                            onDeleteClick = { onDeleteClick(it.id) },
                            item = it,
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }

            is ExtractorAlbumsScreenState.Loading -> {
                Box(
                    modifier = Modifier
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
        }

        ExtractorTopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            state = extractorTopBarState.value,
            leadingSlot = {
                BackIconButton(onBack = onBack)
                ExtractorHeader(headerText = "My Albums")
            },
            trailingSlot = {
                Spacer(modifier = Modifier.width(12.dp))
            },
            centerSlot = {}
        )
    }
}

@Composable
private fun AlbumCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    item: AlbumItemUiModel
) {
    Surface(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "# ${item.keyword}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = item.metadata,
                        style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
                    )
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AlbumPreview(
                    thumbnails = item.thumbnails
                )

                IconButton(onClick = onClick) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumPreview(
    modifier: Modifier = Modifier,
    thumbnails: List<Uri>,
) {
    Row(
        modifier = Modifier.then(modifier),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        thumbnails.forEach {
            ExtractorImageItem(
                imageUri = it,
                size = 56
            )
        }
    }
}