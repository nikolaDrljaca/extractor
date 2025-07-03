package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StartWorkerCard(
    modifier: Modifier = Modifier,
    onStart: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "That's it!",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Lupa will start scanning your photos.",
            )
        }

        val size = ButtonDefaults.MediumContainerHeight
        Button(
            onClick = onStart,
            modifier = Modifier
                .heightIn(size)
                .fillMaxWidth(),
            contentPadding = ButtonDefaults.contentPaddingFor(size),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(text = "Let's Start!")
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            StartWorkerCard(
                onStart = {},
                modifier = Modifier.fillMaxHeight()
            )
        }
    }
}