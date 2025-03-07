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
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.searchsheet.dateRange
import com.drbrosdev.extractor.ui.components.searchsheet.isRangeSelected
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview
import com.drbrosdev.extractor.util.asDisplayString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorDateFilter(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    onReset: () -> Unit,
    state: DateRangePickerState,
) {
    val dateRangeSelected by remember {
        derivedStateOf {
            state.isRangeSelected()
        }
    }
    val dateRange by remember {
        derivedStateOf {
            state.dateRange()
        }
    }

    val contentColor by animateColorAsState(
        targetValue = when {
            dateRangeSelected -> MaterialTheme.colorScheme.onPrimary
            else -> MaterialTheme.colorScheme.onSurface
        },
        label = ""
    )
    
    val cardColor by animateColorAsState(
        targetValue = when {
            dateRangeSelected -> MaterialTheme.colorScheme.primary
            else -> Color.Transparent
        },
        label = ""
    )

    val cardBorderColor by animateColorAsState(
        targetValue = when {
            dateRangeSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurface
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
                onClick = onClick,
                header = stringResource(R.string.start_date),
                modifier = Modifier.weight(1f),
                side = ExtractorDateFilterCardSide.LEFT,
                dateAsString = dateRange?.start.asDisplayString(stringResource(id = R.string.start_date)),
                containerColor = cardColor,
                borderColor = cardBorderColor,
                contentColor = contentColor,
                enabled = enabled
            )

            ExtractorDateFilterCard(
                onClick = onClick,
                header = stringResource(R.string.end_date),
                modifier = Modifier.weight(1f),
                side = ExtractorDateFilterCardSide.RIGHT,
                dateAsString = dateRange?.end.asDisplayString(stringResource(id = R.string.end_date)),
                containerColor = cardColor,
                borderColor = cardBorderColor,
                contentColor = contentColor,
                enabled = enabled
            )

            Spacer(modifier = Modifier.width(4.dp))

            Surface(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                onClick = onReset,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(IntrinsicSize.Max),
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
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


@OptIn(ExperimentalMaterial3Api::class)
@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface {
            Column {
                ExtractorDateFilter(
                    onClick = {},
                    onReset = {},
                    state = rememberDateRangePickerState(
                        initialSelectedStartDateMillis = System.currentTimeMillis(),
                        initialSelectedEndDateMillis = System.currentTimeMillis(),
                    )
                )
                Spacer(Modifier.height(12.dp))
                ExtractorDateFilter(
                    onClick = {},
                    onReset = {},
                    state = rememberDateRangePickerState()
                )
            }
        }
    }
}