package com.drbrosdev.extractor.ui.components.extractordatefilter

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import arrow.core.None
import arrow.core.Some
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.dialog.datepicker.ExtractorDatePicker
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorDateFilter(
    modifier: Modifier = Modifier,
    onDateChanged: () -> Unit,
    state: ExtractorDateFilterState = rememberExtractorDateFilterState()
) {
    when (state.selection) {
        ExtractorDateFilterSelection.START ->
            ExtractorDatePicker(
                onDismiss = state::clearSelection,
                onConfirm = {
                    state.updateStartDate(it)
                    onDateChanged()
                }
            )


        ExtractorDateFilterSelection.END ->
            ExtractorDatePicker(
                onDismiss = state::clearSelection,
                onConfirm = {
                    state.updateEndDate(it)
                    onDateChanged()
                }
            )

        ExtractorDateFilterSelection.NONE -> Unit
    }

    val startContentColor by animateColorAsState(
        targetValue = when (state.startDate) {
            None -> MaterialTheme.colorScheme.onPrimary
            is Some -> Color.White
        },
        label = ""
    )

    val endContentColor by animateColorAsState(
        targetValue = when (state.endDate) {
            None -> MaterialTheme.colorScheme.onPrimary
            is Some -> Color.White
        },
        label = ""
    )

    val startCardColor by animateColorAsState(
        targetValue = when (state.startDate) {
            None -> Color.Transparent
            is Some -> Color.Black
        },
        label = ""
    )

    val startCardBorderColor by animateColorAsState(
        targetValue = when (state.startDate) {
            None -> MaterialTheme.colorScheme.onPrimary
            is Some -> Color.Black
        },
        label = ""
    )

    val endCardColor by animateColorAsState(
        targetValue = when (state.endDate) {
            None -> Color.Transparent
            is Some -> Color.Black
        },
        label = ""
    )

    val endCardBorderColor by animateColorAsState(
        targetValue = when (state.endDate) {
            None -> MaterialTheme.colorScheme.onPrimary
            is Some -> Color.Black
        },
        label = ""
    )

    Column(
        modifier = Modifier
            .then(modifier),
    ) {
        Text(
            text = stringResource(R.string.date),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Normal
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ExtractorDateFilterCard(
                onClick = state::showStartSelection,
                header = stringResource(R.string.start_date),
                modifier = Modifier.weight(1f),
                side = ExtractorDateFilterCardSide.LEFT,
                dateAsString = state.startDate.getAsString(stringResource(id = R.string.start_date)),
                containerColor = startCardColor,
                borderColor = startCardBorderColor,
                contentColor = startContentColor,
                enabled = !state.disabled
            )

            ExtractorDateFilterCard(
                onClick = state::showEndSelection,
                header = stringResource(R.string.end_date),
                modifier = Modifier.weight(1f),
                side = ExtractorDateFilterCardSide.RIGHT,
                dateAsString = state.endDate.getAsString(stringResource(id = R.string.end_date)),
                containerColor = endCardColor,
                borderColor = endCardBorderColor,
                contentColor = endContentColor,
                enabled = !state.disabled
            )

            Spacer(modifier = Modifier.width(4.dp))

            Surface(
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                onClick = {
                    state.clearDates()
                    onDateChanged()
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(IntrinsicSize.Max),
                enabled = !state.disabled
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "Search Images",
                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

private enum class ExtractorDateFilterCardSide {
    LEFT, RIGHT
}

@Composable
private fun ExtractorDateFilterCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    header: String,
    dateAsString: String,
    side: ExtractorDateFilterCardSide,
    containerColor: Color = Color.Transparent,
    borderColor: Color = Color.White,
    enabled: Boolean = true,
    contentColor: Color
) {
    val shape = when (side) {
        ExtractorDateFilterCardSide.LEFT -> RoundedCornerShape(14.dp, 0.dp, 0.dp, 14.dp)
        ExtractorDateFilterCardSide.RIGHT -> RoundedCornerShape(0.dp, 14.dp, 14.dp, 0.dp)
    }

    val alignment = when (side) {
        ExtractorDateFilterCardSide.RIGHT -> Alignment.End
        ExtractorDateFilterCardSide.LEFT -> Alignment.Start
    }

    val actualBorderColor = when {
        enabled -> borderColor
        else -> Color.Gray
    }

    OutlinedCard(
        shape = shape,
        border = BorderStroke(width = 1.dp, color = actualBorderColor),
        modifier = Modifier.then(modifier),
        enabled = enabled,
        colors = CardDefaults.outlinedCardColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray,
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = alignment
        ) {
            Text(text = header, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dateAsString,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}


@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column {
            ExtractorDateFilter(
                state = ExtractorDateFilterState(
                    initStart = Some(1L)
                ),
                onDateChanged = {},
            )
        }
    }
}