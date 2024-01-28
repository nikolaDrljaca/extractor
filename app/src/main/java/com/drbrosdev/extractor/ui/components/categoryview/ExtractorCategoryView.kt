package com.drbrosdev.extractor.ui.components.categoryview

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.AlbumPreview
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.ui.components.shared.AnimatedBorderContent
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextButton
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview
import com.drbrosdev.extractor.util.toUri

private const val IMAGE_SIZE = 130

@Composable
fun ExtractorCategoryView(
    modifier: Modifier = Modifier,
    onViewAllClicked: () -> Unit,
    onAlbumPreviewClick: (Long) -> Unit,
    onInitClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    category: ExtractorAlbumsViewDefaults.Category,
    state: ExtractorCategoryViewState,
) {
    Column(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .then(modifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(bottom = 6.dp)
                .padding(contentPadding)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = category.stringRes),
                style = MaterialTheme.typography.titleLarge
            )

            when {
                state is ExtractorCategoryViewState.Content && category == ExtractorAlbumsViewDefaults.Category.USER -> {
                    ExtractorTextButton(onClick = onViewAllClicked) {
                        Text(text = stringResource(R.string.album_view_all))
                    }
                }

                else -> {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }

        AnimatedContent(
            targetState = state,
            label = "",
            contentAlignment = Alignment.Center,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 220,
                        delayMillis = 90
                    )
                ) togetherWith fadeOut(animationSpec = tween(durationMillis = 90))
            }
        ) {
            when (it) {
                is ExtractorCategoryViewState.Content -> ExtractorCategoryContentView(
                    onAlbumPreviewClick = onAlbumPreviewClick,
                    items = it.albums,
                )

                ExtractorCategoryViewState.Initial -> ExtractorCategoryInitialView(
                    modifier = Modifier.padding(contentPadding),
                    onInitClick = onInitClick,
                    category = category
                )

                ExtractorCategoryViewState.Loading -> ExtractorCategoryLoadingView(
                    modifier = Modifier.padding(contentPadding),
                )

                ExtractorCategoryViewState.Empty -> ExtractorCategoryEmptyView(
                    modifier = Modifier.padding(contentPadding),
                )
            }
        }

    }
}


object ExtractorAlbumsViewDefaults {

    enum class Category(val stringRes: Int) {
        VISUAL(R.string.visual_albums),
        TEXT(R.string.text_albums),
        USER(R.string.my_albums)
    }
}

@Composable
private fun ExtractorCategoryInitialView(
    modifier: Modifier = Modifier,
    onInitClick: () -> Unit,
    category: ExtractorAlbumsViewDefaults.Category,
) {
    Column(
        modifier = Modifier
            .requiredHeight(IMAGE_SIZE.dp)
            .fillMaxWidth()
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (category) {
            ExtractorAlbumsViewDefaults.Category.USER -> {
                TextButton(onClick = onInitClick) {
                    Icon(
                        painterResource(id = R.drawable.round_image_search_24),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.go_to_search))
                }
                Text(
                    text = stringResource(R.string.create_own_album),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(IntrinsicSize.Max),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            else -> {
                TextButton(onClick = onInitClick) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = "")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.initialize))
                }

                Text(
                    text = stringResource(R.string.init_album_category_expl),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun ExtractorCategoryLoadingView(
    modifier: Modifier = Modifier,
) {
    AnimatedBorderContent(
        shape = RoundedCornerShape(14.dp),
        gradient = Brush.sweepGradient(
            listOf(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.primary
            )
        ),
        animationDuration = 5000
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .then(modifier),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.loading),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ExtractorCategoryEmptyView(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .requiredHeight(IMAGE_SIZE.dp)
            .fillMaxWidth()
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "\uD83D\uDE41",
                textAlign = TextAlign.Center,
                modifier = Modifier.width(IntrinsicSize.Max),
                fontSize = 24.sp,
            )
            Text(
                text = stringResource(R.string.oops_no_albums_found),
                color = Color.Gray,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.width(IntrinsicSize.Max),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExtractorCategoryContentView(
    modifier: Modifier = Modifier,
    onAlbumPreviewClick: (Long) -> Unit,
    items: List<AlbumPreview>
) {

    LazyRow(
        modifier = Modifier
            .requiredHeight(IMAGE_SIZE.dp)
            .then(modifier),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
    ) {
        items(
            items = items,
            key = { it.id }
        ) {
            AlbumThumbnailView(
                modifier = Modifier.animateItemPlacement(),
                onClick = { onAlbumPreviewClick(it.id) },
                imageUri = it.thumbnail.toUri(),
                albumName = it.name
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AlbumThumbnailView(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageUri: Uri,
    albumName: String
) {
    val size = IMAGE_SIZE
    val sizeModifier = Modifier.size(size.dp)
    val scaleSize = size * 2
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier.clip(shape)
    ) {
        AsyncImage(
            modifier = Modifier
                .then(sizeModifier)
                .clip(shape)
                .clickable { onClick() }
                .then(modifier),
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .size(scaleSize, scaleSize)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.baseline_image_24)
        )

        Box(
            modifier = Modifier
                .background(color = Color.Black.copy(alpha = 0.25f))
                .matchParentSize()
        )

        Text(
            text = albumName,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .basicMarquee()
                .padding(bottom = 4.dp, start = 2.dp, end = 2.dp),
            style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    val items = listOf(
        AlbumPreview(1L, "some", MediaImageUri("")),
        AlbumPreview(2L, "some", MediaImageUri("")),
        AlbumPreview(3L, "some", MediaImageUri("")),
    )

    ExtractorTheme(dynamicColor = false) {
        Surface {
            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(space = 20.dp)
            ) {
                ExtractorCategoryView(
                    onViewAllClicked = { },
                    state = ExtractorCategoryViewState.Content(albums = items),
                    onAlbumPreviewClick = {},
                    category = ExtractorAlbumsViewDefaults.Category.TEXT,
                    onInitClick = {}
                )

                ExtractorCategoryView(
                    onViewAllClicked = { },
                    onAlbumPreviewClick = {},
                    state = ExtractorCategoryViewState.Initial,
                    category = ExtractorAlbumsViewDefaults.Category.USER,
                    onInitClick = {}
                )

                ExtractorCategoryView(
                    onViewAllClicked = { },
                    onAlbumPreviewClick = {},
                    state = ExtractorCategoryViewState.Loading,
                    category = ExtractorAlbumsViewDefaults.Category.VISUAL,
                    onInitClick = {}
                )

                ExtractorCategoryView(
                    onViewAllClicked = { },
                    onAlbumPreviewClick = {},
                    state = ExtractorCategoryViewState.Empty,
                    category = ExtractorAlbumsViewDefaults.Category.VISUAL,
                    onInitClick = {}
                )
            }
        }
    }
}
