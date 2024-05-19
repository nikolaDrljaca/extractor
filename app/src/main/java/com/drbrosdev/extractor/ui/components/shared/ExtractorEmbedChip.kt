package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.imageinfo.UserEmbedUiModel
import com.drbrosdev.extractor.ui.imageinfo.VisualEmbedUiModel
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@OptIn(ExperimentalLayoutApi::class)
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
            ExtractorEmbeddingChip(
                onClick = onClear,
                text = it.text,
                isChecked = it.isChecked
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
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
            ExtractorEmbeddingChip(
                onClick = onClear,
                text = it.text,
                isChecked = it.isChecked
            )
        }
        trailingSlot?.invoke(this)
    }
}

@Composable
private fun ExtractorEmbeddingChip(
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


@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                ExtractorEmbeddingChip(onClick = {}, text = "sample", isChecked = false)
                ExtractorEmbeddingChip(onClick = {}, text = "sample", isChecked = true)
            }
        }
    }
}