package com.drbrosdev.extractor.ui.allalbum

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorHeader
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorImageItem
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBarState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExtractorAlbumsScreen(
    onBack: () -> Unit,
    onAlbumClick: (Long) -> Unit,
    onAction: (ExtractorSwipeAction<AlbumItemUiModel>) -> Unit,
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
                        SwipeableAlbumCard(
                            onClick = { onAlbumClick(it.id) },
                            onSwipeAction = onAction,
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
                ExtractorHeader(headerText = stringResource(id = R.string.my_albums))
            },
            trailingSlot = {
                Spacer(modifier = Modifier.width(12.dp))
            },
            centerSlot = {}
        )

        SnackbarHost(
            hostState = state.snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 8.dp)
        )
    }
}

sealed class ExtractorSwipeAction<T> {

    data class Share<T>(
        val value: T
    ) : ExtractorSwipeAction<T>()

    data class Delete<T>(
        val value: T
    ) : ExtractorSwipeAction<T>()

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableAlbumCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onSwipeAction: (ExtractorSwipeAction<AlbumItemUiModel>) -> Unit,
    item: AlbumItemUiModel
) {
    val swipeState = rememberDismissState()

    LaunchedEffect(key1 = Unit) {
        snapshotFlow { swipeState.currentValue }
            .collectLatest {
                when (it) {
                    DismissValue.Default -> Unit
                    DismissValue.DismissedToEnd -> onSwipeAction(ExtractorSwipeAction.Share(item))
                    DismissValue.DismissedToStart -> onSwipeAction(ExtractorSwipeAction.Delete(item))
                }
                swipeState.reset()
            }
    }

    SwipeToDismissBox(
        state = swipeState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        backgroundContent = {
            val color = when (swipeState.dismissDirection) {
                null -> Color.Transparent
                DismissDirection.StartToEnd -> MaterialTheme.colorScheme.primary
                DismissDirection.EndToStart -> MaterialTheme.colorScheme.error
            }
            val contentColor = when (swipeState.dismissDirection) {
                null -> Color.Transparent
                DismissDirection.StartToEnd -> Color.White
                DismissDirection.EndToStart -> MaterialTheme.colorScheme.onError
            }
            val icon = when (swipeState.dismissDirection) {
                null -> Icons.Rounded.Share
                DismissDirection.StartToEnd -> Icons.Rounded.Share
                DismissDirection.EndToStart -> Icons.Rounded.Delete
            }
            val alignment = when (swipeState.dismissDirection) {
                null -> Alignment.Center
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val padding = when (swipeState.dismissDirection) {
                null -> PaddingValues()
                DismissDirection.StartToEnd -> PaddingValues(start = 36.dp)
                DismissDirection.EndToStart -> PaddingValues(end = 36.dp)
            }

            Surface(
                color = color,
                contentColor = contentColor,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = alignment) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(padding)
                    )
                }
            }
        }
    ) {
        AlbumCard(
            onClick = onClick,
            item = item,
            modifier = modifier
        )
    }
}

@Composable
private fun AlbumCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    item: AlbumItemUiModel
) {
    Surface(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 2.dp,
        onClick = onClick
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
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AlbumPreview(
                    thumbnails = item.thumbnails
                )
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
                size = 64,
                clipSize = 6.dp
            )
        }
    }
}