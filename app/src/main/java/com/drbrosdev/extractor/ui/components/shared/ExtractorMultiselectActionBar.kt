package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


sealed interface MultiselectAction {
    data object Share : MultiselectAction

    data object CreateAlbum : MultiselectAction

    data object Cancel: MultiselectAction
}

@Composable
fun ExtractorMultiselectActionBar(
    onAction: (MultiselectAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.Black,
        modifier = Modifier.then(modifier)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            MultiselectActionBarItem(
                onClick = { onAction(MultiselectAction.Cancel) },
                painter = painterResource(id = R.drawable.rounded_close_24),
                text = stringResource(R.string.cancel)
            )

            MultiselectActionBarItem(
                onClick = { onAction(MultiselectAction.Share) },
                painter = painterResource(id = R.drawable.round_share_24),
                text = stringResource(id = R.string.bottom_bar_share)
            )

            MultiselectActionBarItem(
                onClick = { onAction(MultiselectAction.CreateAlbum) },
                painter = painterResource(id = R.drawable.rounded_add_24),
                text = stringResource(id = R.string.create_album)
            )
        }
    }
}

@Composable
private fun MultiselectActionBarItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
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
                tint = Color.White
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White
                )
            )
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorMultiselectActionBar(onAction = {})
    }
}