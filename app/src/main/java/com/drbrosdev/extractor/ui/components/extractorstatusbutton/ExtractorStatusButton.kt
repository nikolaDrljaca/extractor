package com.drbrosdev.extractor.ui.components.extractorstatusbutton

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.createExtractorBrush
import com.drbrosdev.extractor.util.thenIf


@Composable
fun ExtractorStatusButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: ExtractorStatusButtonState,
) {
    val brushModifier = when (state) {
        is ExtractorStatusButtonState.Idle -> Modifier
        is ExtractorStatusButtonState.ExtractionRunning -> Modifier.background(
            createExtractorBrush()
        )

        is ExtractorStatusButtonState.OutOfSync ->
            Modifier.background(color = MaterialTheme.colorScheme.error)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .thenIf(state !is ExtractorStatusButtonState.Idle) {
                clickable { onClick() }
            }
            .then(brushModifier)
            .size(40.dp)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is ExtractorStatusButtonState.Idle ->
                Box(modifier = Modifier.size(40.dp))

            is ExtractorStatusButtonState.ExtractionRunning ->
                Box(
                    modifier = Modifier.padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${state.donePercentage}%",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

            ExtractorStatusButtonState.OutOfSync -> Box(
                modifier = Modifier.padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExtractorStatusButton(
                onClick = { /*TODO*/ },
                state = ExtractorStatusButtonState.Idle
            )

            ExtractorStatusButton(
                onClick = { /*TODO*/ },
                state = ExtractorStatusButtonState.ExtractionRunning(34)
            )

            ExtractorStatusButton(
                onClick = { /*TODO*/ },
                state = ExtractorStatusButtonState.OutOfSync
            )
        }
    }
}