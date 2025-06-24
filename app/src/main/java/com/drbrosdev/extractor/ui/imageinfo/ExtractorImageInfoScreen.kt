package com.drbrosdev.extractor.ui.imageinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.drbrosdev.extractor.R

@Composable
fun AppImageDetailScreen(
    modifier: Modifier = Modifier,
    model: LupaImageDetailState
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
            .systemBarsPadding()
    ) {
        AppImageDetailHeading(
            modifier = Modifier,
            model = model.heading
        )

        Box(Modifier) {
            when {
                model.hasDescription -> AppImageDetailDescription(
                    text = model.description!!,
                    modifier = Modifier.fillMaxWidth()
                )

                else -> Spacer(Modifier.height(16.dp))
            }
        }

        AppImageDetailEditable(
            modifier = Modifier
                .padding(top = 8.dp),
            model = model.editables
        )

        /* TODO Move this to new edit info screen
    Box(Modifier.weight(1f)) {
        Text(
            text = stringResource(R.string.extractor_info_about_image),
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.Gray
            ),
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
         */
    }
}

@Composable
private fun AppImageDetailHeading(
    modifier: Modifier = Modifier,
    model: LupaImageHeading
) {
    Row(
        modifier = Modifier
            .then(modifier),
        verticalAlignment = Alignment.Top,
    ) {
        AsyncImage(
            contentDescription = "",
            modifier = Modifier
                .weight(1f)
                .height(144.dp)
                .clip(RoundedCornerShape(28.dp)),
            model = ImageRequest.Builder(LocalContext.current)
                .data(model.uri.toUri())
                .size(192 * 2)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.baseline_image_24),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(2f)
        ) {
            Text(
                text = "Image Info",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "# ID: ${model.mediaImageId}"
            )
            Text(
                text = model.dateAdded,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun AppImageDetailDescription(
    modifier: Modifier = Modifier,
    text: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text(
                text = "Gemini says",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = text,
            )
        }
    }
}

@Composable
private fun ImageAnnotationsFlowRow(
    modifier: Modifier = Modifier,
    annotations: Annotations
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(top = 4.dp)
    ) {
        annotations.embeds.forEach { embed ->
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Text(
                    text = embed,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AppImageDetailEditable(
    modifier: Modifier = Modifier,
    model: LupaImageEditables
) {
    val islandSpacer = 8.dp
    Column(
        modifier = Modifier
            .then(modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.Top,
        ) {
            EditableContentIsland(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                onClick = { model.eventSink(LupaImageEditablesEvents.OnVisualEdit) },
                title = { Text(stringResource(R.string.visual_embeddings)) }
            ) {
                ImageAnnotationsFlowRow(annotations = model.visualEmbeds)
            }
            Spacer(Modifier.width(islandSpacer))
            EditableContentIsland(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                onClick = { model.eventSink(LupaImageEditablesEvents.OnUserEdit) },
                title = { Text(stringResource(R.string.user_embeddings)) }
            ) {
                ImageAnnotationsFlowRow(annotations = model.userEmbeds)
            }
        }
        Spacer(Modifier.height(islandSpacer))
        EditableContentIsland(
            onClick = { model.eventSink(LupaImageEditablesEvents.OnTextEdit) },
            title = { Text(stringResource(R.string.text_embeddings)) }
        ) {
            Text(
                text = model.textEmbed
            )
        }

    }
}

@Composable
private fun EditableContentIsland(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    title: @Composable RowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.titleMedium
                ) {
                    title()
                }
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "edit content",
                    modifier = Modifier
                        .size(20.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            content()
        }
    }
}

