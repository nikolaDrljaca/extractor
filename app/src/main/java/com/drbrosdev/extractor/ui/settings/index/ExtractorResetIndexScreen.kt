package com.drbrosdev.extractor.ui.settings.index

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.OutlinedExtractorActionButton

@Composable
fun ExtractorResetIndexScreen(
    onBack: () -> Unit,
    onResetIndex: () -> Unit,
    isLoading: Boolean
) {
    val textStyle = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Normal
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            // put content under top bar, not great
            Spacer(modifier = Modifier.height(120.dp))

            Text(
                text = stringResource(R.string.reset_index),
                style = MaterialTheme.typography.headlineMedium
            )

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.rounded_release_alert_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.this_is_a_destructive_action),
                    modifier = Modifier.weight(1f),
                    style = textStyle.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            }

            Text(
                text = stringResource(R.string.reset_index_expl),
                style = textStyle,
                modifier = Modifier.padding(
                    vertical = 8.dp
                )
            )

            OutlinedExtractorActionButton(
                onClick = onResetIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentColor = MaterialTheme.colorScheme.error
            ) {
                AnimatedContent(
                    targetState = isLoading,
                    label = "",
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) {
                    if (it) {
                        CircularProgressIndicator(
                            trackColor = Color.Transparent,
                            strokeCap = StrokeCap.Round,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(text = stringResource(R.string.delete_index_and_start_worker))
                    }
                }
            }

        }

        ExtractorTopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            leadingSlot = {
                BackIconButton(onBack = onBack)
            },
            trailingSlot = {
                Spacer(modifier = Modifier.width(12.dp))
            }
        )
    }
}
