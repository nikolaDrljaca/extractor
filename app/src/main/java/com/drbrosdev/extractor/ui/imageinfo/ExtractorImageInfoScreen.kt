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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.AppImageInfoHeader

@Composable
fun AppImageInfoScreen(
    modifier: Modifier = Modifier,
    model: LupaImageInfoState
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
        AppImageInfoHeader(
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
    lupaImageAnnotationsState: LupaImageAnnotationsState
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(top = 4.dp)
    ) {
        lupaImageAnnotationsState.embeds.forEach { embed ->
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
    model: LupaImageEditablesState
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
                ImageAnnotationsFlowRow(lupaImageAnnotationsState = model.visualEmbeds)
            }
            Spacer(Modifier.width(islandSpacer))
            EditableContentIsland(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                onClick = { model.eventSink(LupaImageEditablesEvents.OnUserEdit) },
                title = { Text(stringResource(R.string.user_embeddings)) }
            ) {
                ImageAnnotationsFlowRow(lupaImageAnnotationsState = model.userEmbeds)
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

