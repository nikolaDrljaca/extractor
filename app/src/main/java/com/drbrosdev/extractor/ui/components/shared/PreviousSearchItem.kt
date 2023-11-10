package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchItemState
import com.drbrosdev.extractor.ui.components.previoussearch.drawableResource
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

enum class PreviousSearchItemViewType {
    FIRST,
    LAST,
    NEUTRAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviousSearchItem(
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    itemState: PreviousSearchItemState,
    viewType: () -> PreviousSearchItemViewType = { PreviousSearchItemViewType.NEUTRAL },
) {

    val cornerShape = when (viewType()) {
        PreviousSearchItemViewType.FIRST ->
            RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = 4.dp,
                bottomEnd = 4.dp
            )

        PreviousSearchItemViewType.LAST ->
            RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 4.dp,
                bottomStart = 14.dp,
                bottomEnd = 14.dp
            )

        PreviousSearchItemViewType.NEUTRAL ->
            RoundedCornerShape(4.dp)
    }

    Card(
        modifier = Modifier
            .then(modifier),
        onClick = onClick,
        shape = cornerShape
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = itemState.drawableResource()),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    space = 2.dp, alignment = Alignment.CenterVertically
                ),
            ) {
                Text(
                    text = itemState.text,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Result hits: ${itemState.count}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontStyle = FontStyle.Italic
                    )
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Rounded.Clear,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
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
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            PreviousSearchItem(
                onClick = { /*TODO*/ },
                onDelete = { /*TODO*/ },
                itemState = PreviousSearchItemState("someText", 12, LabelType.TEXT),
                viewType = { PreviousSearchItemViewType.FIRST }
            )

            PreviousSearchItem(
                onClick = { /*TODO*/ },
                onDelete = { /*TODO*/ },
                itemState = PreviousSearchItemState("someText", 12, LabelType.ALL)
            )

            PreviousSearchItem(
                onClick = { /*TODO*/ },
                onDelete = { /*TODO*/ },
                itemState = PreviousSearchItemState("someText", 12, LabelType.IMAGE),
                viewType = { PreviousSearchItemViewType.LAST }
            )
        }
    }
}