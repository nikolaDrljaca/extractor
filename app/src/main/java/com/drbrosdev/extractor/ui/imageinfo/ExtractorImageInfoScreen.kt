package com.drbrosdev.extractor.ui.imageinfo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.embeddingsform.EmbeddingsForm
import com.drbrosdev.extractor.ui.components.shared.ExtractorButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorImageInfoScreen(
    onClearVisual: (String) -> Unit,
    onSaveEmbeddings: () -> Unit,
    modifier: Modifier = Modifier,
    model: ImageInfoUiModel,
) {
    val scrollState = rememberScrollState()

    Surface(
        tonalElevation = BottomSheetDefaults.Elevation,
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(18.dp)
    ) {
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
                    text = "Extractor Image Info",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = "# ID: ${model.mediaImageId}")
            }

            EmbeddingsForm(
                modifier = Modifier.layoutId(ViewIds.EMBEDDINGS),
                formState = model.embeddingsFormState,
                visualEmbeddings = model.visualEmbedding,
                onClearVisual = onClearVisual
            )

            ExtractorButton(
                onClick = onSaveEmbeddings,
                modifier = Modifier.layoutId(ViewIds.SAVE_BUTTON),
                verticalContentPadding = 4.dp
            ) {
                Text(text = "Save")
            }

            Column(
                modifier = Modifier.layoutId(ViewIds.NOTE)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Note")
                Text(
                    text = stringResource(R.string.extractor_info_about_image),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}


private fun imageInfoConstraintSet() = ConstraintSet {
    val saveButton = createRefFor(ViewIds.SAVE_BUTTON)
    val imageInfo = createRefFor(ViewIds.IMAGE_INFO)
    val embeddings = createRefFor(ViewIds.EMBEDDINGS)
    val note = createRefFor(ViewIds.NOTE)

    val guideline = createGuidelineFromStart(0.7f)
    val topGuideline = createGuidelineFromTop(0.03f)

    constrain(note) {
        top.linkTo(embeddings.bottom)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
    }

    constrain(saveButton) {
        top.linkTo(topGuideline)
        end.linkTo(parent.end)
        start.linkTo(guideline)
        width = Dimension.fillToConstraints
    }

    constrain(imageInfo) {
        top.linkTo(topGuideline)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
    }

    constrain(embeddings) {
        top.linkTo(imageInfo.bottom, margin = 12.dp)
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

