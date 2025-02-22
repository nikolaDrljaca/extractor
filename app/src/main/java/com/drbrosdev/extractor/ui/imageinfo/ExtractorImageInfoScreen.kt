package com.drbrosdev.extractor.ui.imageinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.EmbeddingTextField
import com.drbrosdev.extractor.ui.components.shared.ExtractorButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorButtonDefaults
import com.drbrosdev.extractor.ui.components.shared.ExtractorEmbeddingChips
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextFieldState

@OptIn(ExperimentalLayoutApi::class)
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

