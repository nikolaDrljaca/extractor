package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.imageinfo.UserEmbedUiModel
import com.drbrosdev.extractor.ui.imageinfo.VisualEmbedUiModel
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@Composable
fun ExtractorEmbeddingChips(
    modifier: Modifier = Modifier,
    onClear: (String) -> Unit,
    embeddings: List<VisualEmbedUiModel>,
) {
    FlowRow(
        modifier = Modifier
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        embeddings.forEach {
            EmbeddingChip(
                onClick = onClear,
                text = it.text,
                isChecked = it.isChecked
            )
        }
    }
}

@Composable
fun ExtractorEmbeddingChips(
    modifier: Modifier = Modifier,
    onClear: (String) -> Unit,
    embeddings: List<UserEmbedUiModel>,
    trailingSlot: (@Composable FlowRowScope.() -> Unit)? = null
) {
    FlowRow(
        modifier = Modifier
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        embeddings.forEach {
            EmbeddingChip(
                onClick = onClear,
                text = it.text,
                isChecked = it.isChecked
            )
        }
        trailingSlot?.invoke(this)
    }
}

@Composable
fun ExtractorSuggestedEmbeddingChips(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    embeddings: List<UserEmbedUiModel>,
) {
    FlowRow(
        modifier = Modifier
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        embeddings.forEach {
            SuggestedEmbeddingChip(
                onClick = { onClick(it.text) },
                text = "# ${it.text}",
                isChecked = it.isChecked
            )
        }
    }
}

@Composable
private fun EmbeddingChip(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    text: String,
    isChecked: Boolean,
) {
    ExtractorChip(
        modifier = Modifier
            .then(modifier),
        isChecked = isChecked,
        text = text,
        onDismiss = { onClick(text) },
        trailingIcon = {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = "Localized description",
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        }
    )
}

@Composable
private fun SuggestedEmbeddingChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    isChecked: Boolean,
) {
    InputChip(
        modifier = modifier,
        selected = !isChecked,
        onClick = onClick,
        label = { Text(text) },
        border = InputChipDefaults.inputChipBorder(
            enabled = true,
            selected = isChecked
        ),
        shape = CircleShape,
        colors = ExtractorChipDefaults.chipColors()
    )
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                EmbeddingChip(onClick = {}, text = "sample", isChecked = false)
                EmbeddingChip(onClick = {}, text = "sample", isChecked = true)

                SuggestedEmbeddingChip(onClick = {}, text = "suggested", isChecked = false)
                SuggestedEmbeddingChip(onClick = {}, text = "suggested", isChecked = true)
            }
        }
    }
}