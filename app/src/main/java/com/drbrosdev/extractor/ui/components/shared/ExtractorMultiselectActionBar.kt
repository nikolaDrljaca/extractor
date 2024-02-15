package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview


sealed interface MultiselectAction {
    data object Share : MultiselectAction

    data object CreateAlbum : MultiselectAction

    data object Cancel : MultiselectAction

    data object Delete : MultiselectAction
}

data class MultiselectActionItem(
    val resourceId: Int,
    val stringId: Int,
    val action: MultiselectAction
)

val searchGridActions = listOf(
    MultiselectActionItem(
        resourceId = R.drawable.rounded_close_24,
        stringId = R.string.cancel,
        action = MultiselectAction.Cancel
    ),
    MultiselectActionItem(
        resourceId = R.drawable.round_share_24,
        stringId = R.string.action_share,
        action = MultiselectAction.Share
    ),
    MultiselectActionItem(
        resourceId = R.drawable.rounded_add_24,
        stringId = R.string.create_album,
        action = MultiselectAction.CreateAlbum
    ),
)

val albumGridActions = listOf(
    MultiselectActionItem(
        resourceId = R.drawable.rounded_close_24,
        stringId = R.string.cancel,
        action = MultiselectAction.Cancel
    ),
    MultiselectActionItem(
        resourceId = R.drawable.round_share_24,
        stringId = R.string.action_share,
        action = MultiselectAction.Share
    ),
    MultiselectActionItem(
        resourceId = R.drawable.round_delete_24,
        stringId = R.string.action_delete,
        action = MultiselectAction.Share
    ),
    MultiselectActionItem(
        resourceId = R.drawable.rounded_add_24,
        stringId = R.string.create_album,
        action = MultiselectAction.CreateAlbum
    ),
)

@Composable
fun ExtractorMultiselectActionBar(
    onAction: (MultiselectAction) -> Unit,
    modifier: Modifier = Modifier,
    items: List<MultiselectActionItem> = searchGridActions,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        modifier = Modifier.then(modifier)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            items.forEach {
                MultiselectActionBarItem(
                    onClick = { onAction(it.action) },
                    painter = painterResource(id = it.resourceId),
                    text = stringResource(id = it.stringId)
                )
            }
        }
    }
}

@Composable
private fun MultiselectActionBarItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    painter: Painter,
    text: String
) {
    Surface(
        modifier = Modifier.then(modifier),
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.CenterVertically
            ),
            modifier = Modifier
                .padding(12.dp)
        ) {
            Icon(
                painter = painter,
                contentDescription = "",
                modifier = Modifier.requiredSize(32.dp),
                tint = contentColor
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = contentColor
                )
            )
        }
    }
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column {
            ExtractorMultiselectActionBar(onAction = {})
            Spacer(modifier = Modifier.height(12.dp))
            ExtractorMultiselectActionBar(onAction = {}, items = albumGridActions)
        }
    }
}