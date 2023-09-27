package com.drbrosdev.extractor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.createExtractorBrush

enum class ExtractorStatusButtonState {
    IDLE,
    WORKING
}

@Composable
fun ExtractorStatusButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: ExtractorStatusButtonState,
    donePercentage: Int?
) {
    val brush = createExtractorBrush()

    val brushModifier = if (donePercentage == null) {
        Modifier
    } else Modifier.background(brush)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .then(brushModifier)
            .size(40.dp)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            ExtractorStatusButtonState.IDLE ->
                Icon(
                    painter = painterResource(id = R.drawable.round_short_text_24),
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )

            ExtractorStatusButtonState.WORKING ->
                Box(
                    modifier = Modifier.padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$donePercentage%",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Column {
            ExtractorStatusButton(
                onClick = { /*TODO*/ },
                state = ExtractorStatusButtonState.IDLE,
                donePercentage = null
            )

            ExtractorStatusButton(
                onClick = { /*TODO*/ },
                state = ExtractorStatusButtonState.WORKING,
                donePercentage = 34
            )
        }
    }
}