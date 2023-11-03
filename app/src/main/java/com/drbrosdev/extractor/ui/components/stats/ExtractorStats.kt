package com.drbrosdev.extractor.ui.components.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.usecase.LabelType
import com.drbrosdev.extractor.ui.components.shared.ExtractorImageLabelChip
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExtractorStats(
    onStatClick: (String, LabelType) -> Unit,
    modifier: Modifier = Modifier,
    state: ExtractorStatsUiState
) {
    val textStyle = MaterialTheme.typography.titleMedium

    Card(
        modifier = Modifier
            .defaultMinSize(minHeight = 144.dp)
            .then(modifier),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(text = "Most common Visual Embeds", style = textStyle)
            Text(
                text = "Try out some of these to start a search.",
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            when (state) {
                ExtractorStatsUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }

                is ExtractorStatsUiState.View -> {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        state.statEmbeds.forEach {
                            ExtractorImageLabelChip(
                                onDismiss = { onStatClick(it.value, it.type) },
                                text = it.value,
                                isChecked = false,
                                trailingIcon = {
                                    TrailingIcon(labelType = it.type)
                                }
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun TrailingIcon(
    modifier: Modifier = Modifier,
    labelType: LabelType
) {
    val resId = when (labelType) {
        LabelType.ALL -> R.drawable.round_tag_24
        LabelType.TEXT -> R.drawable.round_text_fields_24
        LabelType.IMAGE -> R.drawable.round_image_search_24
    }

    Icon(
        painter = painterResource(id = resId),
        contentDescription = "Localized description",
        Modifier
            .size(16.dp)
            .then(modifier)
    )
}

@Preview
@Composable
private fun CurrentPreview() {
    val embeds = buildList {
        repeat(7) { count -> add(StatEmbed("Embed #$count", LabelType.IMAGE)) }
    }

    ExtractorTheme {
        Column {
            ExtractorStats(
                onStatClick = { query, type -> },
                state = ExtractorStatsUiState.View(embeds)
            )

            ExtractorStats(
                onStatClick = { query, type -> },
                state = ExtractorStatsUiState.Loading
            )
        }
    }
}
