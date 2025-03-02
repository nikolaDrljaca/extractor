package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun SyncInProgressDisplay(
    modifier: Modifier = Modifier,
    progressCount: () -> Int,
    progress: () -> Float
) {
    Column(
        modifier = Modifier
            .then(modifier),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Recommendations are available after sync is complete.",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(Modifier.height(24.dp))
        Box(modifier = Modifier, contentAlignment = Alignment.Center) {

            Text(
                text = "${progressCount()}%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            CircularProgressIndicator(
                progress = progress,
                trackColor = Color.Transparent,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            )
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface {
            SyncInProgressDisplay(
                progressCount = { 12 },
                progress = { 0.5f }
            )
        }
    }
}