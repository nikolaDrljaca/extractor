package com.drbrosdev.extractor.ui.imageinfo

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.framework.fillMaxWidth
import com.drbrosdev.extractor.ui.components.shared.EmbeddingTextField
import com.drbrosdev.extractor.ui.components.shared.ExtractorButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorButtonDefaults
import com.drbrosdev.extractor.ui.components.shared.ExtractorEmbeddingChips
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextFieldState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun ExtractorImageInfoScreen(
    onClearVisual: (String) -> Unit,
    onClearUser: (String) -> Unit,
    onSaveEmbeddings: () -> Unit,
    onAddNewUser: () -> Unit,
    modifier: Modifier = Modifier,
    model: ExtractorImageInfoUiState,
    textEmbedState: ExtractorTextFieldState
) {
    val scrollState = rememberScrollState()
    val smallLabel = MaterialTheme.typography.labelSmall.copy(
        color = Color.Gray
    )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .systemBarsPadding()
            .then(modifier),
        constraintSet = imageInfoConstraintSet()
    ) {
        Column(
            modifier = Modifier.layoutId(ViewIds.IMAGE_INFO)
        ) {
            Text(
                text = stringResource(R.string.extractor_image_info),
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = stringResource(R.string.info_screen_image_id, model.mediaImageId.id))
        }

        Column(
            modifier = Modifier
                .layoutId(ViewIds.EMBEDDINGS)
                .then(modifier),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.visual_embeddings),
                style = MaterialTheme.typography.titleMedium
            )
            ExtractorEmbeddingChips(onClear = onClearVisual, embeddings = model.visualEmbedding)

            Text(
                text = stringResource(R.string.user_embeddings),
                style = MaterialTheme.typography.titleMedium
            )
            ExtractorEmbeddingChips(
                onClear = onClearUser,
                embeddings = model.userEmbedding,
                trailingSlot = {
                    AssistChip(
                        onClick = onAddNewUser,
                        label = { Text(text = "Add") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Add"
                            )
                        },
                        shape = CircleShape
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            EmbeddingTextField(
                value = textEmbedState.textValue,
                onTextChange = textEmbedState::updateTextValue,
                label = { Text(text = stringResource(R.string.text_embeddings)) }
            )
        }

        ExtractorButton(
            onClick = onSaveEmbeddings,
            modifier = Modifier.layoutId(ViewIds.SAVE_BUTTON),
            contentPadding = ExtractorButtonDefaults.paddingValues(vertical = 4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_save_24),
                contentDescription = "null"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = stringResource(R.string.info_screen_save))
        }

        Column(
            modifier = Modifier.layoutId(ViewIds.NOTE)
        ) {
            Text(
                text = stringResource(R.string.extractor_info_about_image),
                style = smallLabel
            )
        }
    }
}

@Composable
fun AppImageDetailScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    ConstraintLayout(
        constraintSet = lupaImageDetailsConstraintSet(),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .systemBarsPadding()
    ) {

    }
}

@Composable
fun AppImageDetailHeading(
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Top,
    ) {
        AsyncImage(
            contentDescription = "",
            modifier = Modifier
                .weight(1f)
                .height(144.dp)
                .clip(RoundedCornerShape(28.dp)),
            model = ImageRequest.Builder(LocalContext.current)
//                .data(item.toUri())
                .data(Uri.EMPTY)
                .size(192 * 2)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.baseline_image_24),
            contentScale = ContentScale.Crop
        )

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
                text = "# ID: 234234"
            )
            Text(
                text = "2025-01-01",
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AppImageDetailDescription(
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
fun AppImageDetailEditable(
    modifier: Modifier = Modifier
) {
    val islandSpacer = 8.dp
    Column(
        modifier = Modifier
            .then(modifier)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EditableContentIsland(
                modifier = Modifier
                    .weight(1f),
                onClick = {},
                title = { Text(stringResource(R.string.visual_embeddings)) }
            ) {

            }
            Spacer(Modifier.width(islandSpacer))
            EditableContentIsland(
                modifier = Modifier
                    .weight(1f),
                onClick = {},
                title = { Text(stringResource(R.string.user_embeddings)) }
            ) {

            }
        }
        Spacer(Modifier.height(islandSpacer))
        EditableContentIsland(
            onClick = {},
            title = { Text(stringResource(R.string.text_embeddings)) }
        ) {
            Text(stringResource(R.string.lorem))
        }
    }
}

@Composable
fun EditableContentIsland(
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

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(darkTheme = true) {
        Surface {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                AppImageDetailHeading()
                AppImageDetailDescription(
                    text = "this is some random text",
                    modifier = Modifier.fillMaxWidth()
                )

                AppImageDetailEditable()
            }
        }
    }
}

private fun lupaImageDetailsConstraintSet() = ConstraintSet {
    val heading = createRefFor("heading")
    val editableSection = createRefFor("editable_section")
    val disclaimer = createRefFor("disclaimer")

    constrain(heading) {
        fillMaxWidth()
        top.linkTo(parent.top)
    }
    constrain(editableSection) {
        fillMaxWidth()
        top.linkTo(heading.bottom)
    }
    constrain(disclaimer) {
        fillMaxWidth()
        bottom.linkTo(parent.bottom)
    }
}


private fun imageInfoConstraintSet() = ConstraintSet {
    val saveButton = createRefFor(ViewIds.SAVE_BUTTON)
    val imageInfo = createRefFor(ViewIds.IMAGE_INFO)
    val embeddings = createRefFor(ViewIds.EMBEDDINGS)
    val note = createRefFor(ViewIds.NOTE)

    val guideline = createGuidelineFromStart(0.65f)
    val topGuideline = createGuidelineFromTop(0.03f)
    val barGuideline = createGuidelineFromTop(0.09f)

    constrain(note) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        bottom.linkTo(parent.bottom)
        width = Dimension.fillToConstraints
    }

    constrain(saveButton) {
        top.linkTo(topGuideline)
        end.linkTo(parent.end)
        bottom.linkTo(barGuideline)
        height = Dimension.fillToConstraints
    }

    constrain(imageInfo) {
        top.linkTo(topGuideline)
        start.linkTo(parent.start)
        end.linkTo(guideline)
        bottom.linkTo(barGuideline)
        width = Dimension.fillToConstraints
        height = Dimension.fillToConstraints
    }

    constrain(embeddings) {
        top.linkTo(barGuideline, margin = 12.dp)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val SAVE_BUTTON = "save_button"
    const val IMAGE_INFO = "image_info"
    const val EMBEDDINGS = "embeddings"
    const val NOTE = "note"
}

